package shop.flowchat.team.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.dto.ApiResponse;
import shop.flowchat.team.dto.team.TeamCreateRequest;
import shop.flowchat.team.dto.team.TeamSimpleResponse;
import shop.flowchat.team.dto.team.TeamViewResponse;
import shop.flowchat.team.service.facade.TeamFacadeService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Team Service API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamFacadeService teamFacadeService;

    @Operation(summary = "팀 서버 생성")
    @PostMapping
    public ApiResponse<UUID> createTeam(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @Valid @RequestBody TeamCreateRequest request) {
        return ApiResponse.success(teamFacadeService.initializeTeam(token, request));
    }

    @Operation(summary = "회원이 참여중인 모든 팀 서버 조회")
    @GetMapping
    public ApiResponse<List<TeamSimpleResponse>> getAllTeams(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ApiResponse.success(teamFacadeService.getAllTeams(token));
    }

    @Operation(summary = "팀 서버 상세 조회 (미구현)")
    @GetMapping("/{teamId}")
    public ApiResponse<TeamViewResponse> getTeamView(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("teamId") UUID teamId) {
        return ApiResponse.success(teamFacadeService.getTeamView(token, teamId));
    }

}
