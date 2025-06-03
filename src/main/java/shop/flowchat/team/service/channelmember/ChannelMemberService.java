package shop.flowchat.team.service.channelmember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channelmember.ChannelMember;
import shop.flowchat.team.infrastructure.repository.channelmember.ChannelMemberRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChannelMemberService {
    private final ChannelMemberRepository channelMemberRepository;

    @Transactional
    public void createChannelMembers(List<UUID> memberIds, Channel channel) {
        List<ChannelMember> channelMembers = memberIds.stream()
                .map(id -> ChannelMember.from(channel, id))
                .toList();
        channelMemberRepository.saveAll(channelMembers);
    }

}
