package shop.flowchat.team.controller.dto.category.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "카테고리 이름을 입력해 주세요.")
        @Size(min = 2, max = 20, message = "20자 이내로 입력해 주세요.")
        @Schema(description = "카테고리 이름", example = "frontend")
        String name
) {
    public static CategoryCreateRequest init() {
        return new CategoryCreateRequest("채팅 채널");
    }
}
