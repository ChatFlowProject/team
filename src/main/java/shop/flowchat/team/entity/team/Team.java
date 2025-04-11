package shop.flowchat.team.entity.team;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.dto.team.TeamCreateRequest;
import shop.flowchat.team.entity.BaseEntity;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String name;
    
    @Column(nullable = false)
    private UUID ownerId; // 회원 Id와 같음

    private String iconUrl;

    @Builder
    private Team(String name, UUID ownerId, String iconUrl) {
        this.name = name;
        this.ownerId = ownerId;
        this.iconUrl = iconUrl;
    }

    public static Team from(TeamCreateRequest request, UUID ownerId) {
        return Team.builder()
                .name(request.name())
                .ownerId(ownerId)
                .iconUrl(request.iconUrl())
                .build();
    }
}
