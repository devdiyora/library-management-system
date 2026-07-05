package com.devdiyora.library.entity;

import com.devdiyora.library.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDate reservationDate;

    private LocalDate fulfilledDate;

    public void reserve(User member, Book book) {

        this.member = member;
        this.book = book;
        this.status = ReservationStatus.PENDING;
        this.reservationDate = LocalDate.now();
    }

    public void fulfill() {

        this.status = ReservationStatus.FULFILLED;
        this.fulfilledDate = LocalDate.now();
    }

    public void cancel() {

        this.status = ReservationStatus.CANCELLED;
    }
}