package com.devdiyora.library.controller;

import com.devdiyora.library.dto.request.IssueBookRequest;
import com.devdiyora.library.dto.request.ReturnBookRequest;
import com.devdiyora.library.dto.response.BorrowTransactionResponse;
import com.devdiyora.library.service.BorrowTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow-transactions")
@RequiredArgsConstructor
public class BorrowTransactionController {

    private final BorrowTransactionService borrowTransactionService;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("/issue")
    public ResponseEntity<BorrowTransactionResponse> issueBook(
            @Valid @RequestBody IssueBookRequest request) {

        BorrowTransactionResponse response =
                borrowTransactionService.issueBook(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("/return")
    public ResponseEntity<BorrowTransactionResponse> returnBook(
            @Valid @RequestBody ReturnBookRequest request) {

        return ResponseEntity.ok(
                borrowTransactionService.returnBook(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping
    public ResponseEntity<List<BorrowTransactionResponse>>
    getAllBorrowTransactions() {

        return ResponseEntity.ok(
                borrowTransactionService.getAllBorrowTransactions()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping("/{id}")
    public ResponseEntity<BorrowTransactionResponse>
    getBorrowTransactionById(@PathVariable Long id) {

        return ResponseEntity.ok(
                borrowTransactionService.getBorrowTransactionById(id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping("/history/{memberId}")
    public ResponseEntity<List<BorrowTransactionResponse>>
    getBorrowHistory(@PathVariable Long memberId) {

        return ResponseEntity.ok(
                borrowTransactionService.getBorrowHistory(memberId)
        );
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/my-history")
    public ResponseEntity<List<BorrowTransactionResponse>> getMyBorrowHistory() {

        return ResponseEntity.ok(
                borrowTransactionService.getMyBorrowHistory()
        );
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/my-active-books")
    public ResponseEntity<List<BorrowTransactionResponse>>
    getMyActiveBorrowedBooks() {

        return ResponseEntity.ok(
                borrowTransactionService.getMyActiveBorrowedBooks()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping("/active/{memberId}")
    public ResponseEntity<List<BorrowTransactionResponse>>
    getActiveBorrowedBooks(@PathVariable Long memberId) {

        return ResponseEntity.ok(
                borrowTransactionService.getActiveBorrowedBooks(memberId)
        );
    }
}
