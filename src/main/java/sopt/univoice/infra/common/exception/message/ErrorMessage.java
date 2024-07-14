package sopt.univoice.infra.common.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorMessage {
    EMAIL_DUPLICATE(HttpStatus.CONFLICT.value(), "이미 사용중인 아이디 입니다."),
    JWT_LOGIN_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "가입된 계정이 없습니다 아이디와 비밀번호를 다시 확인해주세요."),
    JWT_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "사용자의 로그인 검증을 실패했습니다."),
    APPROVEADMIN_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "승인된 관리자의 로그인 검증을 실패했습니다."),
    ;
    private final int status;
    private final String message;
}
