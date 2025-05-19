package shop.flowchat.team.controller.dto.teammember.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TeamMemberModifyRoleRequest(
        @NotNull(message = "변경할 회원의 권한을 입력해 주세요")
        @Schema(description = "팀 서버 회원 권한", example = "MEMBER / ADMIN / OWNER")
        String memberRole
) {
}
