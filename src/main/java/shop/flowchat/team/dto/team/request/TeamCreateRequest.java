package shop.flowchat.team.dto.team.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record TeamCreateRequest(
        @NotBlank(message = "팀 서버 이름을 입력해 주세요.")
        @Size(min = 2, max = 20, message = "20자 이내로 입력해 주세요.")
        @Schema(description = "팀 서버 이름", example = "Chat프로젝트")
        String name,
        @Pattern(regexp = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$", message = "올바른 URL 형식이어야 합니다.")
        @Schema(description = "팀 서버 icon url", example = "https://snowball-bucket.s3.ap-northeast-2.amazonaws.com/f41b6bb9-3jerry.png")
        String iconUrl
) {
}
