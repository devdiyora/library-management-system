package com.devdiyora.library.service.impl;

import com.devdiyora.library.dto.request.ReserveBookRequest;
import com.devdiyora.library.dto.response.ReservationResponse;
import com.devdiyora.library.entity.Book;
import com.devdiyora.library.entity.Reservation;
import com.devdiyora.library.entity.User;
import com.devdiyora.library.enums.BookCopyStatus;
import com.devdiyora.library.enums.BorrowStatus;
import com.devdiyora.library.enums.ReservationStatus;
import com.devdiyora.library.exception.BusinessException;
import com.devdiyora.library.exception.ResourceNotFoundException;
import com.devdiyora.library.repository.*;
import com.devdiyora.library.service.ReservationService;
import com.devdiyora.library.util.CurrentUserProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowTransactionRepository borrowTransactionRepository;
    private final CurrentUserProvider currentUserProvider;

    private ReservationResponse mapToResponse(
            Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getMember().getFirstName() + " "
                        + reservation.getMember().getLastName(),
                reservation.getBook().getTitle(),
                reservation.getStatus().name(),
                reservation.getReservationDate(),
                reservation.getFulfilledDate()
        );
    }

    private Book getBook(Long bookId) {

        return bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found."
                        ));
    }
    private void validateMember(User member) {

        if (!member.hasRole("MEMBER")) {
            throw new BusinessException(
                    "Only members can reserve books."
            );
        }
    }
    private void validateBookAvailability(Book book) {

        if (bookCopyRepository.existsByBookIdAndStatus(
                book.getId(),
                BookCopyStatus.AVAILABLE
        )) {

            throw new BusinessException(
                    "Book is currently available. Please borrow it instead."
            );
        }
    }

    private void validateDuplicateReservation(
            User member,
            Book book
    ) {

        boolean exists =
                reservationRepository.existsByMemberIdAndBookIdAndStatus(
                        member.getId(),
                        book.getId(),
                        ReservationStatus.PENDING
                );

        if (exists) {
            throw new BusinessException(
                    "You have already reserved this book."
            );
        }
    }
    private void validateAlreadyBorrowed(
            User member,
            Book book
    ) {

        boolean borrowed =
                borrowTransactionRepository
                        .findActiveBorrowByMemberAndBook(
                                member.getId(),
                                book.getId(),
                                BorrowStatus.BORROWED
                        )
                        .isPresent();

        if (borrowed) {

            throw new BusinessException(
                    "You have already borrowed this book."
            );
        }
    }
    @Override
    public ReservationResponse reserveBook(ReserveBookRequest request) {

        User member = currentUserProvider.getCurrentUser();

        validateMember(member);

        Book book = getBook(request.getBookId());

        validateBookAvailability(book);

        validateDuplicateReservation(member, book);

        validateAlreadyBorrowed(member, book);

        Reservation reservation = new Reservation();

        reservation.reserve(member, book);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    public List<ReservationResponse> getMyReservations() {

        User member = currentUserProvider.getCurrentUser();

        return reservationRepository.findByMemberId(member.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> getAllReservations() {

        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> getPendingReservations() {

        return reservationRepository.findByStatus(ReservationStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ReservationResponse cancelReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reservation not found."));

        User currentUser = currentUserProvider.getCurrentUser();

        if (!currentUser.hasRole("ADMIN") && !currentUser.hasRole("LIBRARIAN")) {

            if (!reservation.getMember().getId().equals(currentUser.getId())) {
                throw new BusinessException(
                        "You can cancel only your own reservation."
                );
            }
        }

        reservation.cancel();

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return mapToResponse(updatedReservation);
    }
}
