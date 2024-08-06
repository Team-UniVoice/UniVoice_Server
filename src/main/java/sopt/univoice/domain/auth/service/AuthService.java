package sopt.univoice.domain.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.affiliation.entity.Affiliation;
import sopt.univoice.domain.affiliation.repository.AffiliationRepository;
import sopt.univoice.domain.auth.UserAuthentication;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.auth.dto.MemberCreateRequest;
import sopt.univoice.domain.auth.dto.MemberSignInRequest;
import sopt.univoice.domain.auth.dto.UserLoginResponse;
import sopt.univoice.domain.auth.repository.AuthRepository;
import sopt.univoice.domain.auth.repository.CollegeDepartmentRepository;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.NoticeView;
import sopt.univoice.domain.notice.repository.NoticeRepository;
import sopt.univoice.domain.notice.repository.NoticeViewRepository;
import sopt.univoice.domain.universityData.entity.CollegeDepartment;
import sopt.univoice.domain.universityData.entity.Department;
import sopt.univoice.domain.universityData.repository.DepartmentRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.common.exception.UnauthorizedException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.common.jwt.JwtTokenProvider;
import sopt.univoice.infra.external.S3Service;



import java.io.IOException;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${slack.webhook-url}")
    private String webhookUrl;
    @Value("${aws-property.s3-bucket-url}")
    private String s3BucketUrl;

    private final AuthRepository authRepository;
    private final S3Service s3Service;
    private final DepartmentRepository departmentRepository;
    private final CollegeDepartmentRepository collegeDepartmentRepository;
    private final AffiliationRepository affiliationRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final NoticeRepository noticeRepository;
    private final NoticeViewRepository noticeViewRepository;



    @Transactional
    public void signUp(MemberCreateRequest memberCreateRequest) {
        String imageUrl = uploadStudentCardImage(memberCreateRequest.studentCardImage());
        Department department = validateAndGetDepartment(memberCreateRequest.departmentName());
        CollegeDepartment collegeDepartment = validateAndGetCollegeDepartment(department);
        Affiliation affiliation = createAffiliation();
        Member member = createMember(memberCreateRequest, imageUrl, department, collegeDepartment, affiliation);

        authRepository.save(member);
        sendSlackNotification(member);
        createAndSaveNoticeViews(member);
    }


    @Transactional
    public UserLoginResponse logineMember(MemberSignInRequest memberSignInRequest) {
        Member member = authRepository.findByEmail(memberSignInRequest.email())
                .orElseThrow(() -> new UnauthorizedException(ErrorMessage.JWT_LOGIN_PASSWORD_EXCEPTION));

        if (!passwordEncoder.matches(memberSignInRequest.password(), member.getPassword())) {
            throw new UnauthorizedException(ErrorMessage.JWT_LOGIN_PASSWORD_EXCEPTION);
        }

        Long memberId = member.getId();
        Collection<? extends GrantedAuthority> authorities = member.getAuthorities();
        String accessToken = jwtTokenProvider.issueAccessToken(
                UserAuthentication.createUserAuthentication(memberId, authorities)
        );

        return UserLoginResponse.of(accessToken);
    }


    public boolean isDuplicateEmail(CheckEmailRequest checkEmailRequest) {
        return authRepository.existsByEmail(checkEmailRequest.email());
    }

    private String uploadStudentCardImage(MultipartFile studentCardImage) {
        try {
            return s3Service.uploadImage("student-card-images/", studentCardImage);
        } catch (IOException e) {
            log.error("이미지 업로드에 실패했습니다: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 업로드에 실패했습니다!", e);
        }
    }

    private Department validateAndGetDepartment(String departmentName) {
        List<Department> departments = departmentRepository.findByDepartmentName(departmentName);
        if (departments.isEmpty()) {
            log.error("해당 학과가 존재하지 않습니다.");
            throw new RuntimeException("해당 학과가 존재하지 않습니다.");
        }
        return departments.get(0);
    }

    private CollegeDepartment validateAndGetCollegeDepartment(Department department) {
        return collegeDepartmentRepository.findById(department.getCollegeDepartment().getId())
                .orElseThrow(() -> {
                    log.error("해당 단과대학이 존재하지 않습니다.");
                    return new RuntimeException("해당 단과대학이 존재하지 않습니다.");
                });
    }

    private Affiliation createAffiliation() {
        Affiliation affiliation = Affiliation.createDefault();
        affiliationRepository.save(affiliation);
        return affiliation;
    }

    private Member createMember(MemberCreateRequest request, String imageUrl, Department department, CollegeDepartment collegeDepartment, Affiliation affiliation) {
        String hashedPassword = passwordEncoder.encode(request.password());

        return Member.builder()
                .admissionNumber(request.admissionNumber())
                .name(request.name())
                .studentNumber(request.studentNumber())
                .email(request.email())
                .password(hashedPassword)
                .studentCardImage(imageUrl)
                .universityName(request.universityName())
                .departmentName(department.getDepartmentName())
                .collegeDepartmentName(collegeDepartment.getCollegeDepartmentName())
                .affiliation(affiliation)
                .build();
    }


    private void sendSlackNotification(Member member) {
        Slack slack = Slack.getInstance();
        Map<String, String> messageData = new HashMap<>();
        messageData.put("text", String.format(
                "새로운 회원 가입:\n" +
                        "\"admissionNumber\" : %d\n" +
                        "\"name\" : \"%s\"\n" +
                        "\"studentNumber\" : \"%s\"\n" +
                        "\"email\" : \"%s\"\n" +
                        "\"password\" : \"%s\"\n" +
                        "\"studentCardImage\" : \"%s\"\n" +
                        "\"universityName\" : \"%s\"\n" +
                        "\"collegeDepartmentName\" : \"%s\"\n" +
                        "\"departmentName\" : \"%s\"",
                member.getAdmissionNumber(),
                member.getName(),
                member.getStudentNumber(),
                member.getEmail(),
                member.getPassword(),
                member.getStudentCardImage(),
                member.getUniversityName(),
                member.getCollegeDepartmentName(),
                member.getDepartmentName()
        ));

        try {
            String payload = objectMapper.writeValueAsString(messageData);
            WebhookResponse response = slack.send(webhookUrl, payload);
            log.info("Slack response: {}", response);
        } catch (IOException e) {
            log.error("Slack 메시지 전송에 실패했습니다: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void createAndSaveNoticeViews(Member member) {
        List<Notice> notices = noticeRepository.findAllByMemberUniversityName(member.getUniversityName());
        for (Notice notice : notices) {
            NoticeView noticeView = NoticeView.builder()
                    .notice(notice)
                    .member(member)
                    .readAt(false)
                    .build();
            noticeViewRepository.save(noticeView);
        }
    }




}

