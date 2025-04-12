package shop.flowchat.team.service.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.dto.ApiResponse;
import shop.flowchat.team.dto.member.request.MemberListRequest;
import shop.flowchat.team.dto.member.response.MemberInfoResponse;
import shop.flowchat.team.dto.member.response.MemberResponse;

import java.util.UUID;

@FeignClient(name = "member-service", url = "${feign.chatflow.member}")
public interface MemberClient {
    @GetMapping("/members")
    ApiResponse<MemberInfoResponse> getMemberInfo(@RequestHeader("Authorization") String token);

    @PostMapping("/members/search")
    ApiResponse<MemberResponse> getMemberInfoList(
            @RequestHeader("Authorization") String token,
            @RequestBody MemberListRequest request);

    @GetMapping("/friendships/me")
    ApiResponse<Boolean> checkFriendship(
            @RequestHeader("Authorization") String token,
            @RequestParam("friendId") UUID friendId);
}
