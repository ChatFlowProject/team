package shop.flowchat.team.controller.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChannelMoveRequest(
        @NotNull(message = "목적지에 해당하는 카테고리 ID를 입력해 주세요.")
        @Min(value = 0, message = "0 이상의 값이어야 합니다.")
        @Schema(description = "채널이 위치할 카테고리 ID", example = "1")
        Long destCategoryId,
        @NotNull(message = "채널 ID를 입력해 주세요. 처음 위치로 이동할 경우 0")
        @Min(value = 0, message = "0 이상의 값이어야 합니다.")
        @Schema(description = "이전 위치의 채널 ID", example = "1")
        Long prevChannelId,
        @NotNull(message = "채널 ID를 입력해 주세요. 마지막 위치로 이동할 경우 0")
        @Min(value = 0, message = "0 이상의 값이어야 합니다.")
        @Schema(description = "다음 위치의 채널 ID", example = "3")
        Long nextChannelId
) {
}
