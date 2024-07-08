package sopt.univoice.domain.notice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public record NoticeRegisterRequestDto(
    String title,
    String target,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String content,
    List<String> noticeImage
) {
}
