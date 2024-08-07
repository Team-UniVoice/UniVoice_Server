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


    @PostMapping("/university")
    public ResponseEntity<SuccessStatusResponse<List<String>>> getAllUniversityNames() {
        List<String> universityNames = universityDataService.getAllUniversityNames();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.UNIVERSITY_GET_SUCCESS, universityNames));
    }


    @PostMapping("/department")
    public ResponseEntity<SuccessStatusResponse<List<String>>> getDepartmentNamesByUniversity(@RequestBody UniversityNameRequest universityNameRequest) {
        List<String> departmentNames = universityDataService.getDepartmentNamesByUniversity(universityNameRequest.universityName());
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.DEPARTMENT_GET_SUCCESS, departmentNames));
    }





}
