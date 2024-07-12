package sopt.univoice.domain.notice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.affiliation.entity.Membership;
import sopt.univoice.domain.affiliation.entity.Role;
import sopt.univoice.domain.notice.dto.request.NoticeRegisterRequestDto;
import sopt.univoice.domain.notice.dto.response.NoticeGetResponseDto;
import sopt.univoice.domain.notice.dto.response.NoticeRegisterResponseDto;
import sopt.univoice.domain.notice.dto.response.SaveNoticeGetResponse;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.NoticeImage;
import sopt.univoice.domain.notice.entity.NoticeLike;
import sopt.univoice.domain.notice.entity.SaveNotice;
import sopt.univoice.domain.notice.repository.NoticeLikeRepository;
import sopt.univoice.domain.notice.repository.NoticeRepository;
import sopt.univoice.domain.notice.repository.SaveNoticeRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.domain.user.repository.UserRepository;
import sopt.univoice.infra.common.exception.message.BusinessException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.external.S3Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final NoticeLikeRepository noticeLikeRepository;
    private final SaveNoticeRepository saveNoticeRepository;

    @Transactional
    public NoticeRegisterResponseDto registerNotice(NoticeRegisterRequestDto noticeRegisterRequestDto, List<MultipartFile> files, Long memberId) {

        Member member = userRepository.findByIdOrThrow(memberId); // 아직 accessToken부분이 구현이 안되어서 임시로 사용자 id 설정함
        // 나중에 accessToken에서 사용자 id를 추출하는 코드 구현해서 Long id를 파라미터로 받아올 듯

        if (member.getAffiliation().getRole() != Role.APPROVEADMIN) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZED_EXCEPTION);
        }

        List<NoticeImage> noticeImages = uploadImages(files);

        LocalDateTime startTime = noticeRegisterRequestDto.startTime() != null ? noticeRegisterRequestDto.startTime() : null;
        LocalDateTime endTime = noticeRegisterRequestDto.endTime() != null ? noticeRegisterRequestDto.endTime() : null;
        String target = noticeRegisterRequestDto.target() != null ? noticeRegisterRequestDto.target() : "";
        // 대상, 일시가 null일 경우

        Notice notice = Notice.builder()
                            .title(noticeRegisterRequestDto.title())
                            .content(noticeRegisterRequestDto.content())
                            .target(target)
                            .startTime(startTime)
                            .endTime(endTime)
                            .category("공지사항")
                            .member(member)
                            .noticeLike(0)
                            .viewCount(0)
                            .saveCount(0)
                            .noticeImages(noticeImages)
                            .build();

        noticeImages.forEach(image -> image.setNotice(notice));
        return NoticeRegisterResponseDto.of(noticeRepository.save(notice));
    }

    private List<NoticeImage> uploadImages(List<MultipartFile> files) {
        return files.stream()
                   .map(file -> {
                       try {
                           String imageUrl = s3Service.uploadImage("notice-images/", file);
                           return NoticeImage.builder()
                                      .noticeImage(imageUrl)
                                      .build();
                       } catch (IOException e) {
                           throw new RuntimeException(ErrorMessage.UPLOAD_FAILED.getMessage(), e);
                       }
                   })
                   .collect(Collectors.toList());
    }

    @Transactional
    public NoticeGetResponseDto getNotice(Long noticeId, Long memberId) {
        Notice notice = noticeRepository.findByIdOrThrow(noticeId);

        Member member = userRepository.findByIdOrThrow(memberId);

        String writeAffiliation;

        if (notice.getMember().getAffiliation().getMembership() == Membership.TOTALCOUNCIL) {
            writeAffiliation = "총 학생회";
        } else if (notice.getMember().getAffiliation().getMembership() == Membership.COLLEGE) {
            writeAffiliation = "단과대학 학생회";
        } else if (notice.getMember().getAffiliation().getMembership() == Membership.STUDENTCOUNCIL) {
            writeAffiliation = "학과 학생회";
        } else {
            writeAffiliation = "소속을 찾을 수 없습니다";
        }

        List<String> imageList = notice.getNoticeImages().stream()
                                     .map(NoticeImage::getNoticeImage)
                                     .collect(Collectors.toList());

        return NoticeGetResponseDto.of(notice, writeAffiliation, imageList);
        // 세부 공지는 차피 메인홈을 거쳐 들어오는 것이므로 메인 홈에서 먼저 다니고 있는 대학교의 공지만 조회할 수 있도록 할 것이여서 그 로직은 여기선 구현 안함
    }

    @Transactional
    public void postLike(Long noticeId, Long memberId) {
        Notice notice = noticeRepository.findByIdOrThrow(noticeId);

        Member member = userRepository.findByIdOrThrow(memberId);
        // 추후 memberId 파라미터로 넣어 유저 검증

        boolean alreadyLiked = noticeLikeRepository.existsByNoticeAndMember(notice, member);
        if (alreadyLiked) {
            throw new BusinessException( ErrorMessage.ALREADY_LIKED);
        }

        NoticeLike noticeLike = NoticeLike.builder()
                                    .notice(notice)
                                    .member(member)
                                    .build();
        noticeLikeRepository.save(noticeLike);

        notice.incrementLike();
        noticeRepository.save(notice);
    }

    @Transactional
    public void deleteLike(Long noticeId, Long memberId) {
        Notice notice = noticeRepository.findByIdOrThrow(noticeId);

        Member member = userRepository.findByIdOrThrow(memberId);

        NoticeLike noticeLike = noticeLikeRepository.findByNoticeAndMember(notice, member)
                                    .orElseThrow(() -> new BusinessException(ErrorMessage.NOT_LIKE));

        noticeLikeRepository.delete(noticeLike);

        notice.decrementLike();
        noticeRepository.save(notice);
    }

    @Transactional
    public void saveNotice(Long noticeId, Long memberId) {
        Notice notice = noticeRepository.findByIdOrThrow(noticeId);

        Member member = userRepository.findByIdOrThrow(memberId);
        // 추후 memberId 파라미터로 넣어 유저 검증

        boolean alreadySaved = saveNoticeRepository.existsByNoticeAndMember(notice, member);
        if (alreadySaved) {
            throw new BusinessException(ErrorMessage.ALREADY_SAVED);
        }

        SaveNotice saveNotice = SaveNotice.builder()
                                    .notice(notice)
                                    .member(member)
                                    .build();
        saveNoticeRepository.save(saveNotice);

        notice.incrementSave();
        noticeRepository.save(notice);
    }

    @Transactional
    public void deleteSave(Long noticeId, Long memberId) {
        Notice notice = noticeRepository.findByIdOrThrow(noticeId);

        Member member = userRepository.findByIdOrThrow(memberId);

        SaveNotice saveNotice = saveNoticeRepository.findByNoticeAndMember(notice, member)
                                    .orElseThrow(() -> new BusinessException(ErrorMessage.NOT_SAVE));

        saveNoticeRepository.delete(saveNotice);

        notice.decrementSave();
        noticeRepository.save(notice);
    }

    @Transactional
    public List<SaveNoticeGetResponse> getSaveNotice(Long memberId) {

        Member member = userRepository.findByIdOrThrow(memberId);

        List<SaveNotice> saveNotices = saveNoticeRepository.findByMemberOrderByCreatedAtDesc(member);

        return saveNotices.stream()
                   .map(saveNotice -> {
                       Notice notice = saveNotice.getNotice();
                       List<String> imageList = notice.getNoticeImages().stream()
                                                    .map(NoticeImage::getNoticeImage)
                                                    .collect(Collectors.toList());
                       return SaveNoticeGetResponse.of(notice, imageList);
                   })
                   .collect(Collectors.toList());
    }
}