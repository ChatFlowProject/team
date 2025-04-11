package shop.flowchat.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.flowchat.team.entity.teammember.TeamMember;

import java.util.Optional;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.team WHERE tm.team.id = :teamId AND tm.memberId = :memberId")
    Optional<TeamMember> findByTeamIdAndMemberId(@Param("teamId") UUID teamId, @Param("memberId") UUID memberId);

}
