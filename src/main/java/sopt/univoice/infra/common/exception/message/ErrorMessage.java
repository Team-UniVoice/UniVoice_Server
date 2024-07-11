package sopt.univoice.infra.common.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorMessage {

    NOT_FOUND_USER(HttpStatus.NO_CONTENT.value(), "ID에 해당하는 사용자가 없습니다."),
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 업로드에 실패했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NO_CONTENT.value(), "ID에 해당하는 사용자가 존재하지 않습니다."),
    BLOG_NOT_FOUND(HttpStatus.NO_CONTENT.value(), "ID에 해당하는 블로그가 존재하지 않습니다."),
    EMAIL_DUPLICATE(HttpStatus.NO_CONTENT.value(), "이미 사용중인 이메일 입니다.."),
    NOT_FOUND_NOTICE(HttpStatus.NO_CONTENT.value(), "ID에 해당하는 공지가 없습니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT.value(), "이미 좋아요를 눌렀습니다."),
    ALREADY_CANCELED(HttpStatus.CONFLICT.value(), "이미 좋아요를 취소했습니다."),
    ALREADY_SAVED(HttpStatus.CONFLICT.value(), "이미 저장한 공지입니다."),
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "등록을 허용하지 않는 사용자 입니다.")
    ;
    private final int status;
    private final String message;
}