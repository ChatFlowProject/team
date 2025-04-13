package shop.flowchat.team.entity.teammember;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.entity.BaseEntity;
import shop.flowchat.team.entity.team.Team;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(
        name = "uk_team_member",
        columnNames = {"team_id", "member_id"}
)})
public class TeamMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Builder
    private TeamMember(Team team, UUID memberId, MemberRole role) {
        this.team = team;
        this.memberId = memberId;
        this.role = role;
    }

    public static TeamMember from(Team team, UUID memberId, MemberRole role) {
        return TeamMember.builder()
                .team(team)
                .memberId(memberId)
                .role(role)
                .build();
    }

    public void modifyMemberRole(MemberRole role) {
        this.role = role;
    }
}
