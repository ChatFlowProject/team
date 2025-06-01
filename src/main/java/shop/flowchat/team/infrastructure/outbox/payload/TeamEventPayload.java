package shop.flowchat.team.infrastructure.outbox.payload;

import shop.flowchat.team.domain.team.Team;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeamEventPayload(
        UUID id,
        String name,
        UUID masterId,
        String iconUrl,
        LocalDateTime timestamp // 이벤트 발행 시간
) {
    public static TeamEventPayload from(Team team) {
        return new TeamEventPayload(
                team.getId(),
                team.getName(),
                team.getMasterId(),
                team.getIconUrl(),
                LocalDateTime.now()
        );
    }
}
