package shop.flowchat.team.entity.category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.dto.category.CategoryCreateRequest;
import shop.flowchat.team.entity.BaseEntity;
import shop.flowchat.team.entity.team.Team;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(
        name = "uk_team_position",
        columnNames = {"team_id", "position"}
)})
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer position;

    @Builder
    private Category(String name, Team team, Integer position) {
        this.name = name;
        this.team = team;
        this.position = position;
    }

    public static Category from(CategoryCreateRequest request, Team team) {
        return Category.builder()
                .name(request.name())
                .team(team)
                .position(request.position())
                .build();
    }
}
