package sopt.univoice.infra.common.exception;

import sopt.univoice.infra.common.exception.message.BusinessException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
