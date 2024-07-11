package sopt.univoice.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.auth.PrincipalHandler;
import sopt.univoice.domain.auth.repository.AuthRepository;
import sopt.univoice.domain.notice.dto.NoticeCreateRequest;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.NoticeImage;
import sopt.univoice.domain.notice.repository.NoticeImageRepository;
import sopt.univoice.domain.notice.repository.NoticeRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.domain.affiliation.entity.Role;
import sopt.univoice.infra.common.exception.UnauthorizedException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.external.S3Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final AuthRepository authRepository;
    private final PrincipalHandler principalHandler;
    private final S3Service s3Service;

    @Transactional
    public void createPost(NoticeCreateRequest noticeCreateRequest) {
        Long memberId = principalHandler.getUserIdFromPrincipal();
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 사용자 권한 확인
        if (member.getAffiliation().getRole() != Role.APPROVEADMIN) {
            throw new UnauthorizedException(ErrorMessage.JWT_UNAUTHORIZED_EXCEPTION);
        }

        // Notice 엔티티 생성 및 저장
        Notice notice = Notice.builder()
                .title(noticeCreateRequest.getTitle())
                .content(noticeCreateRequest.getContent())
                .target(noticeCreateRequest.getTarget().orElse(null))
                .startTime(noticeCreateRequest.getStartTime().orElse(null))
                .endTime(noticeCreateRequest.getEndTime().orElse(null))
                .member(member)
                .build();
        noticeRepository.save(notice);

        // NoticeImage 엔티티 생성 및 저장
        for (MultipartFile file : noticeCreateRequest.getStudentCardImages()) {
            String fileName = storeFile(file); // 파일 저장 로직 필요
            NoticeImage noticeImage = NoticeImage.builder()
                    .notice(notice)
                    .noticeImage(fileName)
                    .build();
            noticeImageRepository.save(noticeImage);
        }
    }

    private String storeFile(MultipartFile file) {
        try {
            return s3Service.uploadImage("notice-images/", file);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }
}
