package shop.flowchat.team.readmodel.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.BaseEntity;
import shop.flowchat.team.infrastructure.messaging.member.MemberEventPayload;

import java.time.LocalDate;
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
    private MemberState state;

    @Builder
    private MemberReadModel(UUID id, String nickname, String name, LocalDate birth, String avatarUrl, MemberState state) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.state = state;
    }

    public static MemberReadModel create(MemberEventPayload payload) {
        return MemberReadModel.builder()
                .id(payload.id())
                .nickname(payload.nickname())
                .name(payload.name())
                .avatarUrl(payload.avatarUrl())
                .state(payload.state())
                .build();
    }

    public void update(MemberEventPayload payload) {
        this.nickname = payload.nickname();
        this.name = payload.name();
        this.avatarUrl = payload.avatarUrl();
        this.state = payload.state();
    }

    public MemberState changeState(MemberState state) {
        return this.state = state;
    }

}
