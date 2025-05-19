package shop.flowchat.team.presentation.dto.teammember.response;

import io.swagger.v3.oas.annotations.media.Schema;
import shop.flowchat.team.presentation.dto.member.response.MemberInfoResponse;
import shop.flowchat.team.domain.teammember.MemberRole;
import shop.flowchat.team.domain.teammember.TeamMember;



public record TeamMemberResponse(
        @Schema(description = "팀 멤버 고유키", example = "8")
        Long id,
        @Schema(description = "팀 멤버 권한", example = "ADMIN")
        MemberRole role,
        MemberInfoResponse memberInfo
) {
    public static TeamMemberResponse from(TeamMember teamMember, MemberInfoResponse member) {
        return new TeamMemberResponse(
                teamMember.getId(),
                teamMember.getRole(),
                member
        );
    }
}
