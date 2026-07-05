package com.devdiyora.library.service;

import com.devdiyora.library.dto.request.ReserveBookRequest;
import com.devdiyora.library.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse reserveBook(ReserveBookRequest request);

    List<ReservationResponse> getMyReservations();

    List<ReservationResponse> getAllReservations();

    ReservationResponse cancelReservation(Long reservationId);

    List<ReservationResponse> getPendingReservations();
}