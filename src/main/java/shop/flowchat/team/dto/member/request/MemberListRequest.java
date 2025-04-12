package shop.flowchat.team.dto.member.request;

import java.util.List;
import java.util.UUID;

public record MemberListRequest(
        List<UUID> memberIds
) {
    public static MemberListRequest from(List<UUID> memberIds) {
        return new MemberListRequest(memberIds);
    }
}
