package sopt.univoice.infra.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessMessage {

    UNIVERSITY_GET_SUCCESS(HttpStatus.OK.value(),"대학교이름 조회에 성공하였습니다."),
    DEPARTMENT_GET_SUCCESS(HttpStatus.OK.value(),"대학교이름을 통해 학과 조회에 성공하였습니다."),
    EMAIL_DUPLICATE(HttpStatus.OK.value(),"이미 해당 아이디를 사용자가 사용중입니다. "),
    EMAIL_AVAILABLE(HttpStatus.OK.value(),"사용가능한 아이디입니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED.value(),"회원가입에 성공하였습니다."),
    SIGNIN_SUCCESS(HttpStatus.OK.value(),"로그인에 성공하였습니다."),
    JWT_LOGIN_SUCCESS(HttpStatus.UNAUTHORIZED.value(), "가입된 계정이 없습니다 아이디와 비밀번호를 다시 확인해주세요."),
    CREATE_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지작성에 성공하였습니다."),
    LIKE_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지 좋아요에 성공하였습니다."),
    LIKE_CANCLE_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지 좋아요 취소에 성공하였습니다."),
    SAVE_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지 저장에 성공하였습니다."),
    SAVE_CANCLE_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지 저장 취소에 성공하였습니다."),
    SAVE_ALL_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지 저장 리스트 조회에 성공하였습니다."),
    VIEW_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"공지 조회 체크에 성공하였습니다."),
    GET_QUCIK_HEAD_SUCCESS(HttpStatus.CREATED.value(),"퀵스캔 스토리 헤드 데이터 조회에 성공하였습니다."),
    GET_ALL_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"전체 공지 조회에 성공하였습니다."),
    GET_ALL_UNIVERSITY_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"총학생회 공지 조회에 성공하였습니다."),
    GET_ALL_COLLEGE_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"단과대학 학생회 공지 조회에 성공하였습니다."),
    GET_ALL_DEPARTMENT_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"학과 학생회 공지 조회에 성공하였습니다."),
    GET_QUICK_NOTICE_SUCCESS(HttpStatus.CREATED.value(),"퀵스캔 공지 조회에 성공하였습니다."),
    GET_Detail_NOTICE_SUCCESS(HttpStatus.OK.value(),"개별 공지 조회에 성공하였습니다.")
    ;
    private final int status;
    private final String message;
}
