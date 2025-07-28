package shop.flowchat.team.infrastructure.outbox.payload;

import java.time.LocalDateTime;
import java.util.UUID;
import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelAccessType;

public record ChannelEventPayload(
        Long id,
        String name,
        ChannelAccessType channelAccessType,
        Long teamId,
        LocalDateTime timestamp
) {
    public static ChannelEventPayload from(Channel channel) {
        return new ChannelEventPayload(
                channel.getId(),
                channel.getName(),
                channel.getAccessType(),
                channel.getCategory().getId(),
                LocalDateTime.now()
        );
    }
}
