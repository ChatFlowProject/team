package shop.flowchat.team.presentation.dto.team.response;

import io.swagger.v3.oas.annotations.media.Schema;
import shop.flowchat.team.domain.team.Team;

import java.util.UUID;

public record TeamResponse(
        @Schema(description = "팀 서버 고유키", example = "ef7595ed-a432-4954-b009-5bc2ef5cb769")
        UUID id,
        @Schema(description = "팀 서버 이름", example = "차느님의 서버")
        String name,
        @Schema(description = "팀 마스터 고유키(회원키)", example = "f3ca6705-9cde-4d6c-a3bf-eb89703ac1d7")
        UUID masterId,
        @Schema(description = "팀 서버 아이콘 이미지 url", example = "https://snowball-bucket.s3.ap-northeast-2.amazonaws.com/f41b6bb9-3jerry.png")
        String iconUrl
) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getMasterId(),
                team.getIconUrl()
        );
    }
}
