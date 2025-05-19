package shop.flowchat.team.infrastructure.messaging.member;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.readmodel.member.MemberReadModelUpdater;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberEventConsumer {
    private final MemberReadModelUpdater updater;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "member") // groupId는 글로벌 group-id 설정으로 통일
    public void consume(ConsumerRecord<String, String> record) {
        try {
            OutboxEvent<MemberEventPayload> event = parse(record.value());
            String eventType = event.getEventType();
            MemberEventPayload payload = event.getPayload();

            switch (eventType) {
                case "memberCreate", "memberUpdated" -> updater.upsert(payload);
                case "memberDeleted" -> updater.delete(payload.id());
                case "memberModifyStatus" -> updater.updateStatus(payload.id(), payload.state());
                default -> log.warn("Unhandled eventType: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Failed to consume event", e);
            // DLQ or retry logic could be added here
        }
    }

    private OutboxEvent<MemberEventPayload> parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse event JSON", e);
        }
    }
}
