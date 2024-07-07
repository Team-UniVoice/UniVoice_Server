package sopt.univoice.domain.universityData.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.univoice.domain.universityData.service.UniversityDataService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/universityData")
@RequiredArgsConstructor
public class UniversityDataController {

    private final UniversityDataService universityDataService;

    @GetMapping("/university")
    public List<String> getAllUniversityNames() {
        return universityDataService.getAllUniversityNames();
    }




}
