package shop.flowchat.team.infrastructure.messaging.member;

import shop.flowchat.team.readmodel.member.MemberState;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberEventPayload(
        UUID id,
        String nickname,
        String name,
        String avatarUrl,
        MemberState state,
        LocalDateTime createdAt
) {
}
