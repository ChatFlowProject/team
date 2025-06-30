package shop.flowchat.team.presentation.dto.channel.result;

import shop.flowchat.team.presentation.dto.view.PrivateChannelViewResponse;

public record AddPrivateChannelResult(
        PrivateChannelViewResponse response,
        boolean isCreated
) {
}
