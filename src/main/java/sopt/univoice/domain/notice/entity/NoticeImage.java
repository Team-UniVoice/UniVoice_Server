package sopt.univoice.domain.notice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class NoticeImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String  noticeImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Builder
    public NoticeImage(String noticeImage, Notice notice) {
        this.noticeImage = noticeImage;
        this.notice = notice;
    }
    public void setNotice(Notice notice) {
        this.notice = notice;
    }
}
