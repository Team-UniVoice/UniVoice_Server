package sopt.univoice.domain.notice.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import sopt.univoice.domain.affiliation.entity.Affiliation;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.NoticeImage;

public record NoticeGetResponseDto(

    Long noticeIdx,
    String title,
    String target,
    LocalDateTime startTime,
    LocalDateTime endTime,
    List<String> imageList,
    String content,
    LocalDateTime createdAt,
    Integer viewCount
) {
    public static NoticeGetResponseDto of(Notice notice, List<String> imageList) {
        return new NoticeGetResponseDto(
            notice.getNoticeIdx(),
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
