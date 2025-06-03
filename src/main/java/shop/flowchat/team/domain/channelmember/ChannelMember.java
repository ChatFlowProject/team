package shop.flowchat.team.domain.channelmember;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.channel.Channel;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(
        name = "uk_channel_member",
        columnNames = {"channel_id", "member_id"}
)})
public class ChannelMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private UUID memberId;

    @Builder
    private ChannelMember(Channel channel, UUID memberId) {
        this.channel = channel;
        this.memberId = memberId;
    }

    public static ChannelMember from(Channel channel, UUID memberId) {
        return ChannelMember.builder()
                .channel(channel)
                .memberId(memberId)
                .build();
    }

}
