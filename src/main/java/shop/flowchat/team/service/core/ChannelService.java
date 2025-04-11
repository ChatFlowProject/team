package shop.flowchat.team.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.dto.channel.ChannelCreateRequest;
import shop.flowchat.team.entity.category.Category;
import shop.flowchat.team.entity.channel.Channel;
import shop.flowchat.team.repository.ChannelRepository;

@RequiredArgsConstructor
@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Transactional
    public Long createChannel(ChannelCreateRequest request, Category category) {
        Channel channel = Channel.from(request, category);
        try {
            channelRepository.save(channel);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다.: " + e.getMessage());
        }
        return channel.getId();
    }

}
