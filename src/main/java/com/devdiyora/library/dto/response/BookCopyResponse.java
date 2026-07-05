package com.devdiyora.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookCopyResponse {

    private Long id;

    private String barcode;

    private String status;

    private Long bookId;

    private String title;

    private String isbn;

}