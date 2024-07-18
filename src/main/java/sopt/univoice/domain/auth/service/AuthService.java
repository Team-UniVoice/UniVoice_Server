package sopt.univoice.domain.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.composition.BlockCompositions;
import com.slack.api.model.block.element.BlockElements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;

@Service
@RequiredArgsConstructor
public class AuthService {

    final String WEBHOOK_URL = "https://hooks.slack.com/services/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";
    private static final String S3_BUCKET_URL = "https://uni-voice-bucket.s3.ap-northeast-2.amazonaws.com/";


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

    public boolean isDuplicateEmail(CheckEmailRequest checkEmailRequest) {
        return authRepository.existsByEmail(checkEmailRequest.getEmail());
    }



    private LayoutBlock getHeader(String text) {
        return Blocks.header(h -> h.text(
                BlockCompositions.plainText(pt -> pt.emoji(true).text(text))));
    }

    private LayoutBlock getSection(String message) {
        return Blocks.section(s -> s.text(
                BlockCompositions.markdownText(message)));
    }

    private BlockElement getActionButton(String plainText, String value, String style, String actionId) {
        return BlockElements.button(b -> b.text(plainText(pt -> pt.text(plainText).emoji(true)))
                .value(value)
                .style(style)
                .actionId(actionId));
    }

    private List<BlockElement> getActionBlocks() {
        List<BlockElement> actions = new ArrayList<>();
        actions.add(getActionButton("승인", "approve", "primary", "approve_action"));
        actions.add(getActionButton("거절", "reject", "danger", "reject_action"));
        return actions;
    }

    private List<LayoutBlock> createSlackMessageBlocks(String formattedMessage) {
        return Arrays.asList(
                getHeader("새로운 회원 가입 알림"),
                Blocks.divider(),
                getSection(formattedMessage),
                Blocks.divider(),
                Blocks.actions(actions -> actions.elements(getActionBlocks()))
        );
    }

    // 회원 가입 메서드 수정
    @Transactional
    public void signUp(MemberCreateRequest memberCreateRequest) {
        Slack slack = Slack.getInstance();
        String imageUrl = null;

        try {
            imageUrl = s3Service.uploadImage("student-card-images/", memberCreateRequest.getStudentCardImage());
        } catch (IOException e) {
            System.err.println("이미지 업로드에 실패했습니다: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }

        List<Department> departments = departmentRepository.findByDepartmentName(memberCreateRequest.getDepartmentName());
        if (departments.isEmpty()) {
            System.err.println("해당 학과가 존재하지 않습니다.");
            throw new RuntimeException("해당 학과가 존재하지 않습니다.");
        }

        Department department = departments.get(0);

        CollegeDepartment collegeDepartment = collegeDepartmentRepository.findById(department.getCollegeDepartment().getId())
                .orElseThrow(() -> {
                    System.err.println("해당 단과대학이 존재하지 않습니다.");
                    return new RuntimeException("해당 단과대학이 존재하지 않습니다.");
                });

        Affiliation affiliation = Affiliation.createDefault();
        affiliationRepository.save(affiliation);

        // 비밀번호 해시
        String hashedPassword = passwordEncoder.encode(memberCreateRequest.getPassword());

        Member member = Member.builder()
                .admissionNumber(memberCreateRequest.getAdmissionNumber())
                .name(memberCreateRequest.getName())
                .studentNumber(memberCreateRequest.getStudentNumber())
                .email(memberCreateRequest.getEmail())
                .password(hashedPassword)
                .studentCardImage(imageUrl)
                .universityName(memberCreateRequest.getUniversityName())
                .departmentName(memberCreateRequest.getDepartmentName())
                .collegeDepartmentName(collegeDepartment.getCollegeDepartmentName())
                .affiliation(affiliation)
                .build();

        authRepository.save(member);

        String formattedMessage = String.format(
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
        );

        List<LayoutBlock> blocks = createSlackMessageBlocks(formattedMessage);

        try {
            Payload payload = Payload.builder()
                    .blocks(blocks)
                    .build();
            WebhookResponse response = slack.send(WEBHOOK_URL, payload);
            System.out.println("Slack response: " + response);
        } catch (IOException e) {
            System.err.println("Slack 메시지 전송에 실패했습니다: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // NoticeView 엔티티 생성 및 저장
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

    @Transactional
    public UserLoginResponse logineMember(
            MemberSignInRequest memberSignInRequest
    ) {
        Member member = authRepository.findByEmail(memberSignInRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException(ErrorMessage.JWT_LOGIN_PASSWORD_EXCEPTION));

        if (!passwordEncoder.matches(memberSignInRequest.getPassword(), member.getPassword())) {
            throw new UnauthorizedException(ErrorMessage.JWT_LOGIN_PASSWORD_EXCEPTION);
        }

        Long memberId = member.getId();
        Collection<? extends GrantedAuthority> authorities = member.getAuthorities(); // 역할 정보 가져오기
        String accessToken = jwtTokenProvider.issueAccessToken(
                UserAuthentication.createUserAuthentication(memberId, authorities)
        );

        return UserLoginResponse.of(accessToken);
    }

}
