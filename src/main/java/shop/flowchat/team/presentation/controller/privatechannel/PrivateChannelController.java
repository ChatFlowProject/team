package shop.flowchat.team.presentation.controller.privatechannel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.presentation.dto.ApiResponse;
import shop.flowchat.team.presentation.dto.dialog.response.MessageResponse;
import shop.flowchat.team.presentation.dto.member.request.MemberListRequest;
import shop.flowchat.team.presentation.dto.view.PrivateChannelViewResponse;
import shop.flowchat.team.service.facade.PrivateChannelService;

import java.util.List;

@Tag(name = "Channel Service API (인증 토큰 필요)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
public class PrivateChannelController {
    private final PrivateChannelService privateChannelService;

    @Operation(summary = "친구 DM 채널 생성")
    @PostMapping("/members")
    public ApiResponse<PrivateChannelViewResponse> createPrivateChannel(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @Valid @RequestBody MemberListRequest request) {
        return ApiResponse.success(privateChannelService.addPrivateChannel(token, request));
    }

    @Operation(summary = "나의 모든 DM 채널 조회")
    @GetMapping("/me")
    public ApiResponse<List<PrivateChannelViewResponse>> getAllPrivateChannel(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ApiResponse.success(privateChannelService.getAllPrivateChannelsForMember(token));
    }

    @Operation(summary = "채널 메시지 조회")
    @GetMapping("/{channelId}/messages")
    public ApiResponse<List<MessageResponse>> getPrivateChannelView(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable("channelId") Long channelId) {
        return ApiResponse.success(privateChannelService.getPrivateChannelMessages(token, channelId));
    }

}
