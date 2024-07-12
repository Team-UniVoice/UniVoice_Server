package sopt.univoice.domain.auth.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
