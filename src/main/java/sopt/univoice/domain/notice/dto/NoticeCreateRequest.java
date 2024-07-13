package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
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

    private String target;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    private List<MultipartFile> imageList;
}
