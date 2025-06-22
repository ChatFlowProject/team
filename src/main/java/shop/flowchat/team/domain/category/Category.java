package shop.flowchat.team.domain.category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.presentation.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.domain.BaseEntity;
import shop.flowchat.team.domain.team.Team;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(
        name = "uk_category_team_position",
        columnNames = {"team_id", "position"}
)})
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Double position;

    @Builder
    private Category(String name, Team team, Double position) {
        this.name = name;
        this.team = team;
        this.position = position;
    }

    public static Category from(CategoryCreateRequest request, Team team) {
        return Category.builder()
                .name(request.name())
                .team(team)
                .position(1000.0)
                .build();
    }

    public void movePositionBetween(Double positionA, Double positionB) {
        this.position = (positionA + positionB)/2;
    }

}
