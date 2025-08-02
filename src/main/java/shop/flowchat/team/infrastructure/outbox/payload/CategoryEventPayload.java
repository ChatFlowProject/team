package shop.flowchat.team.infrastructure.outbox.payload;

import java.time.LocalDateTime;
import java.util.UUID;
import shop.flowchat.team.domain.category.Category;

public record CategoryEventPayload(
        Long id,
        String name,
        UUID teamId,
        LocalDateTime timestamp
) {
    public static CategoryEventPayload from(Category category) {
        return new CategoryEventPayload(
                category.getId(),
                category.getName(),
                category.getTeam().getId(),
                LocalDateTime.now()
        );
    }
}
