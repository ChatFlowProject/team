package shop.flowchat.team.infrastructure.repository.channelmember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.flowchat.team.domain.channelmember.ChannelMember;

import java.util.List;
import java.util.UUID;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    @Query("SELECT cm FROM ChannelMember cm JOIN FETCH cm.channel JOIN FETCH cm.member WHERE cm.member.id = :memberId")
    List<ChannelMember> findByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT cm FROM ChannelMember cm JOIN FETCH cm.channel JOIN FETCH cm.member WHERE cm.channel.id = :channelId")
    List<ChannelMember> findByChannelId(@Param("channelId") Long channelId);

}
