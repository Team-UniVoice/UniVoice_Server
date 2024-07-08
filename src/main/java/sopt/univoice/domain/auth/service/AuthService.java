package sopt.univoice.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.auth.dto.MemberCreateRequest;
import sopt.univoice.domain.auth.repository.AuthRepository;
import sopt.univoice.domain.auth.repository.CollegeDepartmentRepository;
import sopt.univoice.domain.universityData.entity.CollegeDepartment;
import sopt.univoice.domain.universityData.entity.Department;
import sopt.univoice.domain.universityData.repository.DepartmentRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.external.S3Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthRepository authRepository;
    private final S3Service s3Service;
    private final DepartmentRepository departmentRepository;
    private final CollegeDepartmentRepository collegeDepartmentRepository;

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

        // departmentName을 통해 department 엔티티를 찾음
        List<Department> departments = departmentRepository.findByDepartmentName(memberCreateRequest.getDepartmentName());
        if (departments.isEmpty()) {
            throw new RuntimeException("해당 학과가 존재하지 않습니다.");
        }

        // 적절한 department 선택 (예: 첫 번째 결과 사용)
        Department department = departments.get(0);

        // department 엔티티를 통해 collegeDepartment 엔티티를 찾음
        CollegeDepartment collegeDepartment = collegeDepartmentRepository.findById(department.getCollegeDepartment().getId())
                .orElseThrow(() -> new RuntimeException("해당 단과대학이 존재하지 않습니다."));

        Member member = Member.builder()
                .admissionNumber(memberCreateRequest.getAdmissionNumber())
                .name(memberCreateRequest.getName())
                .studentNumber(memberCreateRequest.getStudentNumber())
                .email(memberCreateRequest.getEmail())
                .password(memberCreateRequest.getPassword())
                .studentCardImage(imageUrl)
                .universityName(memberCreateRequest.getUniversityName())
                .departmentName(memberCreateRequest.getDepartmentName())
                .collegeDepartmentName(collegeDepartment.getCollegeDepartmentName())
                .build();

        authRepository.save(member);
    }


}
