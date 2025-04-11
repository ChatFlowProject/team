package shop.flowchat.team.service.facade;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.dto.category.CategoryCreateRequest;
import shop.flowchat.team.dto.channel.ChannelCreateRequest;
import shop.flowchat.team.dto.team.TeamCreateRequest;
import shop.flowchat.team.dto.team.TeamSimpleResponse;
import shop.flowchat.team.dto.team.TeamViewResponse;
import shop.flowchat.team.entity.category.Category;
import shop.flowchat.team.entity.channel.ChannelType;
import shop.flowchat.team.entity.team.Team;
import shop.flowchat.team.entity.teammember.MemberRole;
import shop.flowchat.team.entity.teammember.TeamMember;
import shop.flowchat.team.exception.ErrorCode;
import shop.flowchat.team.exception.common.ExternalServiceException;
import shop.flowchat.team.exception.common.ServiceException;
import shop.flowchat.team.service.core.CategoryService;
import shop.flowchat.team.service.core.ChannelService;
import shop.flowchat.team.service.core.TeamMemberService;
import shop.flowchat.team.service.core.TeamService;
import shop.flowchat.team.service.integration.MemberClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamFacadeService {
    private final TeamService teamService;
    private final CategoryService categoryService;
    private final ChannelService channelService;
    private final TeamMemberService teamMemberService;
    private final MemberClient memberClient;

    @Transactional
    public UUID initializeTeam(String token, TeamCreateRequest request) {
        try { // todo: 시그널링 서버 추가시 VOICE 타입 채널 및 카테고리 생성
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            Team team = teamService.createTeam(request, memberId);
            teamMemberService.createTeamMember(team, memberId, MemberRole.OWNER);
            Category category = categoryService.createCategory(CategoryCreateRequest.init(), team);
            channelService.createChannel(ChannelCreateRequest.init(ChannelType.TEXT.toString()), category);
            return team.getId();
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on initializeTeam. [status:%s][message:%s]", e.status(), e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다: " + e.getMessage());
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Long addTeamMember(String token, UUID teamId, UUID memberId) {
        try {
            Boolean isFriend = memberClient.checkFriendship(token, memberId).data();
            if (!isFriend) throw new IllegalArgumentException("잘못된 요청입니다. 초대 대상과 친구 관계가 아닙니다.");
            Team team = teamService.getTeamById(teamId);
            return teamMemberService.createTeamMember(team, memberId, MemberRole.MEMBER).getId();
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on addTeamMember. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public Long joinTeam(String token, UUID teamId) {
        try {
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            Team team = teamService.getTeamById(teamId);
            return teamMemberService.createTeamMember(team, memberId, MemberRole.MEMBER).getId();
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on addTeamMember. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public Long addCategory(UUID teamId, CategoryCreateRequest request) {
        Team team = teamService.getTeamById(teamId);
        return categoryService.createCategory(request, team).getId();
    }

    @Transactional
    public Long addChannel(UUID teamId, Long categoryId, ChannelCreateRequest request) {
        Category category = validateTeamCategory(teamId, categoryId);
        return channelService.createChannel(request, category);
    }

    @Transactional(readOnly = true)
    public List<TeamSimpleResponse> getAllTeams(String token) {
        try {
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            return teamService.getAllTeamsByMemberId(memberId).stream()
                    .map(TeamSimpleResponse::from)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on getAllTeams. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public TeamViewResponse getTeamView(String token, UUID teamId) {
        return null;
    }

    @Transactional
    public void modifyTeamMemberRole(String token, UUID teamId, UUID memberId, MemberRole role) {
        try {
            memberClient.getMemberInfo(token).data().id(); // todo: 수정자의 권한 확인 로직 추가
            TeamMember teamMember = teamMemberService.getTeamMemberByTeamIdAndMemberId(teamId, memberId);
            teamMember.modifyMemberRole(role); // todo: 변경 감지 확인
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on getAllTeams. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public void deleteCategory(UUID teamId, Long categoryId) {

    }

    public Category validateTeamCategory(UUID teamId, Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        if (!category.getTeam().getId().equals(teamId)) {
            throw new IllegalArgumentException("잘못된 요청입니다. 요청한 팀 ID가 카테고리가 위치한 팀 ID와 일치하지 않습니다.");
        }
        return category;
    }

}
