package shop.flowchat.team.service.channelmember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channelmember.ChannelMember;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;

import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class ChannelMemberService {

    @Transactional
    public List<ChannelMember> createChannelMembers(List<MemberReadModel> members, Channel channel) {
        List<ChannelMember> channelMembers = new ArrayList<>();
        for (MemberReadModel member : members) {
            ChannelMember channelMember = ChannelMember.from(member);
            channel.addChannelMember(channelMember); // 연관관계 양방향 설정
            channelMembers.add(channelMember);
        }

        // channelMemberRepository.saveAll(channelMembers);
        // saveAll 안 해도 됨 cascade = ALL -> channel save 시 자동 저장

        return channelMembers;
    }

}
