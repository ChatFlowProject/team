package shop.flowchat.team.infrastructure.outbox.model.outbox;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.flowchat.team.domain.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "outbox", uniqueConstraints = @UniqueConstraint(columnNames = "eventId"))
public class Outbox extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String eventId; // 외부 메시지 식별자 (UUID) - AFTER_COMMIT 단계에서 Outbox 조회에 사용

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(length = 800, nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Builder
    private Outbox(String eventId, String aggregateType, String aggregateId, String eventType, String payload, EventStatus status) {
        this.eventId = eventId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
    }

    public void markSuccess() {
        this.status = EventStatus.SUCCESS;
    }

    public void markFailed() {
        this.status = EventStatus.FAILED;
    }

}
