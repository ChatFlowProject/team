package shop.flowchat.team.exception.custom;

import shop.flowchat.team.exception.ErrorCode;

public class ExternalServiceException extends ServiceException{

    public ExternalServiceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ExternalServiceException(String message) {
        super(ErrorCode.EXTERNAL_SERVER_ERROR, message);
    }

    public ExternalServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

}
