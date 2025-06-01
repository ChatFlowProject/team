package shop.flowchat.team.infrastructure.outbox.model.readmodel.friendship;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.BaseEntity;
import shop.flowchat.team.infrastructure.messaging.friendship.FriendshipEventPayload;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class FriendshipReadModel extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id; // "fromMemberId:toMemberId" 형태의 정렬된 aggregateId (fromMemberId가 toMemberId보다 큼)

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID fromMemberId;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID toMemberId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Builder
    private FriendshipReadModel(String id, UUID fromMemberId, UUID toMemberId, LocalDateTime timestamp) {
        this.id = id;
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
        this.timestamp = timestamp;
    }

    public static FriendshipReadModel create(FriendshipEventPayload payload) {
        return FriendshipReadModel.builder()
                .id(payload.id())
                .fromMemberId(payload.fromMemberId())
                .toMemberId(payload.toMemberId())
                .timestamp(payload.timestamp())
                .build();
    }

}