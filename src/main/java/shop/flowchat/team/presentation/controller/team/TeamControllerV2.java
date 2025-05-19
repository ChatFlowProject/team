package shop.flowchat.team.presentation.controller.team;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.presentation.dto.ApiResponse;
import shop.flowchat.team.presentation.dto.team.request.TeamCreateRequest;
import shop.flowchat.team.presentation.dto.team.response.TeamCreateResponse;
import shop.flowchat.team.service.facade.TeamFacadeServiceV2;

@Tag(name = "Team Service API (인증 토큰 필요)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/teams")
public class TeamControllerV2 {
    private final TeamFacadeServiceV2 teamFacadeService;

    @Operation(summary = "팀 서버 생성 V2")
    @PostMapping
    public ApiResponse<TeamCreateResponse> createTeam(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @Valid @RequestBody TeamCreateRequest request) {
        return ApiResponse.success(teamFacadeService.initializeTeam(token, request));
    }

}
