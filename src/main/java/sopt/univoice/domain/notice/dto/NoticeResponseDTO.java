package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public record NoticeResponseDTO(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String title,
        Long likeCount,
        Long viewCount,
        String category,
        LocalDateTime createdAt,
        String image
) {
}
