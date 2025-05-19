package shop.flowchat.team.controller.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberInfoResponse(
        @Schema(description = "회원 고유키", example = "98bd5bf6-848a-43d4-8683-205523c9e359")
        UUID id,
        @Schema(description = "회원 닉네임", example = "jerry0339")
        String nickname,
        @Schema(description = "회원 이름", example = "Jerry")
        String name,
        @Schema(description = "회원 아바타 이미지 url", example = "https://snowball-bucket.s3.ap-northeast-2.amazonaws.com/f41b6bb9-3jerry.png")
        String avatarUrl,
        @Schema(description = "회원 상태", example = "ONLINE(온라인), OFFLINE(오프라인)")
        MemberState state,
        @Schema(description = "회원 가입 시기", example = "2008-04-07T15:00:00")
        LocalDateTime createdAt
) {
}
