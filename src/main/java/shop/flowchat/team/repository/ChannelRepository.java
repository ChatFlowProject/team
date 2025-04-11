package shop.flowchat.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.entity.channel.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

}
