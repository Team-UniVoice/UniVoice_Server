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

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<SuccessStatusResponse<NoticeRegisterResponseDto>> registerNotice(@RequestHeader("Authorization") String accessToken, @ModelAttribute NoticeRegisterRequestDto noticeRegisterRequestDto, @RequestPart(value = "file") List<MultipartFile> files) {
        if (!ACCESS_TOKEN.equals(accessToken)) { // 아직 accessToken이 없어서 임시로 검증하는 부분
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessStatusResponse.of(SuccessMessage.POST_NOTICE_SUCCESS, noticeService.registerNotice(noticeRegisterRequestDto, files)));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<NoticeGetResponseDto>> getNotice(@RequestHeader("Authorization") String accessToken, @PathVariable Long noticeId) {
        if (!ACCESS_TOKEN.equals(accessToken)) { // 아직 accessToken이 없어서 임시로 검증하는 부분
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessStatusResponse.of(SuccessMessage.GET_NOICE_SUCCESS, noticeService.getNotice(noticeId)));
    }
}
