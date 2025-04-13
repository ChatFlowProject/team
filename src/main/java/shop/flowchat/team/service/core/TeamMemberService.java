package shop.flowchat.team.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.entity.team.Team;
import shop.flowchat.team.entity.teammember.MemberRole;
import shop.flowchat.team.entity.teammember.TeamMember;
import shop.flowchat.team.exception.common.EntityNotFoundException;
import shop.flowchat.team.repository.TeamMemberRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public TeamMember createTeamMember(Team team, UUID memberId, MemberRole role) {
        TeamMember teamMember = TeamMember.from(team, memberId, role);
        try {
            teamMemberRepository.save(teamMember);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다: " + e.getMessage());
        }
        return teamMember;
    }

    @Transactional(readOnly = true)
    public TeamMember getTeamMemberByTeamIdAndMemberId(UUID teamId, UUID memberId) {
        return teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 팀 멤버입니다."));
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getTeamMembersByMemberId(UUID memberId) {
        return teamMemberRepository.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getTeamMembersByTeamId(UUID teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    @Transactional
    public void modifyMemberRole(UUID teamId, UUID memberId, MemberRole role) {
        TeamMember teamMember = getTeamMemberByTeamIdAndMemberId(teamId, memberId);
        teamMember.modifyMemberRole(role);
    }

    @Transactional
    public void deleteByTeamIdAndMemberId(UUID teamId, UUID memberId) {
        TeamMember teamMember = getTeamMemberByTeamIdAndMemberId(teamId, memberId);
        if(teamMember.getTeam().getMasterId().equals(memberId)) {
            throw new IllegalArgumentException("팀 서버 마스터는 팀을 떠날 수 없습니다.");
        }
        teamMemberRepository.delete(teamMember);
    }

}
