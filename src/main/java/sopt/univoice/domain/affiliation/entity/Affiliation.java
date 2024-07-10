package sopt.univoice.domain.affiliation.entity;



import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.persistence.BaseTimeEntity;

import java.util.List;


@Entity
@Getter
@NoArgsConstructor
public class Affiliation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String affiliationName;

    private String affiliation;

    private String affiliationCardImage;

    private String affiliationLogoImage;


    @OneToMany(mappedBy = "affiliation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members;


    // 새로운 생성자 추가
    @Builder
    public Affiliation(Role role) {
        this.role = role;
    }

    public static Affiliation createDefault() {
        return Affiliation.builder().role(Role.UNUSER).build();
    }

}
