package com.taskmonitor.module.task.service;

import com.taskmonitor.common.exception.BusinessException;
import com.taskmonitor.common.exception.ErrorCode;
import com.taskmonitor.common.exception.ResourceNotFoundException;
import com.taskmonitor.module.task.domain.Category;
import com.taskmonitor.module.task.dto.CategoryRequest;
import com.taskmonitor.module.task.dto.CategoryResponse;
import com.taskmonitor.module.task.repository.CategoryRepository;
import com.taskmonitor.module.user.domain.User;
import com.taskmonitor.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(UUID userId) {
        return categoryRepository.findAllByUserIdOrSystem(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(UUID userId, CategoryRequest request) {
        if (categoryRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Category with this name already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Category category = Category.builder()
                .user(user)
                .name(request.getName().trim())
                .colorHex(request.getColorHex().trim())
                .icon(request.getIcon() != null ? request.getIcon().trim() : "folder")
                .isSystem(false)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Created category '{}' for user {}", saved.getName(), userId);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteCategory(UUID userId, UUID categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (category.isSystem()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "System categories cannot be deleted");
        }

        categoryRepository.delete(category);
        log.info("Deleted category {} for user {}", categoryId, userId);
    }

    public CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .colorHex(category.getColorHex())
                .icon(category.getIcon())
                .isSystem(category.isSystem())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
