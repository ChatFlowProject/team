package shop.flowchat.team.infrastructure.messaging.member;

import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModelState;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberEventPayload(
        UUID id,
        String nickname,
        String name,
        String avatarUrl,
        MemberReadModelState state,
        LocalDateTime createdAt,
        LocalDateTime timestamp
) {
}
