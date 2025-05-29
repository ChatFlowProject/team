package shop.flowchat.team.infrastructure.messaging.friendship;

import java.util.UUID;

public record FriendshipEventPayload(
        Long id,
        UUID fromMemberId,
        UUID toMemberId

) {
    public static FriendshipEventPayload from(Long id, UUID fromMemberId, UUID toMemberId) {
        return new FriendshipEventPayload(
                id,
                fromMemberId,
                toMemberId
        );
    }
}
