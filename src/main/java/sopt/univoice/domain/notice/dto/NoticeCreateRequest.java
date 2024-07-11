package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@NoArgsConstructor
//@AllArgsConstructor
public class NoticeCreateRequest {


    private String title;

    private String content;

    private Optional<Long> target = Optional.empty();
    private Optional<LocalDateTime> startTime = Optional.empty();
    private Optional<LocalDateTime> endTime = Optional.empty();

    private List<MultipartFile> studentCardImages;

}
