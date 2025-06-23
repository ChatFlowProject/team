package shop.flowchat.team.presentation.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChannelUpdateRequest(
        @NotBlank(message = "채널 이름을 입력해 주세요.")
        @Size(min = 2, max = 20, message = "20자 이내로 입력해 주세요.")
        @Schema(description = "채널 이름", example = "채팅-fe")
        String name
) {
}
