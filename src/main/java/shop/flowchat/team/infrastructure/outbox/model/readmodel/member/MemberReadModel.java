package shop.flowchat.team.infrastructure.outbox.model.readmodel.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.BaseEntity;
import shop.flowchat.team.infrastructure.messaging.member.MemberEventPayload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MemberReadModel extends BaseEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private String nickname;
    private String name;
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private MemberReadModelState state;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Builder
    private MemberReadModel(UUID id, String nickname, String name, LocalDate birth, String avatarUrl, MemberReadModelState state, LocalDateTime timestamp) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.state = state;
        this.timestamp = timestamp;
    }

    public static MemberReadModel create(MemberEventPayload payload) {
        return MemberReadModel.builder()
                .id(payload.id())
                .nickname(payload.nickname())
                .name(payload.name())
                .avatarUrl(payload.avatarUrl())
                .state(payload.state())
                .timestamp(payload.timestamp())
                .build();
    }

    public void update(MemberEventPayload payload) {
        this.nickname = payload.nickname();
        this.name = payload.name();
        this.avatarUrl = payload.avatarUrl();
        this.state = payload.state();
        this.timestamp = payload.timestamp();
    }

}
