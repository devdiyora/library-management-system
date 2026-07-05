package com.devdiyora.library.service.impl;

import com.devdiyora.library.dto.request.BookCopyRequest;
import com.devdiyora.library.dto.response.BookCopyResponse;
import com.devdiyora.library.entity.Book;
import com.devdiyora.library.entity.BookCopy;
import com.devdiyora.library.enums.BookCopyStatus;
import com.devdiyora.library.exception.BusinessException;
import com.devdiyora.library.exception.DuplicateResourceException;
import com.devdiyora.library.exception.ResourceNotFoundException;
import com.devdiyora.library.repository.BookCopyRepository;
import com.devdiyora.library.repository.BookRepository;
import com.devdiyora.library.service.BookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookCopyResponse addBookCopy(BookCopyRequest request) {

        if (bookCopyRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateResourceException("Barcode already exists.");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found."));

        BookCopy bookCopy = new BookCopy();

        bookCopy.setBarcode(request.getBarcode());
        bookCopy.setBook(book);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        return mapToResponse(savedBookCopy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponse> getAllBookCopies() {

        return bookCopyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookCopyResponse getBookCopyById(Long id) {

        BookCopy bookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book copy not found."));

        return mapToResponse(bookCopy);
    }

    @Override
    @Transactional(readOnly = true)
    public BookCopyResponse getBookCopyByBarcode(String barcode) {

        BookCopy bookCopy = bookCopyRepository.findByBarcode(barcode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book copy not found."));

        return mapToResponse(bookCopy);
    }

    @Override
    @Transactional
    public BookCopyResponse updateBookCopy(Long id,
                                           BookCopyRequest request) {

        BookCopy bookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book copy not found."));

        if (!bookCopy.getBarcode().equals(request.getBarcode())
                && bookCopyRepository.existsByBarcode(request.getBarcode())) {

            throw new DuplicateResourceException("Barcode already exists.");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found."));

        bookCopy.setBarcode(request.getBarcode());
        bookCopy.setBook(book);

        BookCopy updatedBookCopy = bookCopyRepository.save(bookCopy);

        return mapToResponse(updatedBookCopy);
    }

    @Override
    @Transactional
    public void deleteBookCopy(Long id) {

        BookCopy bookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book copy not found."));

        if (bookCopy.getStatus() == BookCopyStatus.BORROWED) {
            throw new BusinessException(
                    "Cannot delete a copy that is currently borrowed."
            );
        }

        bookCopyRepository.delete(bookCopy);
    }
    private BookCopyResponse mapToResponse(BookCopy bookCopy) {

        return new BookCopyResponse(
                bookCopy.getId(),
                bookCopy.getBarcode(),
                bookCopy.getStatus().name(),
                bookCopy.getBook().getId(),
                bookCopy.getBook().getTitle(),
                bookCopy.getBook().getIsbn()
        );
    }
}