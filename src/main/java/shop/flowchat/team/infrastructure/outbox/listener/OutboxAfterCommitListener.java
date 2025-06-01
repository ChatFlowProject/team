package shop.flowchat.team.infrastructure.outbox.listener;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.infrastructure.outbox.model.outbox.Outbox;
import shop.flowchat.team.common.exception.custom.EntityNotFoundException;
import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.outbox.publisher.KafkaEventPublisher;
import shop.flowchat.team.infrastructure.repository.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OutboxAfterCommitListener {
    private final OutboxRepository outboxRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOutboxEventAfterCommit(OutboxEvent event) {
        String eventId = event.getEventId();
        Outbox outbox = outboxRepository.findByEventId(eventId)
                .orElseThrow(() -> new EntityNotFoundException("eventId에 대한 OutBox 데이터가 존재하지 않습니다."));

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
