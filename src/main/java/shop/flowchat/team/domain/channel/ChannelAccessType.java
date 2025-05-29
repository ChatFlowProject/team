package shop.flowchat.team.domain.channel;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChannelAccessType {
    PUBLIC, PRIVATE;

    @JsonCreator
    public static ChannelAccessType of(final String parameter) {
        String type = parameter.toUpperCase();
        return Arrays.stream(values())
                .filter(t -> t.toString().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 채널 접근 유형입니다."));
    }

}
