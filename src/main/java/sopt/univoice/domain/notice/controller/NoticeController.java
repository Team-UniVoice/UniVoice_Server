package sopt.univoice.domain.notice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.univoice.domain.notice.dto.*;
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

    @PostMapping("/like/cancel/{noticeId}")
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

    @PostMapping("/save/cancel/{noticeId}")
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
                .body(SuccessStatusResponse.of(SuccessMessage.VIEW_COUNT_NOTICE_SUCCESS, null));
    }

    @PostMapping("/view-check/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<Object>> viewCheck(@PathVariable Long noticeId) {

        noticeService.viewCheck(noticeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.VIEW_CHECK_NOTICE_SUCCESS, null));
    }

    @GetMapping("/quickhead")
    public ResponseEntity<SuccessStatusResponse<Object>> quickhead() {
        QuickScanDTO response = noticeService.quickhead();
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.GET_QUCIK_HEAD_SUCCESS, response));
    }

    @GetMapping("/all")
    public ResponseEntity<SuccessStatusResponse<Object>> getAllNoticeByUserUniversity() {
        List<NoticeDTO> response = noticeService.getAllNoticeByUserUniversity();
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.GET_ALL_NOTICE_SUCCESS, response));
    }



    @GetMapping("/university")
    public ResponseEntity<SuccessStatusResponse<Object>> getUniversityNoticeByUserUniversity() {
        List<NoticeDTO> response = noticeService.getUniversityNoticeByUserUniversity();
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.GET_ALL_UNIVERSITY_NOTICE_SUCCESS, response));
    }



    @GetMapping("/college-department")
    public ResponseEntity<SuccessStatusResponse<Object>> getCollegeDepartmentNoticeByUserUniversity() {
        List<NoticeResponseDTO> response = noticeService.getCollegeDepartmentNoticeByUserUniversity();


        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.GET_ALL_COLLEGE_NOTICE_SUCCESS, response));
    }

    @GetMapping("/department")
    public ResponseEntity<SuccessStatusResponse<Object>> getDepartmentNoticeByUserUniversity() {
        List<NoticeResponseDTO> response = noticeService.getDepartmentNoticeByUserUniversity();


        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.GET_ALL_DEPARTMENT_NOTICE_SUCCESS, response));
    }

    @PostMapping("/quick")
    public ResponseEntity<SuccessStatusResponse<List<QuickQueryNoticeDTO>>> getQuickNoticeByUserUniversity(@RequestBody AffiliationRequestDTO request) {

        List<QuickQueryNoticeDTO> response = noticeService.getQuickNoticeByUserUniversity(request.getAffiliation());
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.GET_QUICK_NOTICE_SUCCESS, response));
    }


    @GetMapping("/{noticeId}")
    public ResponseEntity<SuccessStatusResponse<NoticeDetailResponseDTO>> getNoticeById(@PathVariable Long noticeId) {
        NoticeDetailResponseDTO response = noticeService.getNoticeById(noticeId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.GET_Detail_NOTICE_SUCCESS, response));
    }




}
