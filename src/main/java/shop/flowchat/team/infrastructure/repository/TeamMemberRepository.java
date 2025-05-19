package shop.flowchat.team.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.flowchat.team.domain.model.team.Team;
import shop.flowchat.team.domain.model.teammember.TeamMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.team WHERE tm.team.id = :teamId AND tm.memberId = :memberId")
    Optional<TeamMember> findByTeamIdAndMemberId(@Param("teamId") UUID teamId, @Param("memberId") UUID memberId);

    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.team WHERE tm.memberId = :memberId")
    List<TeamMember> findByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.team WHERE tm.team.id = :teamId")
    List<TeamMember> findByTeamId(@Param("teamId") UUID teamId);

    void deleteByTeam(Team team); // 벌크로 동작하는 쿼리 메소드

}
