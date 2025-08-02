package shop.flowchat.team.infrastructure.outbox.event.team;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.TeamEventPayload;

public class TeamDeleteEvent extends OutboxEvent {

    public TeamDeleteEvent(String aggregateId, TeamEventPayload payload) {
        super("team", aggregateId, "teamDelete", payload, "");
    }

}