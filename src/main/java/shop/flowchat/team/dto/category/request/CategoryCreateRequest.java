package shop.flowchat.team.dto.category.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "카테고리 이름을 입력해 주세요.")
        @Size(min = 2, max = 20, message = "20자 이내로 입력해 주세요.")
        @Schema(description = "카테고리 이름", example = "frontend")
        String name,
        @NotNull(message = "팀 서버내의 카테고리 위치를 입력해 주세요.")
        @Min(value = 1, message = "팀 서버내의 카테고리 위치는 1 이상이어야 합니다.")
        @Schema(description = "팀 서버내의 카테고리 위치", example = "1")
        Integer position

) {
    public static CategoryCreateRequest init() {
        return new CategoryCreateRequest("채팅 채널", 1);
    }
}
