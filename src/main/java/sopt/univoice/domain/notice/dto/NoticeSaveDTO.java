package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoticeSaveDTO {
    private Long id;
    private String title;
    private Long viewCount;
    private Long noticeLike;
    private String category;
    private LocalDateTime createdAt;
    private String image;
}
