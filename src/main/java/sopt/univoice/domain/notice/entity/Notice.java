package sopt.univoice.domain.notice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.user.entity.User;
import sopt.univoice.infra.persistence.BaseTimeEntity;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice", schema = "public")
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_idx")
    private Long noticeIdx;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "notice_like", nullable = false)
    private Integer noticeLike;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Column(name = "target", nullable = false, length = 30)
    private String target;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "category", nullable = false)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeImage> noticeImages = new ArrayList<>();

    public void addNoticeImage(NoticeImage noticeImage) {
        noticeImages.add(noticeImage);
    }

    @Builder
    public Notice(String title, String content, Integer noticeLike, Integer viewCount, String target, LocalDateTime startTime, LocalDateTime endTime, String category, User user, List<NoticeImage> noticeImages) {
        this.title = title;
        this.content = content;
        this.noticeLike = noticeLike;
        this.viewCount = viewCount;
        this.target = target;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.user = user;
        this.noticeImages = noticeImages;
    }

    public static Notice of(String title, String content, Integer noticeLike, Integer viewCount, String target, LocalDateTime startTime, LocalDateTime endTime, String category, User user, List<NoticeImage> noticeImages) {
        return new Notice(title, content, noticeLike, viewCount, target, startTime, endTime, category, user, noticeImages);
    }
}
