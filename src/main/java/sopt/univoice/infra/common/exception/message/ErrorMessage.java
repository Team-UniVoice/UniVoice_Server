package sopt.univoice.infra.common.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorMessage {
    NOT_FOUND_USER(HttpStatus.NO_CONTENT.value(), "ID에 해당하는 사용자가 없습니다."),
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 업로드에 실패했습니다."),
    NOT_FOUND_NOTICE(HttpStatus.NO_CONTENT.value(), "ID에 해당하는 공지가 없습니다.")
    ;
    private final int status;
    private final String message;
}