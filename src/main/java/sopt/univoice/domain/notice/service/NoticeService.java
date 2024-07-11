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
import sopt.univoice.domain.notice.entity.NoticeLike;
import sopt.univoice.domain.notice.repository.NoticeImageRepository;
import sopt.univoice.domain.notice.repository.NoticeLikeRepository;
import sopt.univoice.domain.notice.repository.NoticeRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.domain.affiliation.entity.Role;
import sopt.univoice.infra.common.exception.UnauthorizedException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.external.OpenAiService;
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
    private final OpenAiService openAiService;
    private final NoticeLikeRepository noticeLikeRepository;

    @Transactional
    public void createPost(NoticeCreateRequest noticeCreateRequest) {
        Long memberId = principalHandler.getUserIdFromPrincipal();
        System.out.println("Authenticated Member ID: " + memberId);
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        System.out.println("Member Role: " + member.getAffiliation().getRole());

        String summarizedContent = null;
        try {
            summarizedContent = openAiService.summarizeText(noticeCreateRequest.getContent());
            System.out.println("Summarized Content: " + summarizedContent);
        } catch (IOException e) {
            System.err.println("Error summarizing content: " + e.getMessage());
            e.printStackTrace();
        }

        // 사용자 권한 확인
        if (member.getAffiliation().getRole() != Role.APPROVEADMIN) {
            System.out.println("User does not have APPROVEADMIN role");
            throw new UnauthorizedException(ErrorMessage.JWT_UNAUTHORIZED_EXCEPTION);
        }

        // Notice 엔티티 생성 및 저장
        Notice notice = Notice.builder()
                .title(noticeCreateRequest.getTitle())
                .content(noticeCreateRequest.getContent())
                .target(noticeCreateRequest.getTarget())
                .startTime(noticeCreateRequest.getStartTime())
                .endTime(noticeCreateRequest.getEndTime())
                .member(member)
                .contentSummary(summarizedContent)
                .build();
        noticeRepository.save(notice);
        System.out.println("Notice saved successfully with ID: " + notice.getId());

        // NoticeImage 엔티티 생성 및 저장
        for (MultipartFile file : noticeCreateRequest.getStudentCardImages()) {
            String fileName = storeFile(file); // 파일 저장 로직 필요
            NoticeImage noticeImage = NoticeImage.builder()
                    .notice(notice)
                    .noticeImage(fileName)
                    .build();
            noticeImageRepository.save(noticeImage);
            System.out.println("NoticeImage saved successfully with file name: " + fileName);
        }
    }

    private String storeFile(MultipartFile file) {
        try {
            return s3Service.uploadImage("notice-images/", file);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }


    @Transactional
    public void likeNotice(Long noticeId) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        // member와 notice를 가져옵니다.
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

        // noticeLike를 1 증가시킵니다.
        notice.setNoticeLike(notice.getNoticeLike() + 1);
        noticeRepository.save(notice);

        // NoticeLike 객체를 생성하고 저장합니다.
        NoticeLike noticeLike = NoticeLike.builder()
                .notice(notice)
                .member(member)
                .build();
        noticeLikeRepository.save(noticeLike);
    }


    @Transactional
    public void likeCancleNotice(Long noticeId) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        // member와 notice를 가져옵니다.
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

        // noticeLike를 1 감소시킵니다.
        notice.setNoticeLike(notice.getNoticeLike() - 1);
        noticeRepository.save(notice);

        // NoticeLike 테이블에서 해당 데이터를 삭제합니다.
        NoticeLike noticeLike = noticeLikeRepository.findByNoticeAndMember(notice, member)
                .orElseThrow(() -> new RuntimeException("좋아요 정보가 존재하지 않습니다."));
        noticeLikeRepository.delete(noticeLike);
    }






}
