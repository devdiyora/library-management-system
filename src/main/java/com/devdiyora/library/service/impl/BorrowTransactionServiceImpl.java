package com.devdiyora.library.service.impl;

import com.devdiyora.library.dto.request.IssueBookRequest;
import com.devdiyora.library.dto.request.ReturnBookRequest;
import com.devdiyora.library.dto.response.BorrowTransactionResponse;
import com.devdiyora.library.entity.BookCopy;
import com.devdiyora.library.entity.BorrowTransaction;
import com.devdiyora.library.entity.Reservation;
import com.devdiyora.library.entity.User;
import com.devdiyora.library.enums.BorrowStatus;
import com.devdiyora.library.enums.ReservationStatus;
import com.devdiyora.library.exception.BusinessException;
import com.devdiyora.library.exception.ResourceNotFoundException;
import com.devdiyora.library.repository.*;
import com.devdiyora.library.service.BorrowTransactionService;
import com.devdiyora.library.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BorrowTransactionServiceImpl implements BorrowTransactionService {

    private static final int MAX_BORROW_LIMIT = 5;
    private static final int FINE_PER_DAY = 10;

    private final BorrowTransactionRepository borrowTransactionRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final CurrentUserProvider currentUserProvider;

    private BorrowTransactionResponse mapToResponse(BorrowTransaction transaction) {

        return new BorrowTransactionResponse(
                transaction.getId(),
                transaction.getMember().getFirstName() + " " + transaction.getMember().getLastName(),
                transaction.getBookCopy().getBook().getTitle(),
                transaction.getBookCopy().getBarcode(),
                transaction.getIssueDate(),
                transaction.getDueDate(),
                transaction.getReturnDate(),
                transaction.getStatus().name(),
                transaction.getFineAmount()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowTransactionResponse> getMyBorrowHistory() {

        User currentUser = currentUserProvider.getCurrentUser();

        return borrowTransactionRepository.findByMemberId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowTransactionResponse> getMyActiveBorrowedBooks() {

        User currentUser = currentUserProvider.getCurrentUser();

        return borrowTransactionRepository.findByMemberIdAndStatus(
                        currentUser.getId(),
                        BorrowStatus.BORROWED
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BorrowTransactionResponse issueBook(IssueBookRequest request) {

        User member = userRepository.findByIdForUpdate(request.getMemberId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found."));

        if (!member.hasRole("MEMBER")) {
            throw new BusinessException("Selected user is not a member.");
        }

        long activeBorrowCount =
                borrowTransactionRepository.countByMemberIdAndStatus(
                        member.getId(),
                        BorrowStatus.BORROWED
                );

        if (activeBorrowCount >= MAX_BORROW_LIMIT) {
            throw new BusinessException(
                    "Maximum borrow limit reached."
            );
        }

        List<BorrowTransaction> activeBorrowTransactions =
                borrowTransactionRepository.findByMemberIdAndStatus(
                        member.getId(),
                        BorrowStatus.BORROWED
                );

        boolean hasOverdueBook = activeBorrowTransactions.stream()
                .anyMatch(transaction -> transaction.getDueDate().isBefore(LocalDate.now()));

        if (hasOverdueBook) {
            throw new BusinessException(
                    "Member has overdue books. Return them first."
            );
        }

        BookCopy bookCopy = bookCopyRepository
                .findByBarcodeForUpdate(request.getBarcode())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book copy not found."));

        if (!bookCopy.isAvailable()) {
            throw new BusinessException(
                    "Book copy is not available."
            );
        }

        User librarian = currentUserProvider.getCurrentUser();

        BorrowTransaction transaction = new BorrowTransaction();

        transaction.issue(
                member,
                librarian,
                bookCopy
        );

        bookCopy.markAsBorrowed();

        borrowTransactionRepository.save(transaction);
        bookCopyRepository.save(bookCopy);

        return mapToResponse(transaction);
    }

    @Override
    public BorrowTransactionResponse returnBook(ReturnBookRequest request) {

        BookCopy bookCopy = bookCopyRepository.findByBarcodeForUpdate(request.getBarcode())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book copy not found."));

        BorrowTransaction transaction =
                borrowTransactionRepository.findByBookCopyIdAndStatus(
                                bookCopy.getId(),
                                BorrowStatus.BORROWED
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Active borrow transaction not found."
                                ));

        User librarian = currentUserProvider.getCurrentUser();

        transaction.returnBook(librarian);

        if (transaction.getDueDate().isBefore(LocalDate.now())) {

            long lateDays = ChronoUnit.DAYS.between(
                    transaction.getDueDate(),
                    LocalDate.now()
            );

            transaction.setFineAmount(
                    BigDecimal.valueOf(lateDays * FINE_PER_DAY)
            );
        }

        bookCopy.markAsAvailable();

        Reservation reservation =
                reservationRepository
                        .findFirstByBookIdAndStatusOrderByReservationDateAsc(
                                bookCopy.getBook().getId(),
                                ReservationStatus.PENDING
                        )
                        .orElse(null);

        if (reservation != null) {

            reservation.fulfill();

            reservationRepository.save(reservation);
        }

        borrowTransactionRepository.save(transaction);
        bookCopyRepository.save(bookCopy);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowTransactionResponse> getBorrowHistory(Long memberId) {

        return borrowTransactionRepository.findByMemberId(memberId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowTransactionResponse> getActiveBorrowedBooks(Long memberId) {

        return borrowTransactionRepository.findByMemberIdAndStatus(
                        memberId,
                        BorrowStatus.BORROWED
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowTransactionResponse> getAllBorrowTransactions() {

        return borrowTransactionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowTransactionResponse getBorrowTransactionById(Long id) {

        BorrowTransaction transaction =
                borrowTransactionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Borrow transaction not found."
                                ));

        return mapToResponse(transaction);
    }

}
