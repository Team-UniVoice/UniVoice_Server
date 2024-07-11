package sopt.univoice.domain.notice.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.firstcome.entity.FirstCome;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.persistence.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Notice   extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String title;

    private String content;

    private Long noticeLike = 0L;

    private Long viewCount = 0L;
    private String target;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long category;
    private Long contentSummary;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private FirstCome firstCome;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeImage> noticeImages;


    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeView> noticeViews;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaveNotice> saveNotices;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeLike> noticeLikes;

    @Builder
    public Notice(String title, String content, String target, LocalDateTime startTime, LocalDateTime endTime, Member member) {
        this.title = title;
        this.content = content;
        this.target = target;
        this.startTime = startTime;
        this.endTime = endTime;
        this.member = member;
        this.noticeLike = 0L;
        this.viewCount = 0L;
    }




}
