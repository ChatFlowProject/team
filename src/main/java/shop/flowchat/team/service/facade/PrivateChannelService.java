package shop.flowchat.team.service.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.common.util.JwtTokenProvider;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelType;
import shop.flowchat.team.domain.channelmember.ChannelMember;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.friendship.FriendshipReadModel;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;
import shop.flowchat.team.infrastructure.repository.readmodel.FriendshipReadModelRepository;
import shop.flowchat.team.infrastructure.repository.readmodel.MemberReadModelRepository;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.presentation.dto.channel.response.ChannelResponse;
import shop.flowchat.team.presentation.dto.view.PrivateChannelViewResponse;
import shop.flowchat.team.presentation.dto.member.request.MemberListRequest;
import shop.flowchat.team.presentation.dto.member.response.MemberInfoResponse;
import shop.flowchat.team.service.channel.ChannelService;
import shop.flowchat.team.service.channelmember.ChannelMemberService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrivateChannelService {
    private final ChannelService channelService;
    private final ChannelMemberService channelMemberService;
    private final FriendshipReadModelRepository friendshipReadModelRepository;
    private final MemberReadModelRepository memberReadModelRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public PrivateChannelViewResponse addPrivateChannel(String token, MemberListRequest request) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        List<UUID> friendIds = request.memberIds();
        List<FriendshipReadModel> friendships = friendshipReadModelRepository.findByFromMemberIdOrToMemberId(memberId, memberId);
        if(friendIds.stream()
                .anyMatch(friendId -> friendships.stream()
                        .allMatch(friend -> !friend.getFromMemberId().equals(friendId) && !friend.getToMemberId().equals(friendId)))) {
            throw new IllegalArgumentException("친구 관계가 아닌 회원이 포함되어 있어 채널을 생성할 수 없습니다.");
        }

        friendIds.add(memberId);
        List<MemberReadModel> friends = memberReadModelRepository.findByIdIn(friendIds.stream().distinct().toList());
        String friendNames = friends.stream()
                .map(MemberReadModel::getName)
                .collect(Collectors.joining(","));
        ChannelCreateRequest channelCreateRequest = ChannelCreateRequest.initPrivateChannel(friendNames, ChannelType.TEXT.toString());
        Channel channel = channelService.createPrivateChannel(channelCreateRequest);
        channelMemberService.createChannelMembers(friends, channel);

        return makePrivateChannelViewResponse(friends, channel, memberId);
    }

    @Transactional(readOnly = true)
    public List<ChannelResponse> getAllPrivateChannel(String token) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        List<ChannelMember> channelMembers = channelMemberService.getChannelMembersByMemberId(memberId);
        return channelMembers.stream()
                .map(channelMember -> ChannelResponse.ofPrivate(channelMember.getChannel(), channelMember.getMember().getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrivateChannelViewResponse getPrivateChannelView(String token, Long channelId) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        Channel channel = channelService.getChannelById(channelId);
        List<ChannelMember> channelMembers = channelMemberService.getChannelMembersByChannelId(channel.getId());
        List<MemberReadModel> members = channelMembers.stream()
                .map(ChannelMember::getMember)
                .collect(Collectors.toList());
        return makePrivateChannelViewResponse(members, channel, memberId);
    }

    private PrivateChannelViewResponse makePrivateChannelViewResponse(List<MemberReadModel> members, Channel channel, UUID myMemberId) {
        String memberName = members.stream()
                .filter(member -> member.getId().equals(myMemberId))
                .map(MemberReadModel::getName)
                .findFirst()
                .orElse(null);
        ChannelResponse channelResponse = ChannelResponse.ofPrivate(channel, memberName);
        List<MemberInfoResponse> channelMemberResponses = members.stream()
                .map(MemberInfoResponse::from)
                .collect(Collectors.toList());
        return PrivateChannelViewResponse.from(channelResponse, channelMemberResponses);
    }
}
