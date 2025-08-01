package shop.flowchat.team.service.team;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.presentation.dto.team.request.TeamCreateRequest;
import shop.flowchat.team.domain.team.Team;
import shop.flowchat.team.common.exception.custom.AuthorizationException;
import shop.flowchat.team.common.exception.custom.EntityNotFoundException;
import shop.flowchat.team.infrastructure.repository.team.TeamRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    @Transactional
    public Team createTeam(TeamCreateRequest request, UUID masterId) {
        Team team = Team.from(request, masterId);
        try {
            teamRepository.save(team);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("createTeam - 입력값이 잘못되었습니다." );
        }
        return team;
    }

    @Transactional(readOnly = true)
    public Team getTeamById(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 팀 서버입니다."));
    }

    @Transactional(readOnly = true)
    public Team validateTeamMaster(UUID teamId, UUID memberId) {
        Team team = getTeamById(teamId);
        if(!team.getMasterId().equals(memberId)) {
            throw new AuthorizationException("팀 서버 마스터가 아닙니다.");
        }
        return team;
    }

    @Transactional
    public void deleteTeam(Team team) {
        teamRepository.delete(team);
    }

}
