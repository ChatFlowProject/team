package shop.flowchat.team.infrastructure.outbox.event.team;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.TeamEventPayload;

public class TeamCreateEvent extends OutboxEvent {

    public TeamCreateEvent(String aggregateId, TeamEventPayload payload) {
        super("team", aggregateId, "teamCreate", payload, "");
    }

}
