package shop.flowchat.team.infrastructure.messaging.friendship;

import java.util.UUID;

public record FriendshipEventPayload(
        UUID id,
        UUID fromMemberId,
        UUID toMemberId

) {
    public static FriendshipEventPayload from(UUID id, UUID fromMemberId, UUID toMemberId) {
        return new FriendshipEventPayload(
                id,
                fromMemberId,
                toMemberId
        );
    }
}
