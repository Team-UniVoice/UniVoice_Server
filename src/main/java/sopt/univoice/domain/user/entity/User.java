package sopt.univoice.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.infra.persistence.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", schema = "public")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userIdx;

    @Column(name = "admission_number",nullable = false)
    private Integer admissionNumber;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "student_number",nullable = false)
    private Integer studentNumber;

    @Column(name = "email", nullable = false, length = 20, unique = true) // 컬럼의 값이 중복될 수 없도록 unique = true로 설정 -> 사용자 ID 중복되지 않도록
    @Size(min = 5, max = 20) // 아이디는 5~20자
    private String email;

    @Column(name = "password", nullable = false, length = 16)
    @Size(min = 8, max = 16) // 비밀번호는 8~16자
    private String password;

    @Column(name = "student_card_image", nullable = false)
    private String studentCardImage;

    @Column(name = "university_name", nullable = false)
    private String universityName;

    @Column(name = "college_department_name", nullable = false)
    private String collegeDepartmentName;

    @Column(name = "department_name", nullable = false)
    private String departmentName;

    @Builder
    public User(Integer admissionNumber, String name, Integer studentNumber, String email, String password, String studentCardImage, String universityName, String collegeDepartmentName, String departmentName) {
        this.admissionNumber = admissionNumber;
        this.name = name;
        this.studentNumber = studentNumber;
        this.email = email;
        this.password = password;
        this.studentCardImage = studentCardImage;
        this.universityName = universityName;
        this.collegeDepartmentName = collegeDepartmentName;
        this.departmentName = departmentName;
    }

    public static User of(Integer admissionNumber, String name, Integer studentNumber, String id, String password, String studentCardImage, String universityName, String collegeDepartmentName, String departmentName) {
        return new User(admissionNumber, name, studentNumber, id, password, studentCardImage, universityName, collegeDepartmentName, departmentName);
    }
}
