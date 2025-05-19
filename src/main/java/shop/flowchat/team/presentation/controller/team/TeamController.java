package shop.flowchat.team.presentation.controller.team;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.presentation.dto.ApiResponse;
import shop.flowchat.team.presentation.dto.team.request.TeamCreateRequest;
import shop.flowchat.team.presentation.dto.team.request.TeamUpdateRequest;
import shop.flowchat.team.presentation.dto.team.response.TeamCreateResponse;
import shop.flowchat.team.presentation.dto.team.response.TeamResponse;
import shop.flowchat.team.presentation.dto.view.TeamViewResponse;
import shop.flowchat.team.service.facade.TeamFacadeService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Team Service API (인증 토큰 필요)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamFacadeService teamFacadeService;

    @Operation(summary = "팀 서버 생성")
    @PostMapping
    public ApiResponse<TeamCreateResponse> createTeam(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @Valid @RequestBody TeamCreateRequest request) {
        return ApiResponse.success(teamFacadeService.initializeTeam(token, request));
    }

    @Operation(summary = "회원이 참여중인 모든 팀 서버 조회")
    @GetMapping
    public ApiResponse<List<TeamResponse>> getAllTeams(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ApiResponse.success(teamFacadeService.getAllTeams(token));
    }

    @Operation(summary = "팀 서버 상세 조회")
    @GetMapping("/{teamId}")
    public ApiResponse<TeamViewResponse> getTeamView(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId) {
        return ApiResponse.success(teamFacadeService.getTeamView(token, teamId));
    }

    @Operation(summary = "팀 서버 정보 수정")
    @PutMapping("/{teamId}")
    public ApiResponse<TeamResponse> updateTeam(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId,
            @Valid @RequestBody TeamUpdateRequest request) {
        return ApiResponse.success(teamFacadeService.updateTeam(token, teamId, request));
    }

    @Operation(summary = "팀 서버 삭제 (팀 회원까지 모두)")
    @DeleteMapping("/{teamId}")
    public ApiResponse deleteTeam(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId) {
        teamFacadeService.deleteTeam(token, teamId);
        return ApiResponse.success();
    }

}
