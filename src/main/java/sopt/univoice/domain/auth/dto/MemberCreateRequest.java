package sopt.univoice.domain.auth.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberCreateRequest{

    @NotNull
    private  Long admissionNumber;

    @NotEmpty
    private String name;

    @NotEmpty
    private String studentNumber;

    @NotEmpty
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{5,20}$", message = "영문 소문자, 숫자, 특수문자 사용 5~20자 여야 합니다.")
    private String email;

    @NotEmpty
    @Size(min = 8, max = 16)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$", message = "영문 소문자, 숫자, 특수문자 각각 1개 이상 포함 8~16자 여야 합니다.")
    private String password;

    @NotNull
    MultipartFile studentCardImage;

    @NotEmpty
    private String  universityName;

    @NotEmpty
    private String departmentName;
}
