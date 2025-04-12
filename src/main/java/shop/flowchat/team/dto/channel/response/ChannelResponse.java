package shop.flowchat.team.dto.channel.response;

import io.swagger.v3.oas.annotations.media.Schema;
import shop.flowchat.team.entity.channel.Channel;
import shop.flowchat.team.entity.channel.ChannelType;

public record ChannelResponse(
        @Schema(description = "채널 고유키", example = "5")
        Long id,
        @Schema(description = "채널 이름", example = "fe-알림")
        String name,
        @Schema(description = "카테고리내 채널 위치", example = "6")
        Integer position,
        @Schema(description = "채널 유형", example = "TEXT")
        ChannelType type

) {
    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getPosition(),
                channel.getType()
        );
    }
}
