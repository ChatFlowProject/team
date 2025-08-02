package shop.flowchat.team.infrastructure.outbox.event.team;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.TeamEventPayload;

public class TeamUpdateEvent extends OutboxEvent {

    public TeamUpdateEvent(String aggregateId, TeamEventPayload payload) {
        super("team", aggregateId, "teamUpdate", payload, "");
    }

}