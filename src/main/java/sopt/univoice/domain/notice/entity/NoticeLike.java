package sopt.univoice.domain.notice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.user.entity.Member;

@Entity
@Getter
@NoArgsConstructor
public class NoticeLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public NoticeLike(Notice notice, Member member) {
        this.notice = notice;
        this.member = member;
    }



}
