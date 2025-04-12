package shop.flowchat.team.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.dto.channel.request.ChannelCreateRequest;
import shop.flowchat.team.entity.category.Category;
import shop.flowchat.team.entity.channel.Channel;
import shop.flowchat.team.repository.ChannelRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Transactional
    public Channel createChannel(ChannelCreateRequest request, Category category) {
        Channel channel = Channel.from(request, category);
        try {
            channelRepository.save(channel);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다.: " + e.getMessage());
        }
        return channel;
    }

    @Transactional(readOnly = true)
    public List<Channel> getChannelByCategoryIds(List<Long> categoryIds) {
        return channelRepository.findByCategoryIdIn(categoryIds);
    }

}
