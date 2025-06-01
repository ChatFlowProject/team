package shop.flowchat.team.infrastructure.outbox.model.outbox;

import lombok.Getter;

@Getter
public enum EventStatus {
    PENDING, SUCCESS, FAILED
}
