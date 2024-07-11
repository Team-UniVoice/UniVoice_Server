package sopt.univoice.domain.notice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.univoice.domain.auth.dto.MemberCreateRequest;
import sopt.univoice.domain.auth.service.AuthService;
import sopt.univoice.domain.notice.dto.NoticeCreateRequest;
import sopt.univoice.domain.notice.service.NoticeService;
import sopt.univoice.infra.common.dto.SuccessMessage;
import sopt.univoice.infra.common.dto.SuccessStatusResponse;

@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping()
    public ResponseEntity<SuccessStatusResponse<Void>> createPost(@ModelAttribute NoticeCreateRequest noticeCreateRequest) {

        noticeService.createPost(noticeCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.CREATE_NOTICE_SUCCESS, null));
    }


}
