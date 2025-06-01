package shop.flowchat.team.infrastructure.messaging.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import shop.flowchat.team.service.readmodel.MemberReadModelService;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberEventConsumer {
    private final MemberReadModelService service;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "member") // groupId는 글로벌 group-id 설정으로 통일
    public void consume(ConsumerRecord<String, String> record, @Header(name = "eventType", required = false) String eventType) {
        try {
//            OutboxEvent<MemberEventPayload> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
//            MemberEventPayload payload = event.getPayload();

            // Kafka 메시지를 역직렬화 - cf.header는 따로 추출
            MemberEventPayload payload = objectMapper.readValue(record.value(), MemberEventPayload.class);

            if (eventType == null) {
                log.warn("eventType Header is null. Skipping record: {}", record);
                return;
            }

            // eventType 기준으로 switch 분기 처리
            switch (eventType) {
                case "signUp", "memberUpdate" -> service.upsert(payload);
                case "memberDelete" -> service.delete(payload);
                default -> log.warn("Unhandled member eventType: {}", eventType);
            }

        } catch (Exception e) {
            // 예외 발생시 offset commit되지 않음 -> 따라서 위의 이벤트 처리 로직은 데이터를 멱등하게 처리해야 함
            log.error("Failed to consume event", e);
        }
    }

}