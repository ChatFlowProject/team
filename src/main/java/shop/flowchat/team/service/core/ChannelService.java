package shop.flowchat.team.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.entity.category.Category;
import shop.flowchat.team.entity.channel.Channel;
import shop.flowchat.team.exception.common.AuthorizationException;
import shop.flowchat.team.exception.common.EntityNotFoundException;
import shop.flowchat.team.repository.ChannelRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Transactional
    public Channel createChannel(ChannelCreateRequest request, Category category) {
        Channel channel = Channel.from(request, category);
        Double maxPosition = channelRepository.findMaxPositionByCategoryId(category.getId());
        channel.movePositionBetween(maxPosition, maxPosition + 2000.0);
        try {
            channelRepository.save(channel);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다.");
        }
        return channel;
    }

    @Transactional(readOnly = true)
    public List<Channel> getChannelByCategoryIds(List<Long> categoryIds) {
        return channelRepository.findByCategoryIdIn(categoryIds);
    }

    @Transactional(readOnly = true)
    public Channel getChannelById(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 채널입니다."));
    }

    @Transactional(readOnly = true)
    public Channel validateCategoryChannel(Long categoryId, Long channelId) {
        Channel channel = getChannelById(channelId);
        if(!channel.getCategory().getId().equals(categoryId)) {
            throw new AuthorizationException("채널이 위치한 카테고리 ID와 일치하지 않습니다.");
        }
        return channel;
    }

    @Transactional
    public void deleteChannelsByCategory(Category category) {
        channelRepository.deleteByCategory(category);
    }

    @Transactional
    public void deleteChannel(Channel channel) {
        channelRepository.delete(channel);
    }
}
