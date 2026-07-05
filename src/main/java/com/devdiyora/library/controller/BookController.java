package com.devdiyora.library.controller;

import com.devdiyora.library.dto.request.BookRequest;
import com.devdiyora.library.dto.response.BookResponse;
import com.devdiyora.library.dto.response.PageResponse;
import com.devdiyora.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping
    public ResponseEntity<BookResponse> addBook(
            @Valid @RequestBody BookRequest request) {

        BookResponse response = bookService.addBook(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "title") String sortBy,

            @RequestParam(defaultValue = "asc") String direction
    ) {

        return ResponseEntity.ok(

                bookService.getAllBooks(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookService.getBookById(id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {

        return ResponseEntity.ok(
                bookService.updateBook(id, request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id) {

        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                bookService.searchBooks(keyword)
        );
    }
}