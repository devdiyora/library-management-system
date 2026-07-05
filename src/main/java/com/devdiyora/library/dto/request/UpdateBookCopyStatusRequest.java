package com.devdiyora.library.dto.request;

import com.devdiyora.library.enums.BookCopyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookCopyStatusRequest {

    @NotNull
    private BookCopyStatus status;

}
