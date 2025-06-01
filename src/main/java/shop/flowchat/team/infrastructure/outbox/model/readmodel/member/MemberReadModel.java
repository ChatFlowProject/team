package shop.flowchat.team.infrastructure.outbox.model.readmodel.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.BaseEntity;
import shop.flowchat.team.infrastructure.messaging.member.MemberEventPayload;
import shop.flowchat.team.presentation.dto.member.response.MemberState;

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

    private LocalDateTime profileUpdateTimestamp;
    private LocalDateTime statusUpdateTimestamp;

    // private boolean isDeleted;

    @Builder
    private MemberReadModel(UUID id, String nickname, String name, String avatarUrl, MemberReadModelState state,
                            LocalDateTime profileUpdateTimestamp, LocalDateTime statusUpdateTimestamp) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.state = state;
        this.profileUpdateTimestamp = profileUpdateTimestamp;
        this.statusUpdateTimestamp = statusUpdateTimestamp;
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

    public boolean isNewProfileUpdateEvent(LocalDateTime timestamp) {
        return profileUpdateTimestamp == null || timestamp.isAfter(profileUpdateTimestamp);
    }

    public boolean isNewStatusUpdateEvent(LocalDateTime timestamp) {
        return statusUpdateTimestamp == null || timestamp.isAfter(statusUpdateTimestamp);
    }

    public void updateProfile(MemberEventPayload payload) {
        this.nickname = payload.nickname();
        this.name = payload.name();
        this.avatarUrl = payload.avatarUrl();
        this.profileUpdateTimestamp = payload.timestamp();
    }

    public MemberReadModelState updateStatus(MemberEventPayload payload) {
        this.state = payload.state();
        this.statusUpdateTimestamp = payload.timestamp();
        return state;
    }

}
