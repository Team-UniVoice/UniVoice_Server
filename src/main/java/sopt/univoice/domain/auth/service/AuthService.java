package sopt.univoice.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.auth.dto.MemberCreateRequest;
import sopt.univoice.domain.auth.repository.AuthRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.external.S3Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthRepository authRepository;
    private final S3Service s3Service;

    public boolean isDuplicateEmail(CheckEmailRequest checkEmailRequest) {
        return authRepository.existsByEmail(checkEmailRequest.getEmail());
    }


    @Transactional
    public void signUp(MemberCreateRequest memberCreateRequest) {
        String imageUrl = null;
        try {
            imageUrl = s3Service.uploadImage("student-card-images/", memberCreateRequest.getStudentCardImage());
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }

        Member member = Member.builder()
                .admissionNumber(memberCreateRequest.getAdmissionNumber())
                .name(memberCreateRequest.getName())
                .studentNumber(memberCreateRequest.getStudentNumber())
                .email(memberCreateRequest.getEmail())
                .password(memberCreateRequest.getPassword())
                .studentCardImage(imageUrl)
                .universityName(memberCreateRequest.getUniversityName())
                .departmentName(memberCreateRequest.getDepartmentName())
                .build();

        authRepository.save(member);
    }


}
