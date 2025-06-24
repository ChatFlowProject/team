package shop.flowchat.team.presentation.dto.dialog.response;

import shop.flowchat.team.presentation.dto.dialog.AttachmentDto;
import shop.flowchat.team.presentation.dto.dialog.Sender;

import java.time.LocalDateTime;
import java.util.List;

public record MessageResponse(
    Long messageId,
    Sender sender,
    String content,
    LocalDateTime createdAt,
    Boolean isUpdated,
    Boolean isDeleted,
    List<AttachmentDto> attachments
) {
}
