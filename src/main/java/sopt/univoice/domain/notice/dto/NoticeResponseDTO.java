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
    private LocalDateTime createdAt;
    private String title;
    private Long likeCount;
    private Long viewCount;
    private String category;
    private String image;
}
