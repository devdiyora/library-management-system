package com.devdiyora.library.service;

import com.devdiyora.library.dto.request.CategoryRequest;
import com.devdiyora.library.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(Long id,
                                    CategoryRequest request);

    void deleteCategory(Long id);
}