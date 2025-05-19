package shop.flowchat.team.presentation.dto.view;

import org.springframework.util.ObjectUtils;
import shop.flowchat.team.presentation.dto.category.response.CategoryResponse;
import shop.flowchat.team.presentation.dto.channel.response.ChannelResponse;

import java.util.List;

public record CategoryViewResponse(
        CategoryResponse category,
        List<ChannelResponse> channels
) {
    public static CategoryViewResponse from(CategoryResponse category, List<ChannelResponse> channels) {
        if(ObjectUtils.isEmpty(channels)) channels = List.of();
        return new CategoryViewResponse(category, channels);
    }
}
