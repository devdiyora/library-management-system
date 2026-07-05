package com.devdiyora.library.service.impl;

import com.devdiyora.library.dto.request.BookRequest;
import com.devdiyora.library.dto.response.BookResponse;
import com.devdiyora.library.dto.response.PageResponse;
import com.devdiyora.library.entity.Book;
import com.devdiyora.library.entity.Category;
import com.devdiyora.library.enums.BookCopyStatus;
import com.devdiyora.library.exception.BusinessException;
import com.devdiyora.library.exception.DuplicateResourceException;
import com.devdiyora.library.exception.ResourceNotFoundException;
import com.devdiyora.library.repository.BookCopyRepository;
import com.devdiyora.library.repository.BookRepository;
import com.devdiyora.library.repository.CategoryRepository;
import com.devdiyora.library.service.BookService;
import com.devdiyora.library.util.SortFieldValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCopyRepository bookCopyRepository;

    @Override
    public BookResponse addBook(BookRequest request) {

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book with this ISBN already exists.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        Book book = new Book();

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setAuthorName(request.getAuthorName());
        book.setPublisherName(request.getPublisherName());
        book.setLanguage(request.getLanguage());
        book.setPublicationYear(request.getPublicationYear());
        book.setEdition(request.getEdition());
        book.setCategory(category);

        Book savedBook = bookRepository.save(book);

        return mapToResponse(savedBook);
    }


    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "title",
            "authorName",
            "isbn",
            "publicationYear"
    );

    @Override
    public PageResponse<BookResponse> getAllBooks(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        SortFieldValidator.validate(sortBy, ALLOWED_SORT_FIELDS);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        Page<Book> bookPage =
                bookRepository.findAll(pageable);

        List<BookResponse> responses =
                bookPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return new PageResponse<>(
                responses,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        );
    }
    @Override
    public BookResponse getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found."));

        return mapToResponse(book);
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found."));

        if (!book.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {

            throw new DuplicateResourceException("Book with this ISBN already exists.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setAuthorName(request.getAuthorName());
        book.setPublisherName(request.getPublisherName());
        book.setLanguage(request.getLanguage());
        book.setPublicationYear(request.getPublicationYear());
        book.setEdition(request.getEdition());
        book.setCategory(category);

        Book updatedBook = bookRepository.save(book);

        return mapToResponse(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found."));

        if (bookCopyRepository.existsByBookIdAndStatus(
                book.getId(), BookCopyStatus.BORROWED)) {

            throw new BusinessException(
                    "Cannot delete book: some copies are currently borrowed."
            );
        }

        bookRepository.delete(book);
    }

    @Override
    public List<BookResponse> searchBooks(String keyword) {

        return bookRepository.searchBooks(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    private BookResponse mapToResponse(Book book) {

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAuthorName(),
                book.getPublisherName(),
                book.getLanguage(),
                book.getPublicationYear(),
                book.getEdition(),
                book.getCategory().getName()
        );
    }
}