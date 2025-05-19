package shop.flowchat.team.readmodel.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MemberReadModelState {
    ONLINE("온라인"),
    IDLE("자리비움"),
    DO_NOT_DISTURB("방해금지"),
    OFFLINE("오프라인");

    private String name;

    @JsonCreator
    public static MemberReadModelState of(final String parameter) {
        String state = parameter.toUpperCase(); // 특문 포함 모든 문자 동작됨
        return Arrays.stream(values())
                .filter(type -> type.toString().equals(state) || type.getName().equals(state)) // toString: ONLINE, IDLE... / name: 온라인, 자리비움...
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 상태입니다."));
    }
}
