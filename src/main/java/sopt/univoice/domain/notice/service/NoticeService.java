package sopt.univoice.domain.notice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.notice.dto.request.NoticeRegisterRequestDto;
import sopt.univoice.domain.notice.dto.response.NoticeGetResponseDto;
import sopt.univoice.domain.notice.dto.response.NoticeRegisterResponseDto;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.NoticeImage;
import sopt.univoice.domain.notice.repository.NoticeRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.domain.user.repository.UserRepository;
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.external.S3Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public NoticeRegisterResponseDto registerNotice(NoticeRegisterRequestDto noticeRegisterRequestDto, List<MultipartFile> files) {
        Member member = userRepository.findByIdOrThrow(1L); // 아직 accessToken부분이 구현이 안되어서 임시로 사용자 id 설정함
        // 나중에 accessToken에서 사용자 id를 추출하는 코드 구현해서 Long id를 파라미터로 받아올 듯

        List<NoticeImage> noticeImages = uploadImages(files);

        Notice notice = Notice.builder()
                            .title(noticeRegisterRequestDto.title())
                            .content(noticeRegisterRequestDto.content())
                            .target(noticeRegisterRequestDto.target())
                            .startTime(noticeRegisterRequestDto.startTime())
                            .endTime(noticeRegisterRequestDto.endTime())
                            .category("공지사항")
                            .member(member)
                            .noticeLike(0)
                            .viewCount(0)
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
    public NoticeGetResponseDto getNotice(Long noticeId) {

        Notice notice = noticeRepository.findByIdOrThrow(noticeId);

        Member member = userRepository.findByIdOrThrow(1L);

        // 소속명 설정
        String writeAffiliation;
        if ("총학생회".equals(member.getAffiliation().getAffiliation())) {
            writeAffiliation = member.getAffiliation().getAffiliationName() + " 총 학생회";
        } else if ("단과대학".equals(member.getAffiliation().getAffiliation()) || "학생회".equals(member.getAffiliation().getAffiliation())) {
            writeAffiliation = member.getAffiliation().getAffiliationName() + " 학생회";
        } else {
            writeAffiliation = "소속을 찾을 수 없습니다";
        }

        List<String> imageList = notice.getNoticeImages().stream()
                                     .map(NoticeImage::getNoticeImage)
                                     .collect(Collectors.toList());

        return NoticeGetResponseDto.of(notice, writeAffiliation, imageList);

    }
}