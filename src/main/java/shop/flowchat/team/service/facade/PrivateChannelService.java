package shop.flowchat.team.service.facade;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.common.util.JwtTokenProvider;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelType;
import shop.flowchat.team.domain.channelmember.ChannelMember;
import shop.flowchat.team.infrastructure.outbox.event.channel.ChannelCreateEvent;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.friendship.FriendshipReadModel;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;

import shop.flowchat.team.infrastructure.outbox.payload.ChannelEventPayload;
import shop.flowchat.team.infrastructure.redis.RedisService;
import shop.flowchat.team.infrastructure.repository.readmodel.MemberReadModelRepository;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.presentation.dto.channel.response.ChannelResponse;
import shop.flowchat.team.presentation.dto.channel.result.AddPrivateChannelResult;
import shop.flowchat.team.presentation.dto.dialog.response.MessageResponse;
import shop.flowchat.team.presentation.dto.member.request.MemberListRequest;
import shop.flowchat.team.presentation.dto.member.response.MemberInfoResponse;
import shop.flowchat.team.presentation.dto.view.PrivateChannelViewResponse;
import shop.flowchat.team.service.channel.ChannelService;
import shop.flowchat.team.service.channelmember.ChannelMemberService;
import shop.flowchat.team.service.readmodel.FriendshipReadModelService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrivateChannelService {
    private final ChannelService channelService;
    private final ChannelMemberService channelMemberService;
    private final FriendshipReadModelService friendshipReadModelService;
    private final MemberReadModelRepository memberReadModelRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AddPrivateChannelResult addPrivateChannel(String token, MemberListRequest request) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        List<UUID> friendIds = request.memberIds();
        List<FriendshipReadModel> friendships = friendshipReadModelService.getAllFriendshipsByMemberId(memberId);
        if (friendIds.stream()
                .anyMatch(friendId -> friendships.stream()
                        .allMatch(friend -> !friend.getFromMemberId().equals(friendId) && !friend.getToMemberId().equals(friendId)))) {
            throw new IllegalArgumentException("친구 관계가 아닌 회원이 포함되어 있어 채널을 생성할 수 없습니다.");
        }

        friendIds.add(memberId);
        friendIds = friendIds.stream().distinct().toList();
        List<MemberReadModel> friends = memberReadModelRepository.findByIdIn(friendIds);
        String friendNames = friends.stream()
                .map(MemberReadModel::getName)
                .sorted()
                .collect(Collectors.joining(","));

        return channelService.findChannelByName(friendNames)
                .map(channel -> new AddPrivateChannelResult(makePrivateChannelViewResponse(channel, memberId), false))
                .orElseGet(() -> {
                    ChannelCreateRequest channelCreateRequest = ChannelCreateRequest.initPrivateChannel(friendNames, ChannelType.TEXT.toString());
                    Channel channel = channelService.createPrivateChannel(channelCreateRequest);
                    List<ChannelMember> channelMembers = channelMemberService.createChannelMembers(friends, channel);
                    eventPublisher.publishEvent(new ChannelCreateEvent(channel.getId().toString(), ChannelEventPayload.from(channel, channelMembers)));
                    return new AddPrivateChannelResult(makePrivateChannelViewResponse(channel, memberId), true);
                });
    }

    @Transactional(readOnly = true)
    public List<PrivateChannelViewResponse> getAllPrivateChannelsForMember(String token) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        List<Channel> channels = channelService.getAllPrivateChannelsByMemberId(memberId);

        return channels.stream()
                .map(channel -> makePrivateChannelViewResponse(channel, memberId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getPrivateChannelMessages(String token, Long channelId) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);

        return redisService.read(
                memberId.toString() + ":" + channelId.toString(),
                new TypeReference<>() {
                });
    }

    private PrivateChannelViewResponse makePrivateChannelViewResponse(Channel channel, UUID myMemberId) {
        String memberName = channel.getChannelMembers().stream()
                .map(ChannelMember::getMember)
                .filter(member -> member.getId().equals(myMemberId))
                .map(MemberReadModel::getName)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("내가 포함되지 않은 채널입니다."));

        ChannelResponse channelResponse = ChannelResponse.ofPrivate(channel, memberName);
        List<MemberInfoResponse> channelMemberResponses = channel.getChannelMembers().stream()
                .map(ChannelMember::getMember)
                .map(MemberInfoResponse::from)
                .collect(Collectors.toList());

        return PrivateChannelViewResponse.from(channelResponse, channelMemberResponses);
    }
}
