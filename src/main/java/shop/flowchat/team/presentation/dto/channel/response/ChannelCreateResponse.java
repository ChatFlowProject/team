package shop.flowchat.team.presentation.dto.channel.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChannelCreateResponse(
        @Schema(description = "생성된 채널의 고유키", example = "5")
        Long newChannelId,
        @Schema(description = "생성된 채널의 위치", example = "31.25")
        Double position
) {
    public static ChannelCreateResponse from(Long id, Double position) {
        return new ChannelCreateResponse(id, position);
    }
}
