package shop.flowchat.team.service.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.common.exception.ErrorCode;
import shop.flowchat.team.common.exception.custom.AuthorizationException;
import shop.flowchat.team.common.exception.custom.EntityNotFoundException;
import shop.flowchat.team.common.exception.custom.ServiceException;
import shop.flowchat.team.common.util.JwtTokenProvider;
import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelAccessType;
import shop.flowchat.team.domain.channel.ChannelType;
import shop.flowchat.team.domain.team.Team;
import shop.flowchat.team.domain.teammember.MemberRole;
import shop.flowchat.team.domain.teammember.Permission;
import shop.flowchat.team.domain.teammember.TeamMember;
import shop.flowchat.team.infrastructure.outbox.event.category.CategoryCreateEvent;
import shop.flowchat.team.infrastructure.outbox.event.category.CategoryDeleteEvent;
import shop.flowchat.team.infrastructure.outbox.event.channel.ChannelCreateEvent;
import shop.flowchat.team.infrastructure.outbox.event.channel.ChannelDeleteEvent;
import shop.flowchat.team.infrastructure.outbox.event.channel.ChannelUpdateEvent;
import shop.flowchat.team.infrastructure.outbox.event.team.TeamCreateEvent;
import shop.flowchat.team.infrastructure.outbox.event.team.TeamDeleteEvent;
import shop.flowchat.team.infrastructure.outbox.event.team.TeamUpdateEvent;
import shop.flowchat.team.infrastructure.outbox.event.teammember.TeamMemberCreateEvent;
import shop.flowchat.team.infrastructure.outbox.event.teammember.TeamMemberDeleteEvent;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;
import shop.flowchat.team.infrastructure.outbox.payload.*;
import shop.flowchat.team.presentation.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.presentation.dto.category.request.CategoryMoveRequest;
import shop.flowchat.team.presentation.dto.category.response.CategoryCreateResponse;
import shop.flowchat.team.presentation.dto.category.response.CategoryResponse;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.presentation.dto.channel.request.ChannelMoveRequest;
import shop.flowchat.team.presentation.dto.channel.request.ChannelUpdateRequest;
import shop.flowchat.team.presentation.dto.channel.response.ChannelResponse;
import shop.flowchat.team.presentation.dto.member.response.MemberInfoResponse;
import shop.flowchat.team.presentation.dto.team.request.TeamCreateRequest;
import shop.flowchat.team.presentation.dto.team.request.TeamUpdateRequest;
import shop.flowchat.team.presentation.dto.team.response.TeamCreateResponse;
import shop.flowchat.team.presentation.dto.team.response.TeamResponse;
import shop.flowchat.team.presentation.dto.teammember.response.TeamMemberResponse;
import shop.flowchat.team.presentation.dto.teammember.result.JoinTeamResult;
import shop.flowchat.team.presentation.dto.view.CategoryViewResponse;
import shop.flowchat.team.presentation.dto.view.TeamViewResponse;
import shop.flowchat.team.service.category.CategoryService;
import shop.flowchat.team.service.channel.ChannelService;
import shop.flowchat.team.service.readmodel.MemberReadModelService;
import shop.flowchat.team.service.team.TeamService;
import shop.flowchat.team.service.teammember.TeamMemberService;

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
    private final MemberReadModelService memberReadModelService;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TeamCreateResponse initializeTeam(String token, TeamCreateRequest request) {
        try {
            UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
            Team team = teamService.createTeam(request, memberId);
            TeamMember teamMember = teamMemberService.createTeamMember(team, memberId, MemberRole.OWNER);
            Category category = categoryService.createCategory(CategoryCreateRequest.init(), team);
            Channel channel = channelService.createChannel(ChannelCreateRequest.initChannel(ChannelType.TEXT.toString()), category);
            eventPublisher.publishEvent(new TeamCreateEvent(team.getId().toString(), TeamInitializationPayload.from(team, teamMember, category, channel)));
            return TeamCreateResponse.from(team.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("initializeTeam - 입력값이 잘못되었습니다.");
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public JoinTeamResult joinTeam(String token, UUID teamId) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        Team team = teamService.getTeamById(teamId);
        return teamMemberService.findTeamMemberByTeamIdAndMemberId(teamId, memberId)
                .map(teamMember -> new JoinTeamResult(teamMember.getId(), false))
                .orElseGet(() -> {
                    TeamMember teamMember = teamMemberService.createTeamMember(team, memberId, MemberRole.MEMBER);
                    eventPublisher.publishEvent(new TeamMemberCreateEvent(teamMember.getId().toString(), TeamMemberEventPayload.from(
                            teamMember)));
                    return new JoinTeamResult(teamMember.getId(), true);
                });
    }

    @Transactional
    public CategoryCreateResponse addCategory(UUID teamId, CategoryCreateRequest request) {
        Team team = teamService.getTeamById(teamId);
        Category category = categoryService.createCategory(request, team);
        eventPublisher.publishEvent(new CategoryCreateEvent(category.getId().toString(), CategoryEventPayload.from(category)));
        return CategoryCreateResponse.from(category.getId(), category.getPosition());
    }

    @Transactional
    public ChannelResponse addChannel(UUID teamId, Long categoryId, ChannelCreateRequest request) {
        // todo: 권한 체크
        Category category = categoryService.getAndValidateTeamCategory(teamId, categoryId);
        Channel channel = channelService.createChannel(request, category);
        eventPublisher.publishEvent(new ChannelCreateEvent(channel.getId().toString(), ChannelEventPayload.from(channel)));
        return ChannelResponse.ofTeam(channel);
    }

    @Transactional
    public ChannelResponse updateChannel(UUID teamId, Long categoryId, Long channelId, ChannelUpdateRequest request) {
        // todo: 권한 체크
        categoryService.getAndValidateTeamCategory(teamId, categoryId);
        Channel channel = channelService.getChannelById(channelId);
        if (ChannelAccessType.PRIVATE.equals(channel.getAccessType())) {
            throw new IllegalArgumentException("Private 채널의 이름은 변경할 수 없습니다.");
        }
        channel.updateChannel(request);
        eventPublisher.publishEvent(new ChannelUpdateEvent(channel.getId().toString(), ChannelEventPayload.from(channel)));
        return ChannelResponse.ofTeam(channel);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams(String token) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        return teamMemberService.getTeamMembersByMemberId(memberId).stream()
                .map(teamMember -> TeamResponse.from(teamMember.getTeam()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamViewResponse getTeamView(String token, UUID teamId) {
        List<TeamMember> teamMembers = teamMemberService.getTeamMembersByTeamId(teamId);
        if (teamMembers.size() == 0) {
            throw new EntityNotFoundException("존재하지 않는 팀 서버입니다.");
        }
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        if (teamMembers.stream().noneMatch(tm -> tm.getMemberId().equals(memberId))) {
            throw new AuthorizationException("해당 팀 서버의 회원이 아닙니다.");
        }

        List<MemberReadModel> members = memberReadModelService.getMembersByMemberIds(teamMembers.stream().map(TeamMember::getMemberId).toList());
        List<TeamMemberResponse> teamMemberResponses = teamMembers.stream()
                .map(teamMember -> {
                    MemberReadModel member = members.stream()
                            .filter(m -> m.getId().equals(teamMember.getMemberId()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다. : " + teamMember.getMemberId()));
                    return TeamMemberResponse.from(teamMember, MemberInfoResponse.from(member));
                })
                .collect(Collectors.toList());

        TeamResponse teamResponse = TeamResponse.from(teamMembers.get(0).getTeam());
        List<CategoryViewResponse> categoryViewResponses = getCategoryView(teamMembers.get(0).getTeam());
        return TeamViewResponse.from(teamResponse, categoryViewResponses, teamMemberResponses);
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
                                .map(ChannelResponse::ofTeam)
                                .sorted(Comparator.comparing(ChannelResponse::position))// position 오름차순 정렬
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Transactional
    public TeamResponse updateTeam(String token, UUID teamId, TeamUpdateRequest request) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        Team team = teamService.validateTeamMaster(teamId, memberId);
        eventPublisher.publishEvent(new TeamUpdateEvent(team.getId().toString(), TeamEventPayload.from(team)));
        return TeamResponse.from(team.updateTeam(request));
    }

    @Transactional
    public void modifyTeamMemberRole(String token, UUID teamId, UUID targetId, MemberRole role) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        TeamMember requester = teamMemberService.getTeamMemberByTeamIdAndMemberId(teamId, memberId);

        if (!requester.getRole().getPermissions().contains(Permission.UPDATE)) {
            throw new AuthorizationException("UPDATE 권한이 없습니다");
        }
        if (requester.getMemberId().equals(targetId)) {
            throw new IllegalArgumentException("자기 자신의 역할은 수정할 수 없습니다.");
        }

        teamMemberService.modifyMemberRole(teamId, targetId, role);
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
        // todo: 권한 체크
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
        return getCategoryView(categories.get(0).getTeam());
    }

    @Transactional
    public void deleteTeam(String token, UUID teamId) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        Team team = teamService.validateTeamMaster(teamId, memberId);
        List<Category> categories = categoryService.getCategoryByTeam(team);
        if (!categories.isEmpty()) {
            channelService.deleteAllChannelsByCategories(categories);
            categoryService.deleteAllCategoriesByTeam(team);
        }
        teamMemberService.deleteAllByTeam(team);
        teamService.deleteTeam(team);
        eventPublisher.publishEvent(new TeamDeleteEvent(team.getId().toString(), TeamEventPayload.from(team)));
    }

    @Transactional
    public void leaveTeam(String token, UUID teamId) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        TeamMember teamMember = teamMemberService.deleteByTeamIdAndMemberId(teamId, memberId);
        eventPublisher.publishEvent(new TeamMemberDeleteEvent(teamMember.getId().toString(), TeamMemberEventPayload.from(teamMember)));
    }

    @Transactional
    public void kickTeamMember(String token, UUID teamId, UUID targetId) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        TeamMember requester = teamMemberService.getTeamMemberByTeamIdAndMemberId(teamId, memberId);

        if (!requester.getRole().getPermissions().contains(Permission.DELETE)) {
            throw new AuthorizationException("DELETE 권한이 없습니다");
        }
        if (requester.getMemberId().equals(targetId)) {
            throw new IllegalArgumentException("자기 자신을 추방할 수 없습니다.");
        }

        TeamMember teamMember = teamMemberService.deleteByTeamIdAndMemberId(teamId, targetId);
        eventPublisher.publishEvent(new TeamMemberDeleteEvent(teamMember.getId().toString(), TeamMemberEventPayload.from(teamMember)));
    }

    @Transactional
    public void deleteCategory(UUID teamId, Long categoryId) {
        Category category = categoryService.getAndValidateTeamCategory(teamId, categoryId);
        channelService.deleteChannelsByCategory(category);
        categoryService.deleteCategory(category);
        eventPublisher.publishEvent(new CategoryDeleteEvent(category.getId().toString(), CategoryEventPayload.from(category)));
    }

    @Transactional
    public void deleteChannel(UUID teamId, Long categoryId, Long channelId) {
        // todo: 권한 체크
        Channel channel = channelService.getAndValidateCategoryChannel(categoryId, channelId);
        channelService.deleteChannel(channel);
        eventPublisher.publishEvent(new ChannelDeleteEvent(channel.getId().toString(), ChannelEventPayload.from(channel)));
    }

}
