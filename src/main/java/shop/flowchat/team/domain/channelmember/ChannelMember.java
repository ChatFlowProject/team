package shop.flowchat.team.domain.channelmember;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberReadModel member;

    @Builder
    private ChannelMember(Channel channel, MemberReadModel member) {
        this.channel = channel;
        this.member = member;
    }

    public static ChannelMember from(Channel channel, MemberReadModel member) {
        return ChannelMember.builder()
                .channel(channel)
                .member(member)
                .build();
    }

}
