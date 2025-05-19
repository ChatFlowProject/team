package shop.flowchat.team.service.facade;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.presentation.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.presentation.dto.category.request.CategoryMoveRequest;
import shop.flowchat.team.presentation.dto.category.response.CategoryCreateResponse;
import shop.flowchat.team.presentation.dto.category.response.CategoryResponse;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.presentation.dto.channel.request.ChannelMoveRequest;
import shop.flowchat.team.presentation.dto.channel.response.ChannelCreateResponse;
import shop.flowchat.team.presentation.dto.channel.response.ChannelResponse;
import shop.flowchat.team.presentation.dto.member.request.MemberListRequest;
import shop.flowchat.team.presentation.dto.member.response.MemberResponse;
import shop.flowchat.team.presentation.dto.team.request.TeamCreateRequest;
import shop.flowchat.team.presentation.dto.team.request.TeamUpdateRequest;
import shop.flowchat.team.presentation.dto.team.response.TeamCreateResponse;
import shop.flowchat.team.presentation.dto.team.response.TeamResponse;
import shop.flowchat.team.presentation.dto.teammember.response.TeamMemberResponse;
import shop.flowchat.team.presentation.dto.view.CategoryViewResponse;
import shop.flowchat.team.presentation.dto.view.TeamViewResponse;
import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelType;
import shop.flowchat.team.domain.team.Team;
import shop.flowchat.team.domain.teammember.MemberRole;
import shop.flowchat.team.domain.teammember.TeamMember;
import shop.flowchat.team.common.exception.ErrorCode;
import shop.flowchat.team.common.exception.custom.AuthorizationException;
import shop.flowchat.team.common.exception.custom.EntityNotFoundException;
import shop.flowchat.team.common.exception.custom.ExternalServiceException;
import shop.flowchat.team.common.exception.custom.ServiceException;
import shop.flowchat.team.service.category.CategoryService;
import shop.flowchat.team.service.channel.ChannelService;
import shop.flowchat.team.service.teammember.TeamMemberService;
import shop.flowchat.team.service.team.TeamService;
import shop.flowchat.team.infrastructure.feign.MemberClient;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            throw new IllegalArgumentException("입력값이 잘못되었습니다.");
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
            // Team 서버 및 TeamMember들 조회 (fetch join으로 Team까지 조회)
            List<TeamMember> teamMembers = teamMemberService.getTeamMembersByTeamId(teamId);
            if(teamMembers.size() == 0) { // 팀 마스터는 서버를 나갈 수 없으므로 teamMember 수가 0이면 삭제된 서버
                throw new EntityNotFoundException("존재하지 않는 팀 서버입니다.");
            }
            // TeamMember의 회원 정보 조회
            MemberResponse memberResponse = memberClient.getMemberInfoList(token,
                    MemberListRequest.from(teamMembers.stream().map(TeamMember::getMemberId).toList())).data();
            // 해당 팀 서버 조회 요청자의 권한 체크
            if (teamMembers.stream().noneMatch(tm -> tm.getMemberId().equals(memberResponse.requester()))) {
                throw new AuthorizationException("해당 팀 서버의 회원이 아닙니다.");
            }
            // 팀 멤버들 정보 응답
            List<TeamMemberResponse> teamMemberResponses = teamMembers.stream()
                    .map(teamMember -> TeamMemberResponse.from(
                            teamMember,
                            memberResponse.memberList().stream()
                                    .filter(member -> member.id().equals(teamMember.getMemberId()))
                                    .findFirst()
                                    .orElse(null))) // teamMember와 일치하는 memberResponse가 없는 경우 - 탈퇴한 회원의 경우?
                    .collect(Collectors.toList());

            TeamResponse teamResponse = TeamResponse.from(teamMembers.get(0).getTeam()); // 팀 서버 정보 응답
            List<CategoryViewResponse> categoryViewResponses = getCategoryView(teamMembers.get(0).getTeam());// 팀 서버의 카테고리 및 채널 정보 응답
            return TeamViewResponse.from(teamResponse, categoryViewResponses, teamMemberResponses);
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on getTeamView. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryViewResponse> getCategoryView(Team team) {
        List<Category> categories = categoryService.getCategoryByTeam(team);
        List<Channel> channels = channelService.getChannelByCategoryIds(
                categories.stream().map(Category::getId).toList());
        return categories.stream()
                .map(category -> CategoryViewResponse.from(
                        CategoryResponse.from(category),
                        channels.stream()
                                .filter(channel -> channel.getCategory().getId().equals(category.getId()))
                                .map(ChannelResponse::from)
                                .sorted(Comparator.comparing(ChannelResponse::position))// position 오름차순 정렬
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Transactional
    public TeamResponse updateTeam(String token, UUID teamId, TeamUpdateRequest request) {
        try {
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            Team team = teamService.validateTeamMaster(teamId, memberId);
            return TeamResponse.from(team.updateTeam(request));
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on updateTeam. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public void modifyTeamMemberRole(String token, UUID teamId, UUID targetId, MemberRole role) {
        try {
            memberClient.getMemberInfo(token).data().id();
            teamMemberService.modifyMemberRole(teamId, targetId, role);
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on modifyTeamMemberRole. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public List<CategoryViewResponse> moveCategory(UUID teamId, Long categoryId, CategoryMoveRequest request) {
        List<Long> categoryIds = Stream.of(request.prevCategoryId(), categoryId, request.nextCategoryId())
                .filter(id -> id != 0)
                .collect(Collectors.toList());
        List<Category> categories = categoryService.validateTeamCategories(teamId, categoryIds);
        categoryService.moveCategory(categoryId, categories, request);
        return getCategoryView(categories.get(0).getTeam());
    }

    @Transactional
    public List<CategoryViewResponse> moveChannel(UUID teamId, Long categoryId, Long channelId, ChannelMoveRequest request) {
        // todo: teamId & memberId(token) -> 권한 체크 로직 필요
        List<Long> categoryIds = Stream.of(categoryId, request.destCategoryId())
                .distinct() // 중복 제거 - 같은 카테고리내 이동인 경우
                .collect(Collectors.toList());
        List<Category> categories = categoryService.validateTeamCategories(teamId, categoryIds);
        List<Long> channelIds = Stream.of(request.prevChannelId(), channelId, request.nextChannelId())
                .filter(id -> id != 0)
                .collect(Collectors.toList());
        List<Channel> channels = channelService.validateCategoryChannels(categoryIds, channelIds);

        Category destCategory = categories.stream()
                .filter(c -> c.getId().equals(request.destCategoryId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("목적지 카테고리를 찾을 수 없습니다: " + request.destCategoryId()));
        channelService.moveChannel(channelId, destCategory, channels, request);
        return getCategoryView(categories.get(0).getTeam()); // todo: 왜 쿼리 나감?
    }

    @Transactional
    public void deleteTeam(String token, UUID teamId) {
        try {
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            Team team = teamService.validateTeamMaster(teamId, memberId);
            List<Category> categories = categoryService.getCategoryByTeam(team);
            if (!categories.isEmpty()) {
                channelService.deleteAllChannelsByCategories(categories);
                categoryService.deleteAllCategoriesByTeam(team);
            }
            teamMemberService.deleteAllByTeam(team);
            teamService.deleteTeam(team);
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on deleteTeam. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public void leaveTeam(String token, UUID teamId) {
        try {
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            teamMemberService.deleteByTeamIdAndMemberId(teamId, memberId);
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on leaveTeam. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public void kickTeamMember(String token, UUID teamId, UUID targetId) {
        try {
            memberClient.getMemberInfo(token).data().id();
            teamMemberService.deleteByTeamIdAndMemberId(teamId, targetId);
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on kickTeamMember. [status:%s][message:%s]", e.status(), e.getMessage()));
        }
    }

    @Transactional
    public void deleteCategory(UUID teamId, Long categoryId) {
        Category category = categoryService.validateTeamCategory(teamId, categoryId);
        channelService.deleteChannelsByCategory(category);
        categoryService.deleteCategory(category);
    }

    @Transactional
    public void deleteChannel(UUID teamId, Long categoryId, Long channelId) {
        // todo: teamId & memberId(token) -> 권한 체크 로직 필요
        Channel channel = channelService.validateCategoryChannel(categoryId, channelId);
        channelService.deleteChannel(channel);
    }

}
