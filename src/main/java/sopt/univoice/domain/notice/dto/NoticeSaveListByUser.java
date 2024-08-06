package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public record NoticeSaveDTO(
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
