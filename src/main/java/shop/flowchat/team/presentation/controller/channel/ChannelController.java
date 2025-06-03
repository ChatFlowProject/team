package shop.flowchat.team.presentation.controller.channel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.presentation.dto.ApiResponse;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.presentation.dto.channel.request.ChannelMoveRequest;
import shop.flowchat.team.presentation.dto.channel.response.ChannelResponse;
import shop.flowchat.team.presentation.dto.view.CategoryViewResponse;
import shop.flowchat.team.service.facade.TeamFacadeService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Team Channel Service API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/teams/{teamId}/categories/{categoryId}/channels")
public class ChannelController {
    private final TeamFacadeService teamFacadeService;

    @Operation(summary = "팀에 채널 생성")
    @PostMapping
    public ApiResponse<ChannelResponse> createChannel(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody ChannelCreateRequest request) { // todo: 권한 체크 추가 (AuthorizationException)
        return ApiResponse.success(teamFacadeService.addChannel(teamId, categoryId, request));
    }

    @Operation(summary = "팀의 채널 위치 수정")
    @PatchMapping("/{channelId}")
    public ApiResponse<List<CategoryViewResponse>> moveChannel(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("categoryId") Long categoryId,
            @PathVariable("channelId") Long channelId,
            @Valid @RequestBody ChannelMoveRequest request) { // todo: 권한 체크 추가 (AuthorizationException)
        return ApiResponse.success(teamFacadeService.moveChannel(teamId, categoryId, channelId, request));
    }

    @Operation(summary = "팀의 채널 삭제")
    @DeleteMapping("/{channelId}")
    public ApiResponse deleteCategory(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("categoryId") Long categoryId,
            @PathVariable("channelId") Long channelId) { // todo: 권한 체크 추가 (AuthorizationException)
        teamFacadeService.deleteChannel(teamId, categoryId, channelId);
        return ApiResponse.success();
    }

}
