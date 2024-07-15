package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeDetailResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Long noticeLike;
    private Long viewCount;
    private String target;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String category;
    private String contentSummary;
    private Long memberId;
    private String writeAffiliation;
    private List<String> noticeImages;
    private LocalDateTime createdAt; // 추가된 부분
    private Boolean likeCheck;
    private Boolean saveCheck;
    private String dayOfWeek;

}
