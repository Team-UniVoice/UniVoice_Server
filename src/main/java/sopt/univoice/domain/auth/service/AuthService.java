package sopt.univoice.domain.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.univoice.domain.affiliation.entity.Affiliation;
import sopt.univoice.domain.affiliation.repository.AffiliationRepository;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.auth.dto.MemberCreateRequest;
import sopt.univoice.domain.auth.repository.AuthRepository;
import sopt.univoice.domain.auth.repository.CollegeDepartmentRepository;
import sopt.univoice.domain.universityData.entity.CollegeDepartment;
import sopt.univoice.domain.universityData.entity.Department;
import sopt.univoice.domain.universityData.repository.DepartmentRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.external.S3Service;

import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.composition.BlockCompositions;
import com.slack.api.model.block.element.BlockElements;

import java.io.IOException;
import java.util.Arrays;
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
    private final ObjectMapper objectMapper;

    public boolean isDuplicateEmail(CheckEmailRequest checkEmailRequest) {
        return authRepository.existsByEmail(checkEmailRequest.getEmail());
    }


    @Transactional
    public void signUp(MemberCreateRequest memberCreateRequest) {

        Slack slack = Slack.getInstance();
        String imageUrl = null;
        try {
            imageUrl = s3Service.uploadImage("student-card-images/", memberCreateRequest.getStudentCardImage());
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }

        List<Department> departments = departmentRepository.findByDepartmentName(memberCreateRequest.getDepartmentName());
        if (departments.isEmpty()) {
            throw new RuntimeException("해당 학과가 존재하지 않습니다.");
        }

        Department department = departments.get(0);

        CollegeDepartment collegeDepartment = collegeDepartmentRepository.findById(department.getCollegeDepartment().getId())
                .orElseThrow(() -> new RuntimeException("해당 단과대학이 존재하지 않습니다."));

        Affiliation affiliation = Affiliation.createDefault();
        affiliationRepository.save(affiliation);

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
                S3_BUCKET_URL + member.getStudentCardImage(),
                member.getUniversityName(),
                member.getCollegeDepartmentName(),
                member.getDepartmentName()
        );

        List<LayoutBlock> blocks = Arrays.asList(
                Blocks.section(section -> section.text(BlockCompositions.markdownText(formattedMessage))),
                Blocks.actions(actions -> actions.elements(asElements(
                        BlockElements.button(b -> b.text(plainText(pt -> pt.text("승인"))).value("approve").actionId("approve_action")),
                        BlockElements.button(b -> b.text(plainText(pt -> pt.text("거절"))).value("reject").actionId("reject_action"))
                )))
        );

        try {
            Payload payload = Payload.builder()
                    .blocks(blocks)
                    .build();
            WebhookResponse response = slack.send(WEBHOOK_URL, payload);
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
