package com.devdiyora.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueBookRequest {

    @NotNull
    private Long memberId;

    @NotBlank
    private String barcode;

}