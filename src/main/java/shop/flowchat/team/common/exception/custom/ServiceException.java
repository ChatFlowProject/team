package shop.flowchat.team.common.exception.custom;

import lombok.Getter;
import shop.flowchat.team.common.exception.ErrorCode;


@Getter
public class ServiceException extends RuntimeException {

    private ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
