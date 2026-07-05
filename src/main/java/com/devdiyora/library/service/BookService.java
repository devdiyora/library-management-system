package com.devdiyora.library.service;

import com.devdiyora.library.dto.request.BookRequest;
import com.devdiyora.library.dto.response.BookResponse;
import com.devdiyora.library.dto.response.PageResponse;

import java.util.List;

public interface BookService {

    BookResponse addBook(BookRequest request);

    PageResponse<BookResponse> getAllBooks(
            int page,
            int size,
            String sortBy,
            String direction
    );
    BookResponse getBookById(Long id);

    BookResponse updateBook(Long id, BookRequest request);

    void deleteBook(Long id);

    List<BookResponse> searchBooks(String keyword);
}