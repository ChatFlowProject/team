package shop.flowchat.team.infrastructure.outbox.event.category;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.CategoryEventPayload;

public class CategoryCreateEvent extends OutboxEvent {

    public CategoryCreateEvent(String aggregateId, CategoryEventPayload payload) {
        super("category", aggregateId, "categoryCreate", payload, "");
    }

}
