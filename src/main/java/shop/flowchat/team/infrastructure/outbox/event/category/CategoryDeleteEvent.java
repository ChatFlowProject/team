package shop.flowchat.team.infrastructure.outbox.event.category;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.CategoryEventPayload;

public class CategoryDeleteEvent extends OutboxEvent {

    public CategoryDeleteEvent(String aggregateId, CategoryEventPayload payload) {
        super("category", aggregateId, "categoryDelete", payload, "");
    }

}
