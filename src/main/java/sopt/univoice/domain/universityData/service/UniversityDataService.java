package sopt.univoice.domain.universityData.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.univoice.domain.universityData.entity.University;
import sopt.univoice.domain.universityData.repository.UniversityDataRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversityDataService {

    private final UniversityDataRepository universityDataRepository;

    public List<String> getAllUniversityNames() {
        List<University> universities = universityDataRepository.findAll();
        return universities.stream()
                .map(University::getUniversityName)
                .collect(Collectors.toList());
    }
}
