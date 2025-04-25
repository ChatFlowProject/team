package shop.flowchat.team.service.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "test-topic")
    public void listen(String message) {
        log.info("Received Message: {}", message);
    }
}
