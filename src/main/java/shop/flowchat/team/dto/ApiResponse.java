package shop.flowchat.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;


public record ApiResponse<T>(
        @Schema(description = "response status", example = "OK")
        HttpStatus status,
        @Schema(description = "response message", example = "any message")
        T data,
       String message
) {
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(HttpStatus.OK, null, null);
    }

    public static <T> ApiResponse<T> success(final T data) {
        return new ApiResponse<>(HttpStatus.OK, data, null);
    }


}