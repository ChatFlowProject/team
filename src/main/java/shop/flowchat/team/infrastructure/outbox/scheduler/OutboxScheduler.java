package shop.flowchat.team.infrastructure.outbox.scheduler;

import shop.flowchat.team.infrastructure.outbox.model.outbox.EventStatus;
import shop.flowchat.team.infrastructure.outbox.model.outbox.Outbox;
import shop.flowchat.team.infrastructure.repository.outbox.OutboxRepository;
import shop.flowchat.team.infrastructure.outbox.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxRepository outboxRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Scheduled(fixedDelay = 600000) // 10분 간격
    @Transactional
    public void retryPendingMessages() {
        List<Outbox> eventList = outboxRepository.findByStatusIn(List.of(EventStatus.PENDING, EventStatus.FAILED));
        for (Outbox outbox : eventList) {
            try {
                kafkaEventPublisher.sendEvent(
                        outbox.getAggregateType(),   // topic
                        outbox.getEventId(),         // eventId (header & Outbox Unique key)
                        outbox.getEventType(),       // eventType (header)
                        outbox.getAggregateId(),     // record key
                        outbox.getPayload()          // record message
                );
                outbox.markSuccess();
            } catch (Exception e) {
                outbox.markFailed();
            }
            outboxRepository.save(outbox);
        }
    }

}
