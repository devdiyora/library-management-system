package com.devdiyora.library.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 20)
    private String isbn;

    @NotBlank
    @Size(max = 100)
    private String authorName;

    @NotBlank
    @Size(max = 100)
    private String publisherName;

    @NotBlank
    @Size(max = 50)
    private String language;

    @NotNull
    @Min(1000)
    @Max(2026)
    private Integer publicationYear;

    @NotBlank
    @Size(max = 30)
    private String edition;

    @NotNull
    private Long categoryId;
}