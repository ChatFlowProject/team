package shop.flowchat.team.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import shop.flowchat.team.presentation.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.presentation.dto.channel.request.ChannelMoveRequest;
import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.common.exception.custom.AuthorizationException;
import shop.flowchat.team.common.exception.custom.EntityNotFoundException;
import shop.flowchat.team.infrastructure.repository.ChannelRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Transactional
    public Channel createChannel(ChannelCreateRequest request, Category category) {
        Channel channel = Channel.from(request, category);
        Double maxPosition = channelRepository.findMaxPositionByCategoryId(category.getId());
        channel.movePosition(category, maxPosition, maxPosition + 2000.0);
        try {
            channelRepository.save(channel);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다.");
        }
        return channel;
    }

    @Transactional(readOnly = true)
    public List<Channel> getChannelByCategoryIds(List<Long> categoryIds) {
        if (ObjectUtils.isEmpty(categoryIds)) return List.of();
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
        if (!channel.getCategory().getId().equals(categoryId)) {
            throw new AuthorizationException("채널의 카테고리 ID가 올바르지 않습니다.");
        }
        return channel;
    }

    @Transactional(readOnly = true)
    public List<Channel> validateCategoryChannels(List<Long> categoryIds, List<Long> channelIds) {
        List<Channel> channels = channelRepository.findByIdIn(channelIds);
        if (channels.size() != channelIds.size()) {
            throw new EntityNotFoundException("존재하지 않는 채널입니다.");
        }
        // todo: 쿼리 왜 나감?
        boolean invalidAccess = channels.stream()
                .anyMatch(channel -> categoryIds.stream()
                        .noneMatch(categoryId -> categoryId.equals(channel.getCategory().getId()))
                );
        if (invalidAccess) {
            throw new AuthorizationException("채널의 카테고리 ID가 올바르지 않습니다.");
        }
        return channels;
    }

    @Transactional
    public void moveChannel(Long channelId, Category destCategory, List<Channel> channels, ChannelMoveRequest request) {
        Map<Long, Channel> channelMap = channels.stream()
                .collect(Collectors.toMap(Channel::getId, Function.identity()));

        Channel movingChannel = channelMap.get(channelId);
        Channel prevChannel = channelMap.get(request.prevChannelId());
        Channel nextChannel = channelMap.get(request.nextChannelId());

        if (request.nextChannelId() == 0) {
            movingChannel.movePosition(destCategory, prevChannel.getPosition(), prevChannel.getPosition() + 2000.0);
        } else if (request.prevChannelId() == 0) {
            movingChannel.movePosition(destCategory, 0.0, nextChannel.getPosition());
        } else {
            movingChannel.movePosition(destCategory, prevChannel.getPosition(), nextChannel.getPosition());
        }
    }

    @Transactional
    public void deleteAllChannelsByCategories(List<Category> categories) {
        if (ObjectUtils.isEmpty(categories)) return;
        List<Long> categoryIds = categories.stream()
                .map(Category::getId) // Category 엔티티에서 ID 추출
                .collect(Collectors.toList());
        channelRepository.deleteByCategoryIdsIn(categoryIds);
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
