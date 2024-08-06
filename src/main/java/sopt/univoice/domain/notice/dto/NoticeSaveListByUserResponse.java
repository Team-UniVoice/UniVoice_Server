package sopt.univoice.domain.notice.dto;

import java.time.LocalDateTime;

public record NoticeSaveListByUserResponse(
        Long id,
        String title,
        Long viewCount,
        Long noticeLike,
        String category,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime createdAt,
        String image
) {
}
