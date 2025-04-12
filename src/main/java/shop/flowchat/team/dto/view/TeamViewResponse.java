package shop.flowchat.team.dto.view;

import shop.flowchat.team.dto.team.response.TeamResponse;
import shop.flowchat.team.dto.teammember.response.TeamMemberResponse;

import java.util.List;

public record TeamViewResponse(
        TeamResponse team,
        List<CategoryViewResponse> categoriesView,
        List<TeamMemberResponse> teamMembers
) {
    public static TeamViewResponse from(TeamResponse team, List<CategoryViewResponse> categoriesView, List<TeamMemberResponse> teamMembers) {
        return new TeamViewResponse(
                team,
                categoriesView,
                teamMembers
        );
    }
}
