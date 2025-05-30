package shop.flowchat.team.presentation.dto.member.response;

import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

public record MemberResponse(
        UUID requester,
        List<MemberInfoResponse> memberList
) {
    public static MemberResponse from(UUID requester, List<MemberInfoResponse> memberList) {
        if(ObjectUtils.isEmpty(memberList)) memberList = List.of();
        return new MemberResponse(requester, memberList);
    }
}
