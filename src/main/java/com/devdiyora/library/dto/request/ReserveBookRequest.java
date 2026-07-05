package com.devdiyora.library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReserveBookRequest {

    @NotNull
    private Long bookId;
}