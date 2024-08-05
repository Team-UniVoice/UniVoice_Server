package sopt.univoice.domain.auth.dto;

import org.springframework.web.multipart.MultipartFile;

public record MemberCreateRequest(
        Long admissionNumber,
        String name,
        String studentNumber,
        String email,
        String password,
        MultipartFile studentCardImage,
        String universityName,
        String departmentName
) {}
