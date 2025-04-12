package shop.flowchat.team.dto.member.response;

import java.util.List;
import java.util.UUID;

public record MemberResponse(
        UUID requester,
        List<MemberInfoResponse> memberList
) {
    public static MemberResponse from(UUID requester, List<MemberInfoResponse> memberList) {
        return new MemberResponse(requester, memberList);
    }
}
