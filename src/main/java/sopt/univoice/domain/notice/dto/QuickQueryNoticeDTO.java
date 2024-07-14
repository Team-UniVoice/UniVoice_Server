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
    private Long likeCount;
    private Long saveCount;
    private String category;
    private LocalDateTime createdAt; // 추가된 부분
    private LocalDateTime updatedAt; // 추가된 부분
}

