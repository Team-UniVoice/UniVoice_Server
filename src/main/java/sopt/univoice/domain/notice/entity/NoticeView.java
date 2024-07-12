package sopt.univoice.domain.notice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.persistence.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notice_view", schema = "public")
public class NoticeView extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_view_id")
    private Long id;

    private Boolean readAt = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setReadAt(Boolean readAt) {
        this.readAt = readAt;
    }

    @Builder
    public NoticeView(Notice notice, Member member, Boolean readAt) {
        this.notice = notice;
        this.member = member;
        this.readAt = readAt;
    }
}

