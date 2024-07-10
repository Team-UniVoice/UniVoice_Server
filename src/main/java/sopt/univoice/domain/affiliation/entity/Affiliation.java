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

    private String affiliation;

    private String affiliationCardImage;

    private String affiliationLogoImage;


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
