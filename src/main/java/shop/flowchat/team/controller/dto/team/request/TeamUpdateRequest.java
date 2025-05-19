package shop.flowchat.team.controller.dto.team.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TeamUpdateRequest(
        @NotBlank(message = "변경할 팀 서버 이름을 입력해 주세요.")
        @Size(min = 2, max = 20, message = "20자 이내로 입력해 주세요.")
        @Schema(description = "변경할 팀 서버 이름", example = "Chat프로젝트")
        String name,
        @NotNull(message = "변경할 Master의 회원 ID를 입력해 주세요.")
        @Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$",
                message = "올바른 UUID 형식이 아닙니다.")
        @Schema(description = "변경할, 팀 서버 master의 회원 고유키", example = "f3ca6705-9cde-4d6c-a3bf-eb89703ac1d7")
        UUID masterId,
        @Pattern(regexp = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$",
                message = "올바른 URL 형식이어야 합니다.")
        @Schema(description = "변경할 팀 서버 icon url", example = "https://snowball-bucket.s3.ap-northeast-2.amazonaws.com/f41b6bb9-3jerry.png")
        String iconUrl
) {
}
