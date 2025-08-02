package shop.flowchat.team.infrastructure.outbox.event.channel;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.ChannelEventPayload;

public class ChannelDeleteEvent extends OutboxEvent {

    public ChannelDeleteEvent(String aggregateId, ChannelEventPayload payload) {
        super("channel", aggregateId, "channelDelete", payload, "");
    }

}
