package sopt.univoice.domain.notice.dto.request;

import java.time.LocalDateTime;


public record NoticeRegisterRequestDto(
    String title,
    String target,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String content
) {
}

