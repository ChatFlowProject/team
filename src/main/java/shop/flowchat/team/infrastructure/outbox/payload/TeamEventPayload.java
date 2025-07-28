package shop.flowchat.team.infrastructure.outbox.payload;

import shop.flowchat.team.domain.team.Team;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeamEventPayload(
        UUID id,
        String name,
        String iconUrl,
        LocalDateTime timestamp
) {
    public static TeamEventPayload from(Team team) {
        return new TeamEventPayload(
                team.getId(),
                team.getName(),
                team.getIconUrl(),
                LocalDateTime.now()
        );
    }
}
