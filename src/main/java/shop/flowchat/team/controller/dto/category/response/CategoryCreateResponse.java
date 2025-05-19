package shop.flowchat.team.controller.dto.category.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryCreateResponse(
        @Schema(description = "생성된 카테고리의 고유키", example = "5")
        Long newCategoryId,
        @Schema(description = "생성된 카테고리의 위치", example = "62.5")
        Double position
) {
    public static CategoryCreateResponse from(Long id, Double position) {
        return new CategoryCreateResponse(id, position);
    }
}
