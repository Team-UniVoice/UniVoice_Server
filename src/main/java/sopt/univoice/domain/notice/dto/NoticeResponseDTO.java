package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;
    private Long likeCount;
    private Long viewCount; // 조회수로 수정
    private String category;
    private LocalDateTime createdAt;
    private String image; // 이미지 추가
}
