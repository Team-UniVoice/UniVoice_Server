package sopt.univoice.infra.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessMessage {

    UNIVERSITY_GET_SUCCESS(HttpStatus.OK.value(),"대학교이름 조회에 성공하였습니다."),
    DEPARTMENT_GET_SUCCESS(HttpStatus.OK.value(),"대학교이름을 통해 학과 조회에 성공하였습니다."),
    ;
    private final int status;
    private final String message;
}
