package shop.flowchat.team.infrastructure.outbox.event.teammember;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.TeamMemberEventPayload;

public class TeamMemberCreateEvent extends OutboxEvent {

    public TeamMemberCreateEvent(String aggregateId, TeamMemberEventPayload payload) {
        super("teamMember", aggregateId, "teamMemberCreate", payload, "");
    }

}
