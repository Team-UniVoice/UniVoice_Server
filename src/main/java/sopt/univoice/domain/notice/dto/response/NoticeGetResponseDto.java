package sopt.univoice.domain.notice.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sopt.univoice.domain.notice.entity.Notice;

public record NoticeGetResponseDto(

    Long noticeId,
    String writeAffiliation,
    String title,
    String target,
    LocalDateTime startTime,
    LocalDateTime endTime,
    List<String> imageList,
    String content,
    LocalDateTime createdAt,
    Integer viewCount
) {
    public static NoticeGetResponseDto of(Notice notice, String WriteAffiliation, List<String> imageList) {
        return new NoticeGetResponseDto(
            notice.getId(),
            WriteAffiliation,
            notice.getTitle(),
            notice.getTarget(),
            notice.getStartTime(),
            notice.getEndTime(),
            imageList,
            notice.getContent(),
            notice.getCreatedAt(),
            notice.getViewCount()
        );
    }
}
