package sopt.univoice.domain.notice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.univoice.domain.auth.dto.MemberCreateRequest;
import sopt.univoice.domain.auth.service.AuthService;
import sopt.univoice.domain.notice.dto.GetAllNoticesResponseDTO;
import sopt.univoice.domain.notice.dto.NoticeCreateRequest;
import sopt.univoice.domain.notice.dto.NoticeSaveDTO;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.service.NoticeService;
import sopt.univoice.infra.common.dto.SuccessMessage;
import sopt.univoice.infra.common.dto.SuccessStatusResponse;

import java.util.List;

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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.LIKE_NOTICE_SUCCESS, null));
    }

    @PostMapping("/like/cancle/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Object>> likeCancleNotice(@PathVariable Long noticeId) {

        noticeService.likeCancleNotice(noticeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.LIKE_CANCLE_NOTICE_SUCCESS, null));
    }



    @PostMapping("/save/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Object>> saveNotice(@PathVariable Long noticeId) {

        noticeService.saveNotice(noticeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.SAVE_NOTICE_SUCCESS, null));
    }

    @PostMapping("/save/cancle/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Object>> saveCancleNotice(@PathVariable Long noticeId) {

        noticeService.saveCancleNotice(noticeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.SAVE_CANCLE_NOTICE_SUCCESS, null));
    }




    @GetMapping("/save/all")
    public ResponseEntity<SuccessStatusResponse<List<NoticeSaveDTO>>> getSaveNoticeByUser() {

        List<NoticeSaveDTO> notices = noticeService.getSaveNoticeByUser();
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.SAVE_ALL_NOTICE_SUCCESS, notices));
    }

    @PostMapping("/view-count/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Object>> viewCount(@PathVariable Long noticeId) {

        noticeService.viewCount(noticeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.VIEW_NOTICE_SUCCESS, null));
    }


    @GetMapping("/all")
    public ResponseEntity<SuccessStatusResponse<Object>> getAllNoticeByUserUniversity() {
        GetAllNoticesResponseDTO response = noticeService.getAllNoticeByUserUniversity();
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.GET_ALL_NOTICE_SUCCESS, response));
    }





}
