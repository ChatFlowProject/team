package shop.flowchat.team.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.dto.category.request.CategoryMoveRequest;
import shop.flowchat.team.entity.category.Category;
import shop.flowchat.team.entity.team.Team;
import shop.flowchat.team.exception.common.AuthorizationException;
import shop.flowchat.team.exception.common.EntityNotFoundException;
import shop.flowchat.team.repository.CategoryRepository;

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
            throw new IllegalArgumentException("입력값이 잘못되었습니다.");
        }
        return category;
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리입니다."));
    }

    @Transactional(readOnly = true)
    public Category validateTeamCategory(UUID teamId, Long categoryId) {
        Category category = getCategoryById(categoryId);
        if (!category.getTeam().getId().equals(teamId)) {
            throw new AuthorizationException("카테고리가 위치한 팀 ID와 일치하지 않습니다.");
        }
        return category;
    }

    @Transactional(readOnly = true)
    public List<Category> validateTeamCategory(UUID teamId, List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findByIdIn(categoryIds);
        if(categories.size() != categoryIds.size()) {
            throw new EntityNotFoundException("존재하지 않는 카테고리입니다.");
        }
        categories.stream()
                .filter(category -> !category.getTeam().getId().equals(teamId))
                .findAny()
                .orElseThrow(() -> new AuthorizationException("카테고리가 위치한 팀 ID와 일치하지 않습니다."));
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
            movingCategory.movePositionBetween(prevCategory.getPosition(), 0.0);
        } else if (request.prevCategoryId() == 0) {
            movingCategory.movePositionBetween(2000.0, nextCategory.getPosition());
        } else {
            movingCategory.movePositionBetween(prevCategory.getPosition(), nextCategory.getPosition());
        }
    }

    @Transactional
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }
}
