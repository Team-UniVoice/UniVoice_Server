package sopt.univoice.domain.auth.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.auth.service.AuthService;
import sopt.univoice.domain.universityData.dto.UniversityNameRequest;
import sopt.univoice.infra.common.dto.SuccessMessage;
import sopt.univoice.infra.common.dto.SuccessStatusResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/check-email")
    public ResponseEntity<SuccessStatusResponse<Void>> checkEmail(@RequestBody CheckEmailRequest checkEmailRequest) {
        boolean isDuplicate = authService.isDuplicateEmail(checkEmailRequest);
        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(SuccessStatusResponse.of(SuccessMessage.EMAIL_DUPLICATE));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(SuccessStatusResponse.of(SuccessMessage.EMAIL_AVAILABLE));
        }
    }










}
