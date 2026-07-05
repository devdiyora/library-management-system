package com.devdiyora.library.repository;

import com.devdiyora.library.entity.BookCopy;
import com.devdiyora.library.enums.BookCopyStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    Optional<BookCopy> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    boolean existsByBookIdAndStatus(
            Long bookId,
            BookCopyStatus status
    );

    long countByStatus(BookCopyStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       SELECT b
       FROM BookCopy b
       WHERE b.barcode = :barcode
       """)
    Optional<BookCopy> findByBarcodeForUpdate(@Param("barcode") String barcode);

}