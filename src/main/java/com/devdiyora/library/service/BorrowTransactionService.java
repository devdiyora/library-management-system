package com.devdiyora.library.service;

import com.devdiyora.library.dto.request.IssueBookRequest;
import com.devdiyora.library.dto.request.ReturnBookRequest;
import com.devdiyora.library.dto.response.BorrowTransactionResponse;

import java.util.List;

public interface BorrowTransactionService {

    BorrowTransactionResponse issueBook(IssueBookRequest request);

    BorrowTransactionResponse returnBook(ReturnBookRequest request);

    List<BorrowTransactionResponse> getBorrowHistory(Long memberId);

    List<BorrowTransactionResponse> getActiveBorrowedBooks(Long memberId);

    List<BorrowTransactionResponse> getAllBorrowTransactions();

    BorrowTransactionResponse getBorrowTransactionById(Long id);

    List<BorrowTransactionResponse> getMyBorrowHistory();

    List<BorrowTransactionResponse> getMyActiveBorrowedBooks();
}