package shop.flowchat.team.service.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import shop.flowchat.team.dto.ApiResponse;
import shop.flowchat.team.dto.member.MemberResponse;

import java.util.UUID;

@FeignClient(name = "member-service", url = "${feign.chatflow.member}")
public interface MemberClient {
    @GetMapping("/members")
    ApiResponse<MemberResponse> getMemberInfo(@RequestHeader("Authorization") String token);

    @GetMapping("/members/{memberId}")
    ApiResponse<MemberResponse> getMemberInfo(
            @RequestHeader("Authorization") String token,
            @PathVariable("memberId") UUID memberId);

    @GetMapping("/friendships/me")
    ApiResponse<Boolean> checkFriendship(
            @RequestHeader("Authorization") String token,
            @RequestParam("friendId") UUID friendId);
}
