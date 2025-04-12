package shop.flowchat.team.dto.category.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryCreateResponse(
        @Schema(description = "생성된 카테고리의 고유키", example = "5")
        Long newCategoryId,
        @Schema(description = "생성된 카테고리의 위치", example = "3")
        Integer position
) {
    public static CategoryCreateResponse from(Long id, Integer position) {
        return new CategoryCreateResponse(id, position);
    }
}
