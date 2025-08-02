package shop.flowchat.team.infrastructure.outbox.event.channel;

import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.payload.ChannelEventPayload;

public class ChannelUpdateEvent extends OutboxEvent {

    public ChannelUpdateEvent(String aggregateId, ChannelEventPayload payload) {
        super("channel", aggregateId, "channelUpdate", payload, "");
    }

}
