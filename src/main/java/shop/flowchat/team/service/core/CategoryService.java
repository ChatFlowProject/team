package shop.flowchat.team.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.entity.category.Category;
import shop.flowchat.team.entity.team.Team;
import shop.flowchat.team.exception.common.EntityNotFoundException;
import shop.flowchat.team.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(CategoryCreateRequest request, Team team) {
        Category category = Category.from(request, team);
        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다.: " + e.getMessage());
        }
        return category;
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리입니다."));
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoryByTeamId(UUID teamId) {
        return categoryRepository.findByTeamId(teamId);
    }

    @Transactional
    public void deleteCategory(UUID teamId, Long categoryId) {

    }

}
