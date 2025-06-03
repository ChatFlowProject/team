package shop.flowchat.team.service.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.flowchat.team.common.util.JwtTokenProvider;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelType;
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

    public PrivateChannelViewResponse addPrivateChannel(String token, MemberListRequest request) {
        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
        List<UUID> friendIdList = request.memberIds();
        List<FriendshipReadModel> friendshipList = friendshipReadModelRepository.findByFromMemberIdOrToMemberId(memberId, memberId);
        if(friendIdList.stream()
                .anyMatch(friendId -> friendshipList.stream()
                        .allMatch(friend -> !friend.getFromMemberId().equals(friendId) && !friend.getToMemberId().equals(friendId)))) {
            throw new IllegalArgumentException("친구 관계가 아닌 회원이 포함되어 있어 채널을 생성할 수 없습니다.");
        }

        friendIdList.add(memberId);
        List<UUID> memberIds = friendIdList.stream().distinct().toList();
        List<MemberReadModel> friendList = memberReadModelRepository.findByIdIn(memberIds);
        String friendNames = friendList.stream()
                .map(MemberReadModel::getName)
                .collect(Collectors.joining(","));
        ChannelCreateRequest channelCreateRequest = ChannelCreateRequest.initPrivateChannel(friendNames, ChannelType.TEXT.toString());
        Channel channel = channelService.createPrivateChannel(channelCreateRequest);
        channelMemberService.createChannelMembers(memberIds, channel);

        String memberName = friendList.stream()
                .filter(member -> member.getId().equals(memberId))
                .map(MemberReadModel::getName)
                .findFirst()
                .orElse(null);
        ChannelResponse channelResponse = ChannelResponse.ofPrivate(channel, memberName);
        List<MemberInfoResponse> channelMemberResponses = friendList.stream()
                .map(MemberInfoResponse::from)
                .collect(Collectors.toList());
        return PrivateChannelViewResponse.from(channelResponse, channelMemberResponses);
    }

//    public List<PrivateChannelResponse> getAllPrivateChannel(String token) {
//        UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
//        ChannelResponse channelResponse = ChannelResponse.ofPrivate(channel, memberName);
//        List<MemberInfoResponse> channelMemberResponses = friendList.stream()
//                .map(MemberInfoResponse::from)
//                .collect(Collectors.toList());
//        return PrivateChannelResponse.from(channelResponse, channelMemberResponses);
//    }
}
