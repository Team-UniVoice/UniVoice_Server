package sopt.univoice.domain.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
