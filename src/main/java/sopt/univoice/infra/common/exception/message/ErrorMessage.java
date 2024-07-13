package sopt.univoice.infra.common.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorMessage {
    MEMBER_NOT_FOUND(HttpStatus.NO_CONTENT.value(), "ID에 해당하는 사용자가 존재하지 않습니다."),
    BLOG_NOT_FOUND(HttpStatus.NO_CONTENT.value(), "ID에 해당하는 블로그가 존재하지 않습니다."),
    EMAIL_DUPLICATE(HttpStatus.NO_CONTENT.value(), "이미 사용중인 이메일 입니다."),
    JWT_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "사용자의 로그인 검증을 실패했습니다."),
    APPROVEADMIN_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "승인된 관리자의 로그인 검증을 실패했습니다."),
    ALREADY_SAVED(HttpStatus.CONFLICT.value(), "이미 저장한 공지입니다."),
    ;
    private final int status;
    private final String message;
}
