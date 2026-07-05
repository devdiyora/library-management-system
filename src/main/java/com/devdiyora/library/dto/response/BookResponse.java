package com.devdiyora.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookResponse {

    private Long id;

    private String title;

    private String isbn;

    private String authorName;

    private String publisherName;

    private String language;

    private Integer publicationYear;

    private String edition;

    private String category;
}