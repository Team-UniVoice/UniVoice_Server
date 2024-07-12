package sopt.univoice.domain.notice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.user.entity.Member;

@Entity
@Getter
@NoArgsConstructor
public class NoticeView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;


    private boolean readAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;



    @Builder
    public NoticeView(Notice notice, Member member, boolean readAt) {
        this.notice = notice;
        this.member = member;
        this.readAt = readAt;
    }
    // Setter for readAt
    public void setReadAt(boolean readAt) {
        this.readAt = readAt;
    }

}
