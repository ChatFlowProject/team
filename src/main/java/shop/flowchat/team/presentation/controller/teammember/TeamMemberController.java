package shop.flowchat.team.presentation.controller.teammember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.presentation.dto.ApiResponse;
import shop.flowchat.team.presentation.dto.teammember.request.TeamMemberModifyRoleRequest;
import shop.flowchat.team.domain.teammember.MemberRole;
import shop.flowchat.team.presentation.dto.teammember.result.JoinTeamResult;
import shop.flowchat.team.service.facade.TeamFacadeService;

import java.util.UUID;

@Tag(name = "Team Member Service API (인증 토큰 필요)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/teams/{teamId}/members")
public class TeamMemberController {
    private final TeamFacadeService teamFacadeService;

    @Operation(summary = "팀 서버로 친구 초대 (바로 참여)")
    @PostMapping("/{memberId}")
    public ApiResponse<Long> inviteMember(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId,
            @PathVariable("memberId") UUID memberId) {
        return ApiResponse.success(teamFacadeService.addTeamMember(token, teamId, memberId));
    }

    @Operation(summary = "팀 서버 코드로 팀 참여(내가 참여)")
    @PostMapping
    public ApiResponse<Long> joinTeam(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId) {
        JoinTeamResult result = teamFacadeService.joinTeam(token, teamId);
        return result.isCreated()
                ? ApiResponse.created(result.id())
                : ApiResponse.success(result.id());
    }

    @Operation(summary = "팀 회원의 권한 수정")
    @PatchMapping("/{targetId}")
    public ApiResponse modifyTeamMemberRole(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId,
            @PathVariable("targetId") UUID targetId,
            @Valid @RequestBody TeamMemberModifyRoleRequest request) { // todo: 권한 체크 추가 (AuthorizationException)
        teamFacadeService.modifyTeamMemberRole(token, teamId, targetId, MemberRole.of(request.memberRole()));
        return ApiResponse.success();
    }

    @Operation(summary = "팀 서버 나가기")
    @DeleteMapping
    public ApiResponse leaveTeam(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId) {
        teamFacadeService.leaveTeam(token, teamId);
        return ApiResponse.success();
    }

    @Operation(summary = "팀 서버 회원 추방")
    @DeleteMapping("/{targetId}")
    public ApiResponse kickTeamMember(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId,
            @PathVariable("targetId") UUID targetId) { // todo: 권한 체크 추가 (AuthorizationException)
        teamFacadeService.kickTeamMember(token, teamId, targetId);
        return ApiResponse.success();
    }

}
