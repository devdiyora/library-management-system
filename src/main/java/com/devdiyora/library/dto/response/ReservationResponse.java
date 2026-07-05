package com.devdiyora.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReservationResponse {

    private Long reservationId;

    private String memberName;

    private String bookTitle;

    private String status;

    private LocalDate reservationDate;

    private LocalDate fulfilledDate;
}