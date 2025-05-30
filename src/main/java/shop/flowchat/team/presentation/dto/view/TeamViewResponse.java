package shop.flowchat.team.presentation.dto.view;

import org.springframework.util.ObjectUtils;
import shop.flowchat.team.presentation.dto.team.response.TeamResponse;
import shop.flowchat.team.presentation.dto.teammember.response.TeamMemberResponse;

import java.util.List;

public record TeamViewResponse(
        TeamResponse team,
        List<CategoryViewResponse> categoriesView,
        List<TeamMemberResponse> teamMembers
) {
    public static TeamViewResponse from(TeamResponse team, List<CategoryViewResponse> categoriesView, List<TeamMemberResponse> teamMembers) {
        if(ObjectUtils.isEmpty(categoriesView)) categoriesView = List.of();
        if(ObjectUtils.isEmpty(teamMembers)) teamMembers = List.of();
        return new TeamViewResponse(
                team,
                categoriesView,
                teamMembers
        );
    }
}
