package shop.flowchat.team.service.channelmember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channelmember.ChannelMember;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;
import shop.flowchat.team.infrastructure.repository.channelmember.ChannelMemberRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChannelMemberService {
    private final ChannelMemberRepository channelMemberRepository;

    @Transactional
    public void createChannelMembers(List<MemberReadModel> members, Channel channel) {
        List<ChannelMember> channelMembers = members.stream()
                .map(member -> ChannelMember.from(channel, member))
                .toList();
        channelMemberRepository.saveAll(channelMembers);
    }

    @Transactional(readOnly = true)
    public List<ChannelMember> getChannelMembersByMemberId(UUID memberId) {
        return channelMemberRepository.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<ChannelMember> getChannelMembersByChannelId(Long channelId) {
        return channelMemberRepository.findByChannelId(channelId);
    }

}
