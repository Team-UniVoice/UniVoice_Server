package sopt.univoice.domain.universityData.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.univoice.domain.universityData.service.UniversityDataService;
import sopt.univoice.infra.common.dto.SuccessMessage;
import sopt.univoice.infra.common.dto.SuccessStatusResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/universityData")
@RequiredArgsConstructor
public class UniversityDataController {

    private final UniversityDataService universityDataService;

    @GetMapping("/university")
    public ResponseEntity<SuccessStatusResponse<List<String>>> getAllUniversityNames() {
        List<String> universityNames = universityDataService.getAllUniversityNames();
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessStatusResponse.of(SuccessMessage.UNIVERSITY_GET_SUCCESS, universityNames));
    }


}
