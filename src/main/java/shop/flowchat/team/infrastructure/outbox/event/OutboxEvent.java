package shop.flowchat.team.infrastructure.outbox.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class OutboxEvent<T> {
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private T payload;
    private String eventId;

    public void setEventId(String  eventId) {
        this.eventId = eventId;
    }

}
