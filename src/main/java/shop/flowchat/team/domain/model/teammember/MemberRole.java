package shop.flowchat.team.domain.model.teammember;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MemberRole {
    OWNER(Set.of(Permission.READ, Permission.CREATE, Permission.UPDATE, Permission.DELETE)),
    ADMIN(Set.of(Permission.READ, Permission.CREATE, Permission.UPDATE)),
    MEMBER(Set.of(Permission.READ));

    private Set<Permission> permissions;

    @JsonCreator
    public static MemberRole of(final String parameter) {
        String type = parameter.toUpperCase();
        return Arrays.stream(values())
                .filter(t -> t.toString().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 권한 유형입니다."));
    }

}
