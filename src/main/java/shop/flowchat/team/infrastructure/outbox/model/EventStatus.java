package shop.flowchat.team.infrastructure.outbox.model;

import lombok.Getter;

@Getter
public enum EventStatus {
    PENDING, SUCCESS, FAILED
}
