package sopt.univoice.domain.notice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.notice.dto.request.NoticeRegisterRequestDto;
import sopt.univoice.domain.notice.dto.response.NoticeGetResponseDto;
import sopt.univoice.domain.notice.dto.response.NoticeRegisterResponseDto;
import sopt.univoice.domain.notice.service.NoticeService;
import sopt.univoice.infra.common.dto.SuccessMessage;
import sopt.univoice.infra.common.dto.SuccessStatusResponse;

import java.util.List;

@Controller
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private static final String ACCESS_TOKEN = "access-token";

    @PostMapping
    public ResponseEntity<SuccessStatusResponse<NoticeRegisterResponseDto>> registerNotice(@ModelAttribute NoticeRegisterRequestDto noticeRegisterRequestDto, @RequestPart(value = "file") List<MultipartFile> files, @RequestHeader Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessStatusResponse.of(SuccessMessage.POST_NOTICE_SUCCESS, noticeService.registerNotice(noticeRegisterRequestDto, files, memberId)));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<NoticeGetResponseDto>> getNotice(@PathVariable Long noticeId, @RequestHeader Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessStatusResponse.of(SuccessMessage.GET_NOICE_SUCCESS, noticeService.getNotice(noticeId, memberId)));
    }

    @PostMapping("/like/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Void>> postLike(@PathVariable Long noticeId, @RequestHeader Long memberId) {
        noticeService.postLike(noticeId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessStatusResponse.of(SuccessMessage.POST_LIKE_SUCCESS));
    }

    @DeleteMapping("/like/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Void>> deleteLike(@PathVariable Long noticeId, @RequestHeader Long memberId) {
        noticeService.deleteLike(noticeId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessStatusResponse.of(SuccessMessage.DELETE_LIKE_SUCCESS));
    }
}
