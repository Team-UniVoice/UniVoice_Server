package sopt.univoice.domain.affiliation.entity;



import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.persistence.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(schema = "public")
public class Affiliation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "affiliation_id")
    private Long id;

    private String role;

    private String affiliationName;

    private String affiliation; // 0.NULl 미승인 / 1. 일반/ 2. 학생회 / 3. 단과대학 / 4. 총학생회

    private String affiliationCardImage;

    private String affiliationLogoImage;

    @OneToMany(mappedBy = "affiliation")
    private List<Member> members = new ArrayList<>();


    @Builder
    public Affiliation(String role, String affiliationName, String affiliation, String affiliationCardImage, String affiliationLogoImage) {
        this.role = role;
        this.affiliationName = affiliationName;
        this.affiliation = affiliation;
        this.affiliationCardImage = affiliationCardImage;
        this.affiliationLogoImage = affiliationLogoImage;
    }

    public static Affiliation of(String role, String affiliationName, String affiliation, String affiliationCardImage, String affiliationLogoImage) {
        return new Affiliation(role, affiliationName, affiliation, affiliationCardImage, affiliationLogoImage);
    }
}
