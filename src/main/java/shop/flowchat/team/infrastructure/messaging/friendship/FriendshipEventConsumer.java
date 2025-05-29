package shop.flowchat.team.infrastructure.messaging.friendship;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import shop.flowchat.team.readmodel.friendship.FriendshipReadModelUpdater;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendshipEventConsumer {
    private final FriendshipReadModelUpdater updater;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "friendship") // groupId는 글로벌 group-id 설정으로 통일
    public void consume(ConsumerRecord<String, String> record, @Header(name = "eventType", required = false) String eventType) {
        try {
            FriendshipEventPayload payload = objectMapper.readValue(record.value(), FriendshipEventPayload.class);

            if (eventType == null) {
                log.warn("eventType Header is null. Skipping record: {}", record);
                return;
            }

            switch (eventType) {
                case "friendshipEstablished", "friendshipAccept" -> updater.upsert(payload);
                case "friendshipDelete" -> updater.delete(payload);
                default -> log.warn("Unhandled friendship eventType: {}", eventType);
            }

        } catch (Exception e) {
            // 예외 발생시 offset commit되지 않음 -> 따라서 위의 이벤트 처리 로직(switch문)은 unique키를 이용하거나 upsert 방식을 사용하여 데이터를 처리해야 함
            log.error("Failed to consume event", e);
        }
    }

}