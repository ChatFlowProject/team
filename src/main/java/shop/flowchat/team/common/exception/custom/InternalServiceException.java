package shop.flowchat.team.common.exception.custom;

import shop.flowchat.team.common.exception.ErrorCode;

public class InternalServiceException extends ServiceException {

    public InternalServiceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InternalServiceException(String message) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, message);
    }

    public InternalServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

}
