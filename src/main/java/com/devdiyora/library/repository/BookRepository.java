package com.devdiyora.library.repository;

import com.devdiyora.library.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Query("""
        SELECT b
        FROM Book b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(b.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    List<Book> searchBooks(@Param("keyword") String keyword);
    boolean existsByCategoryId(Long categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       SELECT b
       FROM Book b
       WHERE b.id = :id
       """)
    Optional<Book> findByIdForUpdate(@Param("id") Long id);
}