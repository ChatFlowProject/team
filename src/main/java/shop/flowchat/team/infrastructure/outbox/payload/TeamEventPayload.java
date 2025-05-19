package shop.flowchat.team.infrastructure.outbox.payload;

import shop.flowchat.team.domain.team.Team;

import java.util.UUID;

public record TeamEventPayload(
        UUID id,
        String name,
        UUID masterId,
        String iconUrl
) {
    public static TeamEventPayload from(Team team) {
        return new TeamEventPayload(
                team.getId(),
                team.getName(),
                team.getMasterId(),
                team.getIconUrl()
        );
    }
}
