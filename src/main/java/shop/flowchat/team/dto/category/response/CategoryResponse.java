package shop.flowchat.team.dto.category.response;

import io.swagger.v3.oas.annotations.media.Schema;
import shop.flowchat.team.entity.category.Category;

public record CategoryResponse(
        @Schema(description = "카테고리 고유키", example = "2")
        Long id,
        @Schema(description = "카테고리 이름", example = "frontend")
        String name,
        @Schema(description = "팀 서버내 카테고리 위치", example = "31.25")
        Double position
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getPosition()
        );
    }
}
