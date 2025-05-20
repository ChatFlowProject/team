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

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberEventConsumer {
    private final MemberReadModelUpdater updater;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "member") // groupId는 글로벌 group-id 설정으로 통일
    public void consume(ConsumerRecord<String, String> record) {
        try {
            // Kafka 메시지를 단일 OutboxEvent<MemberEventPayload> 로 역직렬화 - cf.header는 따로 추출해야 함
            OutboxEvent<MemberEventPayload> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
            MemberEventPayload payload = event.getPayload();

            // eventType은 Kafka Header에서 직접 추출 (발행할때 header에 담았기 때문)
            String eventType = getHeader(record, "eventType");

            // eventType 기준으로 switch 분기 처리
            switch (eventType) {
                case "signUp", "memberUpdate" -> updater.upsert(payload);
                case "memberDelete" -> updater.delete(payload.id());
                case "memberModifyStatus" -> updater.updateStatus(payload.id(), payload.state());
                default -> log.warn("Unhandled eventType: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Failed to consume event", e);
        }
    }

    private String getHeader(ConsumerRecord<String, String> record, String key) {
        var header = record.headers().lastHeader(key);
        return header != null ? new String(header.value(), StandardCharsets.UTF_8) : "none";
    }

}