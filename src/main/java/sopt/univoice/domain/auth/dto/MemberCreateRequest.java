package sopt.univoice.domain.auth.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class MemberCreateRequest{

    private  Long admissionNumber;

    private String name;

    private String studentNumber;

    private String email;

    private String password;

    MultipartFile studentCardImage;

    private String  universityName;

    private String departmentName;


}
