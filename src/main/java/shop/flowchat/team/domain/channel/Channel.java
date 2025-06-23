package shop.flowchat.team.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.channelmember.ChannelMember;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.domain.BaseEntity;
import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.presentation.dto.channel.request.ChannelUpdateRequest;

import java.util.ArrayList;
import java.util.List;
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

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // null, AccessType=PRIVATE 일 경우 DM 채널
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelMember> channelMembers = new ArrayList<>();

    @Column(nullable = false)
    private Double position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ChannelType channelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ChannelAccessType accessType;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID chatId;

    @Builder
    private Channel(String name, Category category, Double position, ChannelType channelType, ChannelAccessType accessType, UUID chatId) {
        this.name = name;
        this.category = category;
        this.position = position;
        this.channelType = channelType;
        this.accessType = accessType;
        this.chatId = chatId;
    }

    public void addChannelMember(ChannelMember channelMember) { // Note: 연관관계 편의 메서드
        this.channelMembers.add(channelMember);
        channelMember.setChannel(this); // 연관관계 주인의 외래키 설정
    }

    public static Channel ofTeam(ChannelCreateRequest request, Category category, UUID chatId) {
        return Channel.builder()
                .name(request.name())
                .category(category)
                .position(1000.0)
                .channelType(ChannelType.of(request.channelType()))
                .accessType(ChannelAccessType.PUBLIC)
                .chatId(chatId)
                .build();
    }

    public static Channel ofPrivate(ChannelCreateRequest request, UUID chatId) {
        return Channel.builder()
                .name(request.name())
                .position(1000.0)
                .channelType(ChannelType.of(request.channelType()))
                .accessType(ChannelAccessType.PRIVATE)
                .chatId(chatId)
                .build();
    }

    public void updateChannel(ChannelUpdateRequest request) {
        this.name = request.name();
    }

    public void movePosition(Category category, Double positionA, Double positionB) {
        this.category = category;
        this.position = (positionA + positionB)/2;
    }

}
