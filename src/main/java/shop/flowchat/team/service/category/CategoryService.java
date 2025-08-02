package shop.flowchat.team.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.presentation.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.presentation.dto.category.request.CategoryMoveRequest;
import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.domain.team.Team;
import shop.flowchat.team.common.exception.custom.AuthorizationException;
import shop.flowchat.team.common.exception.custom.EntityNotFoundException;
import shop.flowchat.team.infrastructure.repository.category.CategoryRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(CategoryCreateRequest request, Team team) {
        Category category = Category.from(request, team);
        Double maxPosition = categoryRepository.findMaxPositionByTeamId(team.getId());
        category.movePositionBetween(maxPosition, maxPosition + 2000.0);
        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("createCategory - 입력값이 잘못되었습니다.");
        }
        return category;
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리입니다."));
    }

    @Transactional(readOnly = true)
    public Category getAndValidateTeamCategory(UUID teamId, Long categoryId) {
        Category category = getCategoryById(categoryId);
        if (!category.getTeam().getId().equals(teamId)) {
            throw new AuthorizationException("카테고리의 팀 ID가 올바르지 않습니다.");
        }
        return category;
    }

    @Transactional(readOnly = true)
    public List<Category> validateTeamCategories(UUID teamId, List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findByIdIn(categoryIds);
        if(categories.size() != categoryIds.size()) {
            throw new AuthorizationException("카테고리가 올바르지 않습니다.");
        }
        if(categories.stream().anyMatch(category -> !category.getTeam().getId().equals(teamId))) {
            throw new AuthorizationException("카테고리의 팀 ID가 올바르지 않습니다.");
        }
        return categories;
    }

    @Transactional
    public void deleteAllCategoriesByTeam(Team team) {
        categoryRepository.deleteByTeam(team);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoryByTeam(Team team) {
        return categoryRepository.findByTeam(team);
    }

    @Transactional
    public void moveCategory(Long categoryId, List<Category> categories, CategoryMoveRequest request) {
        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        Category movingCategory = categoryMap.get(categoryId);
        Category prevCategory = categoryMap.get(request.prevCategoryId());
        Category nextCategory = categoryMap.get(request.nextCategoryId());

        if (request.nextCategoryId() == 0) {
            movingCategory.movePositionBetween(prevCategory.getPosition(), prevCategory.getPosition() + 2000.0);
        } else if (request.prevCategoryId() == 0) {
            movingCategory.movePositionBetween(0.0, nextCategory.getPosition());
        } else {
            movingCategory.movePositionBetween(prevCategory.getPosition(), nextCategory.getPosition());
        }
    }

    @Transactional
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }
}
