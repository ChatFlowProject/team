package shop.flowchat.team.presentation.dto.channel.response;

import io.swagger.v3.oas.annotations.media.Schema;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelAccessType;
import shop.flowchat.team.domain.channel.ChannelType;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public record ChannelResponse(
        @Schema(description = "채널 고유키", example = "5")
        Long id,
        @Schema(description = "채널 이름", example = "fe-알림")
        String name,
        @Schema(description = "카테고리내 채널 위치", example = "62.5")
        Double position,
        @Schema(description = "채널 유형", example = "TEXT")
        ChannelType type,
        @Schema(description = "채널 접근 유형 (PUBLIC/PRIVATE)", example = "PRIVATE")
        ChannelAccessType accessType,
        @Schema(description = "채팅방 ID", example = "98bd5bf6-848a-43d4-8683-205523c9e359")
        UUID chatId

) {
    public static ChannelResponse ofTeam(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getPosition(),
                channel.getChannelType(),
                channel.getAccessType(),
                channel.getChatId()
        );
    }

    public static ChannelResponse ofPrivate(Channel channel, String memberName) {
        String names = channel.getName();
        names = Arrays.stream(names.split(",\\s*")) // 구분자로 문자열 분리
                .filter(name -> !name.equals(memberName)) // 제거할 이름 제외
                .collect(Collectors.joining(","));
        return new ChannelResponse(
                channel.getId(),
                names,
                channel.getPosition(),
                channel.getChannelType(),
                channel.getAccessType(),
                channel.getChatId()
        );
    }
}
