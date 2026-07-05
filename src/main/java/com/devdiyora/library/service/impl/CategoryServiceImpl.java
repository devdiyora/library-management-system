package com.devdiyora.library.service.impl;

import com.devdiyora.library.dto.request.CategoryRequest;
import com.devdiyora.library.dto.response.CategoryResponse;
import com.devdiyora.library.entity.Category;
import com.devdiyora.library.exception.BusinessException;
import com.devdiyora.library.exception.ResourceNotFoundException;
import com.devdiyora.library.repository.BookRepository;
import com.devdiyora.library.repository.CategoryRepository;
import com.devdiyora.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    private Category getCategory(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found."
                        ));
    }

    private CategoryResponse mapToResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
    private void validateDuplicateCategory(String name) {

        if (categoryRepository.existsByName(name)) {

            throw new BusinessException(
                    "Category already exists."
            );
        }
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        validateDuplicateCategory(request.getName());

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory =
                categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category = getCategory(id);

        return mapToResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id,
                                           CategoryRequest request) {

        Category category = getCategory(id);

        if (!category.getName().equalsIgnoreCase(request.getName())) {
            validateDuplicateCategory(request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory =
                categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }
    @Override
    public void deleteCategory(Long id) {

        Category category = getCategory(id);

        if (bookRepository.existsByCategoryId(category.getId())) {

            throw new BusinessException(
                    "Cannot delete category because books are assigned to it."
            );
        }

        categoryRepository.delete(category);
    }
}