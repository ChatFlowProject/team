package shop.flowchat.team.infrastructure.outbox.payload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelAccessType;
import shop.flowchat.team.domain.channelmember.ChannelMember;

public record ChannelEventPayload(
        Long id,
        String name,
        ChannelAccessType channelAccessType,
        Long categoryId,
        UUID chatId,
        List<UUID> channelMembers,
        LocalDateTime timestamp
) {
    public static ChannelEventPayload from(Channel channel) {
        return new ChannelEventPayload(
                channel.getId(),
                channel.getName(),
                channel.getAccessType(),
                channel.getCategory().getId(),
                channel.getChatId(),
                null,
                LocalDateTime.now()
        );
    }
    public static ChannelEventPayload from(Channel channel,  List<ChannelMember> channelMembers) {
        List<UUID> memberIds = channelMembers.stream()
                .map(cm -> cm.getMember().getId())
                .toList();
        return new ChannelEventPayload(
                channel.getId(),
                channel.getName(),
                channel.getAccessType(),
                channel.getCategory().getId(),
                channel.getChatId(),
                memberIds,
                LocalDateTime.now()
        );
    }
}
