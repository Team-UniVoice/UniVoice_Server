package sopt.univoice.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMypageReponseDto {
    private Long id;
    private String name;
    private String collegeDepartment;
    private String department;
    private String admissionNumber;
    private String university;
    private String universityLogoImage;
}
