package shop.flowchat.team.infrastructure.outbox.payload;

import java.time.LocalDateTime;
import java.util.UUID;
import shop.flowchat.team.domain.teammember.TeamMember;

public record TeamMemberEventPayload(
        Long id,
        UUID teamId,
        UUID memberId,
        LocalDateTime timestamp
) {
    public static TeamMemberEventPayload from(TeamMember teamMember) {
        return new TeamMemberEventPayload(
                teamMember.getId(),
                teamMember.getTeam().getId(),
                teamMember.getMemberId(),
                LocalDateTime.now()
        );
    }
}
