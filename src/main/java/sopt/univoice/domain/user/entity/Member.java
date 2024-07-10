package sopt.univoice.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.affiliation.entity.Affiliation;
import sopt.univoice.infra.persistence.BaseTimeEntity;


@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder
    public Member(Long admissionNumber, String name, String studentNumber, String email, String password, String studentCardImage,
                  String universityName, String collegeDepartmentName, String departmentName, Affiliation affiliation) {
        this.admissionNumber = admissionNumber;
        this.name = name;
        this.studentNumber = studentNumber;
        this.email = email;
        this.password = password;
        this.studentCardImage = studentCardImage;
        this.universityName = universityName;
        this.collegeDepartmentName = collegeDepartmentName;
        this.departmentName = departmentName;
        this.affiliation = affiliation;
    }

}
