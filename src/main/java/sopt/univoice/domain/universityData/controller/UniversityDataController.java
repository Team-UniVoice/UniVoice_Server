package sopt.univoice.domain.universityData.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.univoice.domain.universityData.dto.UniversityNameRequest;
import sopt.univoice.domain.universityData.service.UniversityDataService;
import sopt.univoice.infra.common.dto.SuccessMessage;
import sopt.univoice.infra.common.dto.SuccessStatusResponse;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/universityData")
@RequiredArgsConstructor
public class UniversityDataController {

    private final UniversityDataService universityDataService;
    private final ObjectMapper objectMapper;
    final String WEBHOOK_URL = "https://hooks.slack.com/services/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";


    @PostMapping("/university")
    public ResponseEntity<SuccessStatusResponse<List<String>>> getAllUniversityNames() {
        List<String> universityNames = universityDataService.getAllUniversityNames();

        Slack slack = Slack.getInstance();
        String payload = "{\"text\":\"슬랙 메시지 테스트입니다.\"}";
        try {
            WebhookResponse response = slack.send(WEBHOOK_URL, payload);
            System.out.println(response);
        } catch (IOException e) {
            log.error("slack 메시지 발송 중 문제가 발생했습니다.", e.toString());
            throw new RuntimeException(e);
        }


        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.UNIVERSITY_GET_SUCCESS, universityNames));
    }


    @PostMapping("/department")
    public ResponseEntity<SuccessStatusResponse<List<String>>> getDepartmentNamesByUniversity(@RequestBody UniversityNameRequest universityNameRequest) {
        List<String> departmentNames = universityDataService.getDepartmentNamesByUniversity(universityNameRequest.getUniversityName());
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.DEPARTMENT_GET_SUCCESS, departmentNames));
    }





}
