package sopt.univoice.infra.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessMessage {

    POST_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지사항을 성공적으로 등록했습니다."),
    ;
    private final int status;
    private final String message;
}