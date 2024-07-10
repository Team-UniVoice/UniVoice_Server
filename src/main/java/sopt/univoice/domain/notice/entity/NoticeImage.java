package sopt.univoice.domain.notice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice_image", schema = "public")
public class NoticeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_image_id")
    private Long id;

    @Column(name = "notice_image", length = 255)
    private String noticeImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_idx")
    private Notice notice;

    @Builder
    public NoticeImage(String noticeImage, Notice notice) {
        this.noticeImage = noticeImage;
        this.notice = notice;
    }

    public static NoticeImage of(String noticeImage, Notice notice) {
        return new NoticeImage(noticeImage, notice);
    }
}
