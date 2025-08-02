package shop.flowchat.team.infrastructure.outbox.payload;

import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.team.Team;
import shop.flowchat.team.domain.teammember.TeamMember;

public record TeamInitializationPayload(
        TeamEventPayload teamPayload,
        TeamMemberEventPayload memberPayload,
        CategoryEventPayload categoryPayload,
        ChannelEventPayload channelPayload
) {
    public static TeamInitializationPayload from(Team team, TeamMember member, Category category, Channel channel) {
        return new TeamInitializationPayload(
                TeamEventPayload.from(team),
                TeamMemberEventPayload.from(member),
                CategoryEventPayload.from(category),
                ChannelEventPayload.from(channel)
        );
    }
}