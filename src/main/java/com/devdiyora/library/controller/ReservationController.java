package com.devdiyora.library.controller;

import com.devdiyora.library.dto.request.ReserveBookRequest;
import com.devdiyora.library.dto.response.ReservationResponse;
import com.devdiyora.library.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<ReservationResponse> reserveBook(
            @Valid @RequestBody ReserveBookRequest request) {

        ReservationResponse response =
                reservationService.reserveBook(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/my-reservations")
    public ResponseEntity<List<ReservationResponse>>
    getMyReservations() {

        return ResponseEntity.ok(
                reservationService.getMyReservations()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>>
    getAllReservations() {

        return ResponseEntity.ok(
                reservationService.getAllReservations()
        );
    }

    @PreAuthorize("hasAnyRole('MEMBER','ADMIN','LIBRARIAN')")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse>
    cancelReservation(
            @PathVariable Long reservationId) {

        return ResponseEntity.ok(
                reservationService.cancelReservation(
                        reservationId
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping("/pending")
    public ResponseEntity<List<ReservationResponse>>
    getPendingReservations() {

        return ResponseEntity.ok(
                reservationService.getPendingReservations()
        );
    }
}