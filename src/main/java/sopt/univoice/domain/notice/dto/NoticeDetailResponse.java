package sopt.univoice.domain.notice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record NoticeDetailResponse(
        Long id,
        String title,
        String content,
        Long noticeLike,
        Long viewCount,
        String target,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String category,
        String contentSummary,
        Long memberId,
        String writeAffiliation,
        List<String> noticeImages,
        LocalDateTime createdAt, // 추가된 부분
        Boolean likeCheck,
        Boolean saveCheck,
        String dayOfWeek
) {
}
