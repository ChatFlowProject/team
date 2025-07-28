package shop.flowchat.team.infrastructure.outbox.event.teammember;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.TeamMemberEventPayload;

public class TeamMemberDeleteEvent extends OutboxEvent {

    public TeamMemberDeleteEvent(String aggregateId, TeamMemberEventPayload payload) {
        super("teamMember", aggregateId, "teamMemberCreate", payload, "");
    }

}
