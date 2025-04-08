package shop.flowchat.team.exception;

import lombok.Getter;

@Getter
public class S3Exception extends RuntimeException {

    private ErrorCode errorCode;

    public S3Exception(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public S3Exception(String message) {
        super(message);
    }

    public S3Exception(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}