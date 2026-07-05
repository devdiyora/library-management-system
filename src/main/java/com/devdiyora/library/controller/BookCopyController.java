package com.devdiyora.library.controller;

import com.devdiyora.library.dto.request.BookCopyRequest;
import com.devdiyora.library.dto.request.UpdateBookCopyStatusRequest;
import com.devdiyora.library.dto.response.BookCopyResponse;
import com.devdiyora.library.service.BookCopyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book-copies")
@RequiredArgsConstructor
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping
    public ResponseEntity<BookCopyResponse> addBookCopy(
            @Valid @RequestBody BookCopyRequest request) {

        BookCopyResponse response = bookCopyService.addBookCopy(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<BookCopyResponse>> getAllBookCopies() {

        return ResponseEntity.ok(
                bookCopyService.getAllBookCopies()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<BookCopyResponse> getBookCopyById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookCopyService.getBookCopyById(id)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<BookCopyResponse> getBookCopyByBarcode(
            @PathVariable String barcode) {

        return ResponseEntity.ok(
                bookCopyService.getBookCopyByBarcode(barcode)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookCopyResponse> updateBookCopy(
            @PathVariable Long id,
            @Valid @RequestBody BookCopyRequest request) {

        return ResponseEntity.ok(
                bookCopyService.updateBookCopy(id, request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookCopy(
            @PathVariable Long id) {

        bookCopyService.deleteBookCopy(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookCopyResponse> updateBookCopyStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookCopyStatusRequest request) {

        return ResponseEntity.ok(
                bookCopyService.updateBookCopyStatus(id, request)
        );
    }
}