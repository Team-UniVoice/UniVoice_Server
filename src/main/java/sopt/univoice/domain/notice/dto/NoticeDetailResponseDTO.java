package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeDetailResponseDTO {
    private Long id;
    private String writeAffiliation;
    private String title;
    private String target;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
    private LocalDateTime createdAt;
    private Long viewCount;
    private List<String> noticeImages;
}
