package shop.flowchat.team.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChannelCreateRequest(
        @NotBlank(message = "채널 이름을 입력해 주세요.")
        @Size(min = 2, max = 20, message = "20자 이내로 입력해 주세요.")
        @Schema(description = "채널 이름", example = "채팅-fe")
        String name,
        @NotNull(message = "카테고리 내의 채널 위치를 입력해 주세요.")
        @Min(value = 1, message = "카테고리 내의 채널 위치는 1 이상이어야 합니다.")
        @Schema(description = "카테고리 내의 채널 위치", example = "1")
        Integer position,
        @NotBlank(message = "채널의 유형을 입력해 주세요")
        @Schema(description = "채널 유형", example = "TEXT / VOICE")
        String channelType
) {
    public static ChannelCreateRequest init(String channelType) {
        return new ChannelCreateRequest("일반", 1, channelType);
    }
}
