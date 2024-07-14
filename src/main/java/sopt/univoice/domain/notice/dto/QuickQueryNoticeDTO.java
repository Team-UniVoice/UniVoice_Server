package sopt.univoice.domain.notice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QuickQueryNoticeDTO {
    private Long id;
    private String writeAffiliation;
    private LocalDateTime createdAt;
    private Long viewCount;
    private String title;
    private String target;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Size(max = 150)
    private String contentSummary;
}

