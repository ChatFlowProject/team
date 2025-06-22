package shop.flowchat.team.service.channelmember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channelmember.ChannelMember;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;
import shop.flowchat.team.infrastructure.repository.channelmember.ChannelMemberRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChannelMemberService {
    private final ChannelMemberRepository channelMemberRepository;

    @Transactional
    public void createChannelMembers(List<MemberReadModel> members, Channel channel) {
        for (MemberReadModel member : members) {
            ChannelMember channelMember = ChannelMember.from(member);
            channel.addChannelMember(channelMember); // 연관관계 양방향 설정
        }

        // saveAll 안 해도 됨 cascade = ALL -> channel save 시 자동 저장
        // channelMemberRepository.saveAll(channelMembers);
    }

}
