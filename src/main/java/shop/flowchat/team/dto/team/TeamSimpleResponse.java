package shop.flowchat.team.dto.team;

import shop.flowchat.team.entity.team.Team;

import java.util.UUID;

public record TeamSimpleResponse(
        UUID id,
        String name,
        String iconUrl
) {
    public static TeamSimpleResponse from(Team team) {
        return new TeamSimpleResponse(
                team.getId(),
                team.getName(),
                team.getIconUrl()
        );
    }
}
