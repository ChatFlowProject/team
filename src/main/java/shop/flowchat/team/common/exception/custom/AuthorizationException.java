package shop.flowchat.team.common.exception.custom;

import shop.flowchat.team.common.exception.ErrorCode;

public class AuthorizationException extends ServiceException {
    public AuthorizationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthorizationException(String message) {
        super(ErrorCode.ACCESS_DENIED, message);
    }

    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
