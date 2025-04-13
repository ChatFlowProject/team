package shop.flowchat.team.entity.channel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.entity.BaseEntity;
import shop.flowchat.team.entity.category.Category;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(
        name = "uk_channel_category_position",
        columnNames = {"category_id", "position"}
)})
public class Channel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Min(1)
    @Column(nullable = false)
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Builder
    private Channel(String name, Category category, Integer position, ChannelType type) {
        this.name = name;
        this.category = category;
        this.position = position;
        this.type = type;
    }

    public static Channel from(ChannelCreateRequest request, Category category) {
        return Channel.builder()
                .name(request.name())
                .category(category)
                .position(request.position())
                .type(ChannelType.of(request.channelType()))
                .build();
    }
}
