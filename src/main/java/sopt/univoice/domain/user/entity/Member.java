package sopt.univoice.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.affiliation.entity.Affiliation;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.infra.persistence.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member", schema = "public")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "admission_number")
    private Long admissionNumber;

    private String name;

    @Column(name = "student_number")
    private String studentNumber;

    private String email;

    private String password;

    @Column(name = "student_card_image")
    private String studentCardImage;

    @Column(name = "university_name")
    private String universityName;

    @Column(name = "college_department_name")
    private String collegeDepartmentName;

    @Column(name = "department_name")
    private String departmentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id")
    private Affiliation affiliation;

    @OneToMany(mappedBy = "member")
    private List<Notice> notices = new ArrayList<>();

    @Builder
    public Member(Long admissionNumber, String name, String studentNumber, String email, String password, String studentCardImage, String collegeDepartmentName, String universityName, String departmentName) {
        this.admissionNumber = admissionNumber;
        this.name = name;
        this.studentNumber = studentNumber;
        this.email = email;
        this.password = password;
        this.studentCardImage = studentCardImage;
        this.collegeDepartmentName = collegeDepartmentName;
        this.universityName = universityName;
        this.departmentName = departmentName;
    }

    public static Member of(Long admissionNumber, String name, String studentNumber, String id, String password, String studentCardImage, String universityName, String collegeDepartmentName, String departmentName) {
        return new Member(admissionNumber, name, studentNumber, id, password, studentCardImage, universityName, collegeDepartmentName, departmentName);
    }
}
