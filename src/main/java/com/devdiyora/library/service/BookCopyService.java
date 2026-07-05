package com.devdiyora.library.service;

import com.devdiyora.library.dto.request.BookCopyRequest;
import com.devdiyora.library.dto.response.BookCopyResponse;

import java.util.List;

public interface BookCopyService {

    BookCopyResponse addBookCopy(BookCopyRequest request);

    List<BookCopyResponse> getAllBookCopies();

    BookCopyResponse getBookCopyById(Long id);

    BookCopyResponse getBookCopyByBarcode(String barcode);

    BookCopyResponse updateBookCopy(Long id,
                                    BookCopyRequest request);

    void deleteBookCopy(Long id);

}