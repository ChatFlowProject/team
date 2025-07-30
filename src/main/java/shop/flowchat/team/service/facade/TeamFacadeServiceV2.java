package shop.flowchat.team.service.facade;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.common.exception.ErrorCode;
import shop.flowchat.team.common.exception.custom.ExternalServiceException;
import shop.flowchat.team.common.exception.custom.ServiceException;
import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelType;
import shop.flowchat.team.domain.team.Team;
import shop.flowchat.team.domain.teammember.MemberRole;
import shop.flowchat.team.domain.teammember.TeamMember;
import shop.flowchat.team.infrastructure.feign.MemberClient;
import shop.flowchat.team.infrastructure.outbox.event.team.TeamCreateEvent;
import shop.flowchat.team.infrastructure.outbox.payload.TeamInitializationPayload;
import shop.flowchat.team.presentation.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.presentation.dto.team.request.TeamCreateRequest;
import shop.flowchat.team.presentation.dto.team.response.TeamCreateResponse;
import shop.flowchat.team.service.category.CategoryService;
import shop.flowchat.team.service.channel.ChannelService;
import shop.flowchat.team.service.team.TeamService;
import shop.flowchat.team.service.teammember.TeamMemberService;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TeamFacadeServiceV2 {
    private final TeamService teamService;
    private final CategoryService categoryService;
    private final ChannelService channelService;
    private final TeamMemberService teamMemberService;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberClient memberClient;

    @Transactional
    public TeamCreateResponse initializeTeam(String token, TeamCreateRequest request) {
        try { // todo: 시그널링 서버 추가시 VOICE 타입 채널 및 카테고리 생성
            UUID memberId = memberClient.getMemberInfo(token).data().id();
            Team team = teamService.createTeam(request, memberId);
            TeamMember teamMember = teamMemberService.createTeamMember(team, memberId, MemberRole.OWNER);
            Category category = categoryService.createCategory(CategoryCreateRequest.init(), team);
            Channel channel = channelService.createChannel(ChannelCreateRequest.initChannel(ChannelType.TEXT.toString()), category);

            eventPublisher.publishEvent(new TeamCreateEvent(team.getId().toString(), TeamInitializationPayload.from(team, teamMember, category, channel)));
            return TeamCreateResponse.from(team.getId());
        } catch (FeignException e) {
            throw new ExternalServiceException(String.format("Failed to get response on initializeTeam v2. [status:%s][message:%s]", e.status(), e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("initializeTeam - 입력값이 잘못되었습니다.");
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
