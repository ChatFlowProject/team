package shop.flowchat.team.presentation.dto.view;

import shop.flowchat.team.presentation.dto.channel.response.ChannelResponse;
import shop.flowchat.team.presentation.dto.member.response.MemberInfoResponse;

import java.util.List;

public record PrivateChannelViewResponse(
        ChannelResponse channel,
        List<MemberInfoResponse> channelMembers
) {
    public static PrivateChannelViewResponse from(ChannelResponse channel, List<MemberInfoResponse> channelMembers) {
        return new PrivateChannelViewResponse(
                channel,
                channelMembers
        );
    }
}
