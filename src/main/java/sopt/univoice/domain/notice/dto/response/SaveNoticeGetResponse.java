package sopt.univoice.domain.notice.dto.response;

import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.SaveNotice;
import sopt.univoice.domain.user.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public record SaveNoticeGetResponse(
    Long noticeId,
    LocalDateTime createdAt,
    String title,
    Integer noticeLike,
    Integer saveCount,
    String category,
    List<String> imageList
) {
    public static SaveNoticeGetResponse of(Notice notice, List<String> imageList) {
        return new SaveNoticeGetResponse(
            notice.getId(),
            notice.getCreatedAt(),
            notice.getTitle(),
            notice.getNoticeLike(),
            notice.getSaveCount(),
            notice.getCategory(),
            imageList
        );
    }
}

