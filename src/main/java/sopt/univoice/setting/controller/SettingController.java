package sopt.univoice.setting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sopt.univoice.setting.service.SettingService;
import sopt.univoice.setting.service.dto.SettingCreateDto;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class SettingController {
    private final SettingService settingService;

    @GetMapping("/test")
    public String test() {
        return "테스트 API 입니다! + CD 테스트 !! ";
    }

    @PostMapping
    public ResponseEntity createMember(
            @RequestBody SettingCreateDto memberCreate
    ) {
        return ResponseEntity.created(URI.create(settingService.createMember(memberCreate))).build();
    }
}



