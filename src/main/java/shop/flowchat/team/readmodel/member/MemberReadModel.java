package shop.flowchat.team.readmodel.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.BaseEntity;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MemberReadModel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 20, unique = true)
    private String nickname;
    @Column(nullable = false, length = 20)
    private String name;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private MemberReadModelState state;


    @Builder
    private MemberReadModel(String nickname, String name, LocalDate birth, String avatarUrl, MemberReadModelState state) {
        this.nickname = nickname;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.state = state;
    }

//    public static Member create(SignUpRequest request, PasswordEncoder encoder) {
//        return Member.builder()
//                .nickname(payload.nickname())
//                .name(payload.name())
//                .avatarUrl()
//                .state(MemberState.OFFLINE)
//                .build();
//    }
//
//    public void update(MemberUpdateRequest newMember, PasswordEncoder encoder) {
//        this.birth = LocalDate.parse(newMember.birth(), DateTimeFormatter.ISO_LOCAL_DATE);
//        this.avatar = newMember.avatarUrl();
//    }
//
//    public MemberState modifyState(MemberState state) {
//        return this.state = state;
//    }

}
