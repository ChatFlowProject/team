package shop.flowchat.team.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import shop.flowchat.team.presentation.dto.ApiResponse;
import shop.flowchat.team.presentation.dto.dialog.response.ChatCreateResponse;

@FeignClient(name = "dialog-service", url = "${chatflow.http-url}")
public interface DialogClient {
    @PostMapping("/chat")
    ApiResponse<ChatCreateResponse> createChat();

}
