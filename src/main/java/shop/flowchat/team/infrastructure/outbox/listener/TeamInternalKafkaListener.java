package shop.flowchat.team.infrastructure.outbox.listener;

import shop.flowchat.team.infrastructure.outbox.model.outbox.EventStatus;
import shop.flowchat.team.infrastructure.repository.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamInternalKafkaListener {
    private final OutboxRepository outboxRepository;

    @KafkaListener(topics = "team", groupId = "team-consumer-group")
    public void consumeMemberEvents(String message, @Header("eventId") String eventId) {
        outboxRepository.findByEventId(eventId).ifPresent(outbox -> {
            if (outbox.getStatus() != EventStatus.SUCCESS) {
                outbox.markSuccess();
                outboxRepository.save(outbox);
            }
        });
    }

}
