package com.devdiyora.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private long totalBooks;

    private long totalBookCopies;

    private long availableBookCopies;

    private long borrowedBookCopies;

    private long totalMembers;

    private long activeBorrowTransactions;

    private long pendingReservations;
}