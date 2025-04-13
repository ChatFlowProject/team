package shop.flowchat.team.service.facade;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.dto.category.response.CategoryCreateResponse;
import shop.flowchat.team.dto.category.response.CategoryResponse;
import shop.flowchat.team.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.dto.channel.response.ChannelCreateResponse;
import shop.flowchat.team.dto.channel.response.ChannelResponse;
import shop.flowchat.team.dto.member.request.MemberListRequest;
import shop.flowchat.team.dto.member.response.MemberResponse;
import shop.flowchat.team.dto.team.request.TeamCreateRequest;
import shop.flowchat.team.dto.team.response.TeamCreateResponse;
import shop.flowchat.team.dto.team.response.TeamResponse;
import shop.flowchat.team.dto.teammember.response.TeamMemberResponse;
import shop.flowchat.team.dto.view.CategoryViewResponse;
import shop.flowchat.team.dto.view.TeamViewResponse;
import shop.flowchat.team.entity.category.Category;
import shop.flowchat.team.entity.channel.Channel;
import shop.flowchat.team.entity.channel.ChannelType;
import shop.flowchat.team.entity.team.Team;
import shop.flowchat.team.entity.teammember.MemberRole;
import shop.flowchat.team.entity.teammember.TeamMember;
import shop.flowchat.team.exception.ErrorCode;
import shop.flowchat.team.exception.common.AuthorizationException;
import shop.flowchat.team.exception.common.ExternalServiceException;
import shop.flowchat.team.exception.common.ServiceException;
import shop.flowchat.team.service.core.CategoryService;
import shop.flowchat.team.service.core.ChannelService;
import shop.flowchat.team.service.core.TeamMemberService;
import shop.flowchat.team.service.core.TeamService;
import shop.flowchat.team.service.integration.MemberClient;

import java.util.Comparator;
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
    public TeamCreateResponse initializeTeam(String token, TeamCreateRequest request) {
        try { // todo: 시그널링 서버 추가시 VOICE 타입 채널 및 카테고리 생성
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            Team team = teamService.createTeam(request, memberId);
            teamMemberService.createTeamMember(team, memberId, MemberRole.OWNER);
            Category category = categoryService.createCategory(CategoryCreateRequest.init(), team);
            channelService.createChannel(ChannelCreateRequest.init(ChannelType.TEXT.toString()), category);
            return TeamCreateResponse.from(team.getId());
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
            if (!isFriend) throw new AuthorizationException("초대 대상과 친구 관계가 아닙니다.");
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
            throw new ExternalServiceException(String.format("Failed to get response on joinTeam. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public CategoryCreateResponse addCategory(UUID teamId, CategoryCreateRequest request) {
        Team team = teamService.getTeamById(teamId);
        Category category = categoryService.createCategory(request, team);
        return CategoryCreateResponse.from(category.getId(), category.getPosition());
    }

    @Transactional
    public ChannelCreateResponse addChannel(UUID teamId, Long categoryId, ChannelCreateRequest request) {
        Category category = categoryService.validateTeamCategory(teamId, categoryId);
        Channel channel = channelService.createChannel(request, category);
        return ChannelCreateResponse.from(channel.getId(), channel.getPosition());
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams(String token) {
        try {
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            return teamMemberService.getTeamMembersByMemberId(memberId).stream()
                    .map(teamMember -> TeamResponse.from(teamMember.getTeam()))
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on getAllTeams. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public TeamViewResponse getTeamView(String token, UUID teamId) {
        try {
            // Team 서버 및 TeamMember들 조회
            Team team = teamService.getTeamById(teamId);
            List<TeamMember> teamMembers = teamMemberService.getTeamMembersByTeamId(team.getId());
            // TeamMember의 회원 정보 조회
            MemberResponse memberResponse = memberClient.getMemberInfoList(token,
                    MemberListRequest.from(teamMembers.stream().map(TeamMember::getMemberId).toList())).data();
            // 해당 팀 서버 조회 요청자의 권한 체크
            if (teamMembers.stream().noneMatch(tm -> tm.getMemberId().equals(memberResponse.requester()))) {
                throw new AuthorizationException("해당 팀 서버의 회원이 아닙니다.");
            }
            // 팀 서버의 카테고리 및 채널 조회
            List<Category> categories = categoryService.getCategoryByTeamId(teamId);
            List<Channel> channels = channelService.getChannelByCategoryIds(
                    categories.stream().map(Category::getId).toList());
            // TeamViewResponse 데이터 파싱 및 반환
            TeamResponse teamResponse = TeamResponse.from(team);
            List<CategoryViewResponse> categoryViewResponses = categories.stream()
                    .map(category -> CategoryViewResponse.from(
                            CategoryResponse.from(category),
                            channels.stream()
                                    .filter(channel -> channel.getCategory().getId().equals(category.getId()))
                                    .map(ChannelResponse::from)
                                    .sorted(Comparator.comparing(ChannelResponse::position))// position 오름차순 정렬
                                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());
            List<TeamMemberResponse> teamMemberResponses = teamMembers.stream()
                    .map(teamMember -> TeamMemberResponse.from(
                            teamMember,
                            memberResponse.memberList().stream()
                                    .filter(member -> member.id().equals(teamMember.getMemberId()))
                                    .findFirst()
                                    .orElse(null))) // teamMember와 일치하는 memberResponse가 없는 경우 - 탈퇴한 회원의 경우?
                    .collect(Collectors.toList());
            return TeamViewResponse.from(teamResponse, categoryViewResponses, teamMemberResponses);
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on getTeamView. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public void modifyTeamMemberRole(String token, UUID teamId, UUID memberId, MemberRole role) {
        try {
            memberClient.getMemberInfo(token).data().id(); // todo: 수정자의 권한 확인 로직 추가 (AuthorizationException)
            TeamMember teamMember = teamMemberService.getTeamMemberByTeamIdAndMemberId(teamId, memberId);
            teamMember.modifyMemberRole(role);
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on modifyTeamMemberRole. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public void deleteTeam(String token, UUID teamId) {
        try {
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            teamService.deleteTeam(memberId, teamId);
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on deleteTeam. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }
}
