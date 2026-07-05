package com.devdiyora.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnBookRequest {

    @NotBlank
    private String barcode;

}