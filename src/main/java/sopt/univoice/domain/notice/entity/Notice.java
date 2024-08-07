package sopt.univoice.domain.notice.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.persistence.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Notice   extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String content;

    private Long noticeLike = 0L;

    private Long viewCount = 0L;
    private String target;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String category;

    @Column(length = 1000)
    private String contentSummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeImage> noticeImages = new ArrayList<>();


    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeView> noticeViews;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaveNotice> saveNotices;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeLike> noticeLikes;

    @Builder
    public Notice(String title, String content, String target, LocalDateTime startTime, LocalDateTime endTime, Member member, String contentSummary, String category) {
        this.title = title;
        this.content = content;
        this.target = target;
        this.startTime = startTime;
        this.endTime = endTime;
        this.member = member;
        this.noticeLike = 0L;
        this.viewCount = 0L;
        this.contentSummary = contentSummary;
        this.category = category;
    }


    public void setNoticeLike(Long noticeLike) {
        this.noticeLike = noticeLike;
    }
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void addNoticeImage(NoticeImage noticeImage) {
        noticeImages.add(noticeImage);
        noticeImage.setNotice(this);
    }



}
