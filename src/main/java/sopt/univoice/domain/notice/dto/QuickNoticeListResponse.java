package sopt.univoice.domain.notice.dto;

import java.time.LocalDateTime;

public record QuickNoticeListResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String title,
        String target,
        String writeAffiliation,
        String contentSummary,
        Long likeCount,
        Long viewCount,
        String category,
        LocalDateTime createdAt,
        String logoImage,
        Boolean saveCheck
) {
}
