package sopt.univoice.domain.user.entity;

import jakarta.persistence.*;
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




}
