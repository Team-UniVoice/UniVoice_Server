package sopt.univoice.domain.auth.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.auth.dto.MemberCreateRequest;
import sopt.univoice.domain.auth.dto.MemberSignInRequest;
import sopt.univoice.domain.auth.dto.UserLoginResponse;
import sopt.univoice.domain.auth.service.AuthService;
import sopt.univoice.domain.universityData.dto.UniversityNameRequest;
import sopt.univoice.infra.common.dto.SuccessMessage;
import sopt.univoice.infra.common.dto.SuccessStatusResponse;
import sopt.univoice.infra.common.exception.message.BusinessException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/check-email")
    public ResponseEntity<SuccessStatusResponse<Void>> checkEmail(@RequestBody CheckEmailRequest checkEmailRequest) {
        boolean isDuplicate = authService.isDuplicateEmail(checkEmailRequest);
        if (isDuplicate) {
            throw new BusinessException(ErrorMessage.EMAIL_DUPLICATE);
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(SuccessStatusResponse.of(SuccessMessage.EMAIL_AVAILABLE));
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<UserLoginResponse> signIn(@RequestBody MemberSignInRequest memberSignInRequest) {

        UserLoginResponse userLoginResponse = authService.logineMember(memberSignInRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", userLoginResponse.userId())
                .body(
                        userLoginResponse
                );
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessStatusResponse<Void>> signUp(@Valid @ModelAttribute MemberCreateRequest memberCreateRequest) {
        System.out.println(memberCreateRequest.toString());


        authService.signUp(memberCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessStatusResponse.of(SuccessMessage.SIGNUP_SUCCESS, null));
    }




    @PostMapping("/slack/actions")
    public void handleSlackActions(@RequestBody Map<String, Object> payload) {
        List<Map<String, Object>> actions = (List<Map<String, Object>>) payload.get("actions");
        String actionId = (String) actions.get(0).get("action_id");

        if ("approve_action".equals(actionId)) {
            // 승인 처리 로직
            System.out.println("승인 버튼 클릭됨");
        } else if ("reject_action".equals(actionId)) {
            // 거절 처리 로직
            System.out.println("거절 버튼 클릭됨");
        }
    }











}
