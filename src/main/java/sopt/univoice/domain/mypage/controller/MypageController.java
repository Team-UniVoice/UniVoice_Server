package sopt.univoice.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.univoice.domain.mypage.dto.response.GetMypageReponseDto;
import sopt.univoice.domain.mypage.service.MypageService;
import sopt.univoice.domain.notice.dto.NoticeSaveDTO;
import sopt.univoice.infra.common.dto.SuccessMessage;
import sopt.univoice.infra.common.dto.SuccessStatusResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @GetMapping
    public ResponseEntity<SuccessStatusResponse<GetMypageReponseDto>> getMypage() {
        return ResponseEntity.status(HttpStatus.OK)
                   .body(SuccessStatusResponse.of(SuccessMessage.GET_MYPAGE_SUCCESS, mypageService.getMypage()));
    }
}


