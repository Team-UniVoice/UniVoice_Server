package sopt.univoice.domain.affiliation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.notice.entity.NoticeImage;
import sopt.univoice.domain.user.entity.User;
import sopt.univoice.infra.persistence.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "affiliation", schema = "public")
public class Affiliation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "affiliation_idx")
    private Long affiliationIdx;

    @Column(name = "role")
    private String role;

    @Column(name = "affiliation_name")
    private String affiliationName;

    @Column(name = "affiliation")
    private String affiliation;

    @Column(name = "affiliation_card_image")
    private String affiliationCardImage;

    @Column(name = "affiliation_logo_image")
    private String affiliationLogoImage;

    @OneToMany(mappedBy = "affiliation")
    private List<User> users = new ArrayList<>();

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
