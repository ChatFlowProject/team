package shop.flowchat.team.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.dto.ApiResponse;
import shop.flowchat.team.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.dto.channel.response.ChannelCreateResponse;
import shop.flowchat.team.service.facade.TeamFacadeService;

import java.util.UUID;

@Tag(name = "Channel Service API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/teams/{teamId}/categories/{categoryId}/channels")
public class ChannelController {
    private final TeamFacadeService teamFacadeService;
    // todo: 토큰 헤더 추가 및 권한 체크 로직 추가

    @Operation(summary = "채널 생성")
    @PostMapping
    public ApiResponse<ChannelCreateResponse> createChannel(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody ChannelCreateRequest request) {
        return ApiResponse.success(teamFacadeService.addChannel(teamId, categoryId, request));
    }

}
