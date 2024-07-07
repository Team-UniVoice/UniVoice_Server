package sopt.univoice.infra.common.exception;

import sopt.univoice.infra.common.exception.message.ErrorMessage;

public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
