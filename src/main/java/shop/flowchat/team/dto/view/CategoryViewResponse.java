package shop.flowchat.team.dto.view;

import shop.flowchat.team.dto.category.response.CategoryResponse;
import shop.flowchat.team.dto.channel.response.ChannelResponse;

import java.util.List;

public record CategoryViewResponse(
        CategoryResponse category,
        List<ChannelResponse> channels
) {
    public static CategoryViewResponse from(CategoryResponse category, List<ChannelResponse> channels) {
        return new CategoryViewResponse(category, channels);
    }
}
