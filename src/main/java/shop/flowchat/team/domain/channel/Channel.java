package shop.flowchat.team.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.domain.BaseEntity;
import shop.flowchat.team.domain.category.Category;

import java.util.UUID;

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
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Double position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelAccessType accessType;

    private UUID chatId;

    @Builder
    private Channel(String name, Category category, Double position, ChannelType channelType) {
        this.name = name;
        this.category = category;
        this.position = position;
        this.channelType = channelType;
    }

    public static Channel from(ChannelCreateRequest request, Category category) {
        return Channel.builder()
                .name(request.name())
                .category(category)
                .position(1000.0)
                .channelType(ChannelType.of(request.channelType()))
                .build();
    }

    public void movePosition(Category category, Double positionA, Double positionB) {
        this.category = category;
        this.position = (positionA + positionB)/2;
    }
}
