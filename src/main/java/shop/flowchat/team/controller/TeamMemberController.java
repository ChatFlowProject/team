package shop.flowchat.team.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.dto.ApiResponse;
import shop.flowchat.team.dto.teammember.TeamMemberModifyRoleRequest;
import shop.flowchat.team.entity.teammember.MemberRole;
import shop.flowchat.team.service.facade.TeamFacadeService;

import java.util.UUID;

@Tag(name = "Team Member Service API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/teams/{teamId}/members")
public class TeamMemberController {
    private final TeamFacadeService teamFacadeService;

    @Operation(summary = "팀 서버로 친구 초대")
    @PostMapping("/{memberId}")
    public ApiResponse<Long> inviteMember(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId,
            @PathVariable("memberId") UUID memberId) { // todo: 친구 초대시 바로 참여됨 -> 동작 확인후 수정
        return ApiResponse.success(teamFacadeService.addTeamMember(token, teamId, memberId));
    }

    @Operation(summary = "팀 서버 코드로 팀 참여(내가 참여)")
    @PostMapping
    public ApiResponse<Long> joinTeam(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId) {
        return ApiResponse.success(teamFacadeService.joinTeam(token, teamId));
    }

    @Operation(summary = "팀 회원의 권한 수정")
    @PatchMapping("/{memberId}")
    public ApiResponse modifyTeamMemberRole(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId,
            @PathVariable("memberId") UUID memberId,
            @Valid @RequestBody TeamMemberModifyRoleRequest request) { // todo: 구현
        teamFacadeService.modifyTeamMemberRole(token, teamId, memberId, MemberRole.of(request.memberRole()));
        return ApiResponse.success();
    }

}
