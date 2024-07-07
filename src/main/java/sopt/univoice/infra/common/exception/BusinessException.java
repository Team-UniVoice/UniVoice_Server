package sopt.univoice.infra.common.exception;

import sopt.univoice.infra.common.exception.message.ErrorMessage;

public class BusinessException extends RuntimeException {
    private ErrorMessage errorMessage;

    public BusinessException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
