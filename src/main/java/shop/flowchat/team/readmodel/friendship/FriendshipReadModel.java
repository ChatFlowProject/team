package shop.flowchat.team.readmodel.friendship;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.BaseEntity;
import shop.flowchat.team.infrastructure.messaging.friendship.FriendshipEventPayload;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_friendship_from_to",
                        columnNames = {
                                "from_member_id",
                                "to_member_id"
                        }
                )
        }
)
public class FriendshipReadModel extends BaseEntity {

    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID fromMemberId;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID toMemberId;

    @Builder
    private FriendshipReadModel(Long id, UUID fromMemberId, UUID toMemberId) {
        this.id = id;
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
    }

    public static FriendshipReadModel create(FriendshipEventPayload payload) {
        return FriendshipReadModel.builder()
                .id(payload.id())
                .fromMemberId(payload.fromMemberId())
                .toMemberId(payload.toMemberId())
                .build();
    }

    public void update(FriendshipEventPayload payload) {
        this.id = payload.id();
        this.fromMemberId = payload.fromMemberId();
        this.toMemberId = payload.toMemberId();
    }

}