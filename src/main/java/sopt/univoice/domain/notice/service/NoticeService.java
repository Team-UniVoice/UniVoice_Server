package sopt.univoice.domain.notice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.notice.dto.request.NoticeRegisterRequestDto;
import sopt.univoice.domain.notice.dto.response.NoticeRegisterResponseDto;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.NoticeImage;
import sopt.univoice.domain.notice.repository.NoticeRepository;
import sopt.univoice.domain.user.entity.User;
import sopt.univoice.domain.user.repository.UserRepository;
import sopt.univoice.infra.external.S3Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public NoticeRegisterResponseDto registerNotice(NoticeRegisterRequestDto noticeRegisterRequestDto) {

        User user = userRepository.findByIdOrThrow(1L); // 아직 accessToken부분이 구현이 안되어서 임시로 사용자 id 설정함
        // 나중에 accessToken에서 사용자 id를 추출하는 코드 구현해서 Long id를 파라미터로 받아올 듯

        List<NoticeImage> noticeImages = noticeRegisterRequestDto.noticeImage().stream()
                                             .map(imageUrl -> NoticeImage.builder()
                                                                  .noticeImage(imageUrl)
                                                                  .build())
                                             .collect(Collectors.toList());

        Notice notice = Notice.builder()
                            .title(noticeRegisterRequestDto.title())
                            .content(noticeRegisterRequestDto.content())
                            .target(noticeRegisterRequestDto.target())
                            .startTime(noticeRegisterRequestDto.startTime())
                            .endTime(noticeRegisterRequestDto.endTime())
                            .category("공지사항")
                            .user(user)
                            .noticeLike(0)
                            .viewCount(0)
                            .noticeImages(noticeImages)
                            .build();

        noticeImages.forEach(image -> image.setNotice(notice));
        return NoticeRegisterResponseDto.of(noticeRepository.save(notice));
    }

    @Transactional
    public List<String> uploadImages(List<MultipartFile> files) throws IOException { // 이미지 S3에 업로드하기 위한 로직
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl = s3Service.uploadImage("notice-images/", file);
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }
}
