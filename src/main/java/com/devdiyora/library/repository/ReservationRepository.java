package com.devdiyora.library.repository;

import com.devdiyora.library.entity.Reservation;
import com.devdiyora.library.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByBookIdAndStatusOrderByReservationDateAsc(
            Long bookId,
            ReservationStatus status
    );

    boolean existsByMemberIdAndBookIdAndStatus(
            Long memberId,
            Long bookId,
            ReservationStatus status
    );

    Optional<Reservation> findFirstByBookIdAndStatusOrderByReservationDateAsc(
            Long bookId,
            ReservationStatus status
    );

    long countByStatus(ReservationStatus status);
}