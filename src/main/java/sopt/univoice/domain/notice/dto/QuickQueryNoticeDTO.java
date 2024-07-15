package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QuickQueryNoticeDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;
    private String target;  // 추가
    private String writeAffiliation; // 추가
    private String contentSummary; // 추가
    private Long likeCount;
    private Long viewCount;
    private String category;
    private LocalDateTime createdAt; // 추가된 부분
}

