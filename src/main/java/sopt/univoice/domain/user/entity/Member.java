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
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private  Long admissionNumber;

    private String name;

    private String studentNumber;

    private String email;

    private String password;

    private String studentCardImage;

    private String  universityName;

    private String collegeDepartmentName;

    private String departmentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id")
    private Affiliation affiliation;

    @OneToMany(mappedBy = "member")
    private List<Notice> notices = new ArrayList<>();

    @Builder
    public Member(Long admissionNumber, String name, String studentNumber, String email, String password, String studentCardImage,String collegeDepartmentName, String universityName, String departmentName) {
        this.admissionNumber = admissionNumber;
        this.name = name;
        this.studentNumber = studentNumber;
        this.email = email;
        this.password = password;
        this.studentCardImage = studentCardImage;
        this.collegeDepartmentName=collegeDepartmentName;
        this.universityName = universityName;
        this.departmentName = departmentName;
    }

    public static Member of(Long admissionNumber, String name, String studentNumber, String id, String password, String studentCardImage, String universityName, String collegeDepartmentName, String departmentName) {
        return new Member(admissionNumber, name, studentNumber, id, password, studentCardImage, universityName, collegeDepartmentName, departmentName);
    }
}
