package shop.flowchat.team.infrastructure.outbox.event.channel;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.CategoryEventPayload;
import shop.flowchat.team.infrastructure.outbox.payload.ChannelEventPayload;

public class ChannelCreateEvent extends OutboxEvent {

    public ChannelCreateEvent(String aggregateId, ChannelEventPayload payload) {
        super("channel", aggregateId, "channelCreate", payload, "");
    }

}
