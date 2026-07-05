package com.devdiyora.library.util;

import com.devdiyora.library.exception.BusinessException;

import java.util.List;


public final class SortFieldValidator {

    private SortFieldValidator() {
    }

    public static void validate(String sortBy, List<String> allowedFields) {

        if (!allowedFields.contains(sortBy)) {
            throw new BusinessException(
                    "Invalid sort field: " + sortBy
            );
        }
    }
}
