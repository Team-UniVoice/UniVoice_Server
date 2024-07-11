package sopt.univoice.domain.notice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/create")
    public ResponseEntity<SuccessStatusResponse<Void>> createPost(@ModelAttribute NoticeCreateRequest noticeCreateRequest) {
        System.out.println("createPost method called with request: " + noticeCreateRequest);
        noticeService.createPost(noticeCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.CREATE_NOTICE_SUCCESS, null));
    }


    @PostMapping("/like/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Object>> likeNotice(@PathVariable Long noticeId) {

        noticeService.likeNotice(noticeId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.LIKE_NOTICE_SUCCESS, null));
    }

    @PostMapping("/like/cancle/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Object>> likeCancleNotice(@PathVariable Long noticeId) {

        noticeService.likeCancleNotice(noticeId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.LIKE_CANCLE_NOTICE_SUCCESS, null));
    }


}
