package shop.flowchat.team.presentation.dto.member.request;

import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

public record MemberListRequest(
        List<UUID> memberIds
) {
    public static MemberListRequest from(List<UUID> memberIds) {
        if(ObjectUtils.isEmpty(memberIds)) memberIds = List.of();
        return new MemberListRequest(memberIds);
    }
}
