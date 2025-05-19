package shop.flowchat.team.infrastructure.outbox.listener;

import shop.flowchat.team.infrastructure.outbox.model.EventStatus;
import shop.flowchat.team.infrastructure.outbox.model.Outbox;
import shop.flowchat.team.common.exception.custom.InternalServiceException;
import shop.flowchat.team.infrastructure.outbox.event.OutboxEvent;
import shop.flowchat.team.infrastructure.repository.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxBeforeCommitListener {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOutboxEventBeforeCommit(OutboxEvent event) {
        try {
            String eventId = UUID.randomUUID().toString();
            String jsonPayload = objectMapper.writeValueAsString(event.getPayload());
            Outbox outbox = Outbox.builder()
                    .eventId(eventId)
                    .aggregateType(event.getAggregateType())
                    .aggregateId(event.getAggregateId())
                    .eventType(event.getEventType())
                    .payload(jsonPayload)
                    .status(EventStatus.PENDING)
                    .build();
            outboxRepository.save(outbox);
            event.setEventId(eventId); // Outbox의 eventId를 이벤트에 설정 (AFTER_COMMIT 단계에서 사용)
        } catch (JsonProcessingException e) {
            throw new InternalServiceException("Payload serialization failed: " + e.getMessage());
        }
    }

}
