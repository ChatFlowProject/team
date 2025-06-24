package shop.flowchat.team.presentation.dto.dialog;

import java.util.UUID;

public record Sender(
        UUID memberId,
        String username,
        String avatarUrl
) {}
