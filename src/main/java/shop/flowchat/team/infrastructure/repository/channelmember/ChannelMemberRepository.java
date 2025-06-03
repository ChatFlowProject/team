package shop.flowchat.team.infrastructure.repository.channelmember;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.domain.channelmember.ChannelMember;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {

}
