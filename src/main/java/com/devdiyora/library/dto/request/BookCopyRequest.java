package com.devdiyora.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCopyRequest {

    @NotNull
    private Long bookId;

    @NotBlank
    private String barcode;

}