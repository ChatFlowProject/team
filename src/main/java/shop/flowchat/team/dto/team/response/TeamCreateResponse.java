package shop.flowchat.team.dto.team.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record TeamCreateResponse(
        @Schema(description = "팀 서버 고유키(= 초대 코드)", example = "ef7595ed-a432-4954-b009-5bc2ef5cb769")
        UUID teamId
) {
    public static TeamCreateResponse from(UUID id) {
        return new TeamCreateResponse(id);
    }
}
