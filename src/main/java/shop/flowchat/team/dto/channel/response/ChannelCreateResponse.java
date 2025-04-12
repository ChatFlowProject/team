package shop.flowchat.team.dto.channel.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChannelCreateResponse(
        @Schema(description = "생성된 채널의 고유키", example = "5")
        Long newChannelId,
        @Schema(description = "생성된 채널의 위치", example = "3")
        Integer position
) {
    public static ChannelCreateResponse from(Long id, Integer position) {
        return new ChannelCreateResponse(id, position);
    }
}
