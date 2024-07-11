package sopt.univoice.infra.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessMessage {

    POST_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지사항을 성공적으로 등록했습니다."),
    UPLOAD_SUCCESS(HttpStatus.OK.value(), "이미지를 성공적으로 업로드했습니다."),
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 업로드에 실패했습니다."),
    UNIVERSITY_GET_SUCCESS(HttpStatus.OK.value(),"대학교이름 조회에 성공하였습니다."),
    DEPARTMENT_GET_SUCCESS(HttpStatus.OK.value(),"대학교이름을 통해 학과 조회에 성공하였습니다."),
    EMAIL_DUPLICATE(HttpStatus.OK.value(),"이미 해당 아이디를 사용자가 사용중입니다. "),
    EMAIL_AVAILABLE(HttpStatus.OK.value(),"사용가능한 아이디입니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED.value(),"회원가입에 성공하였습니다."),
    GET_NOICE_SUCCESS(HttpStatus.OK.value(), "공지를 성공적으로 조회했습니다."),
    POST_LIKE_SUCCESS(HttpStatus.CREATED.value(),"좋아요를 성공적으로 등록했습니다.")
    ;
    private final int status;
    private final String message;
}