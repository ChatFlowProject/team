package shop.flowchat.team.infrastructure.outbox.publisher;

import shop.flowchat.team.common.exception.custom.KafkaEventSendException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent(String topic, String eventId, String eventType, String key, String payload) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, payload);
            record.headers().add("eventId", eventId.getBytes(StandardCharsets.UTF_8));
            record.headers().add("eventType", eventType.getBytes(StandardCharsets.UTF_8));

            kafkaTemplate.send(record).get(); // .get() -> 동기 전송: Kafka Cluster로부터 잘 전송되었는지 응답받기 위함
        } catch (Exception e) {
            throw new KafkaEventSendException("Kafka send failed: " + e.getMessage());
        }
    }

}
