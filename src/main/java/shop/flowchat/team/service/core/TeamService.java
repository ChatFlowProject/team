package shop.flowchat.team.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.dto.team.TeamCreateRequest;
import shop.flowchat.team.entity.team.Team;
import shop.flowchat.team.exception.common.EntityNotFoundException;
import shop.flowchat.team.repository.TeamRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    @Transactional
    public Team createTeam(TeamCreateRequest request, UUID ownerId) {
        Team team = Team.from(request, ownerId);
        try {
            teamRepository.save(team);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다: " + e.getMessage());
        }
        return team;
    }

    @Transactional(readOnly = true)
    public List<Team> getAllTeamsByMemberId(UUID memberId) {
        return teamRepository.findByOwnerId(memberId);
    }

    @Transactional(readOnly = true)
    public Team getTeamById(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 팀 서버입니다."));
    }

}
