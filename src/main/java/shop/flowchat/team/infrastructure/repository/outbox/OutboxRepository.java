package shop.flowchat.team.infrastructure.repository.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.infrastructure.outbox.model.outbox.EventStatus;
import shop.flowchat.team.infrastructure.outbox.model.outbox.Outbox;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findByStatusIn(Collection<EventStatus> statuses);
    Optional<Outbox> findByEventId(String eventId);

}
