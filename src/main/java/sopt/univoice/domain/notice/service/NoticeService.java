package sopt.univoice.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.affiliation.entity.Affiliation;
import sopt.univoice.domain.auth.PrincipalHandler;
import sopt.univoice.domain.auth.repository.AuthRepository;
import sopt.univoice.domain.notice.dto.*;
import sopt.univoice.domain.notice.entity.*;
import sopt.univoice.domain.notice.repository.*;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.domain.affiliation.entity.Role;
import sopt.univoice.infra.common.exception.UnauthorizedException;
import sopt.univoice.infra.common.exception.message.BusinessException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.external.OpenAiService;
import sopt.univoice.infra.external.S3Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final AuthRepository authRepository;
    private final PrincipalHandler principalHandler;
    private final S3Service s3Service;
    private final OpenAiService openAiService;
    private final NoticeViewRepository noticeViewRepository;
    private final NoticeLikeRepository noticeLikeRepository;
    private final SaveNoticeRepository saveNoticeRepository;

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
                            .target(noticeCreateRequest.getTarget() != null ? noticeCreateRequest.getTarget() : "")
                            .startTime(noticeCreateRequest.getStartTime() != null ? noticeCreateRequest.getStartTime() : null)
                            .endTime(noticeCreateRequest.getEndTime() != null ? noticeCreateRequest.getEndTime() : null)
                            .member(member)
                            .contentSummary(summarizedContent)
                            .category("공지사항")  // category 값을 '공지사항'으로 설정
                            .build();
        noticeRepository.save(notice);
        System.out.println("Notice saved successfully with ID: " + notice.getId());

        // NoticeImage 엔티티 생성 및 저장
        List<MultipartFile> files = noticeCreateRequest.getImageList();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    String fileUrl = storeFile(file); // 파일 저장 로직 필요
                    NoticeImage noticeImage = NoticeImage.builder()
                                                  .notice(notice)
                                                  .noticeImage(fileUrl)
                                                  .build();
                    noticeImageRepository.save(noticeImage);
                    System.out.println("NoticeImage saved successfully with file URL: " + fileUrl);
                } catch (Exception e) {
                    System.err.println("Error saving NoticeImage: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // NoticeView 엔티티 생성 및 저장
        String universityName = member.getUniversityName();
        List<Member> universityMembers = authRepository.findByUniversityName(universityName);

        for (Member universityMember : universityMembers) {
            NoticeView noticeView = NoticeView.builder()
                                        .notice(notice)
                                        .member(universityMember)
                                        .readAt(false)
                                        .build();
            noticeViewRepository.save(noticeView);
        }
    }

    private String storeFile(MultipartFile file) {
        try {
            String fileUrl = s3Service.uploadImage("notice-images/", file);
            return fileUrl;
        } catch (IOException e) {
            System.err.println("File upload failed: " + e.getMessage());
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

        // 이미 좋아요를 누른 것인지 확인
        boolean alreadyLiked = noticeLikeRepository.existsByNoticeAndMember(notice, member);
        if (alreadyLiked) {
            throw new BusinessException(ErrorMessage.ALREADY_LIKED);
        }

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

    @Transactional
    public void saveNotice(Long noticeId) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        // member와 notice를 가져옵니다.
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

        // 이미 저장한 공지인지 확인하는 부분 추가
        boolean alreadySaved = saveNoticeRepository.existsByNoticeAndMember(notice, member);
        if (alreadySaved) {
            throw new BusinessException(ErrorMessage.ALREADY_SAVED);
        }

        // noticeSave를 1 증가시킵니다.
        notice.setNoticeSave(notice.getNoticeSave() + 1);
        noticeRepository.save(notice);

        // SaveNotice 엔티티 생성 및 저장
        SaveNotice saveNotice = SaveNotice.builder()
                .notice(notice)
                .member(member)
                .build();
        saveNoticeRepository.save(saveNotice);
    }

    @Transactional
    public void saveCancleNotice(Long noticeId) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        // member와 notice를 가져옵니다.
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

        // noticeSave를 1 감소시킵니다.
        notice.setNoticeSave(notice.getNoticeSave() - 1);
        noticeRepository.save(notice);

        // SaveNotice 엔티티 삭제
        SaveNotice saveNotice = saveNoticeRepository.findByNoticeAndMember(notice, member)
                                    .orElseThrow(() -> new RuntimeException("저장된 공지사항이 존재하지 않습니다."));
        saveNoticeRepository.delete(saveNotice);
    }

    @Transactional(readOnly = true)
    public List<NoticeSaveDTO> getSaveNoticeByUser() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        List<SaveNotice> saveNotices = saveNoticeRepository.findByMember(member);

        return saveNotices.stream()
                   .map(saveNotice -> {
                       Notice notice = saveNotice.getNotice();
                       String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
                       return new NoticeSaveDTO(
                           notice.getId(),
                           notice.getTitle(),
                           notice.getViewCount(),
                           notice.getNoticeLike(),
                           notice.getCategory(),
                           notice.getCreatedAt(),
                           image
                       );
                   })
                   .sorted(Comparator.comparing(NoticeSaveDTO::getCreatedAt))
                   .collect(Collectors.toList());
    }

    @Transactional
    public void viewCount(Long noticeId) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        // member와 notice를 가져옵니다.
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

        // viewCount를 1 증가시킵니다.
        notice.setViewCount(notice.getViewCount() + 1);
        noticeRepository.save(notice);

        // NoticeView에서 해당 member와 notice의 데이터를 찾아 readAt을 true로 설정합니다.
        NoticeView noticeView = noticeViewRepository.findByNoticeAndMember(notice, member)
                .orElseThrow(() -> new RuntimeException("조회 기록이 존재하지 않습니다."));
        noticeView.setReadAt(true);
        noticeViewRepository.save(noticeView);
    }



    @Transactional
    public List<QuickQueryNoticeDTO> getQuickNoticeByUserUniversity(String affiliation) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        String universityName = member.getUniversityName();

        List<Notice> notices = noticeRepository.findByMemberUniversityNameAndAffiliationAffiliation(universityName, affiliation);

        System.out.println("Notices: " + notices);

        List<Notice> filteredNotices = notices.stream()
                .filter(notice -> notice.getNoticeViews().stream()
                        .anyMatch(noticeView -> noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()))
                .collect(Collectors.toList());

        System.out.println("Filtered Notices Count: " + filteredNotices.size());

        return filteredNotices.stream().map(notice -> new QuickQueryNoticeDTO(
                notice.getId(),
                notice.getStartTime(),
                notice.getEndTime(),
                notice.getTitle(),
                notice.getNoticeLike(),
                (long) notice.getSaveNotices().size(),
                notice.getCategory()
        )).collect(Collectors.toList());
    }

    @Transactional
    public QuickScanDTO quickhead() {
        Long memberId = principalHandler.getUserIdFromPrincipal();
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        List<Notice> UniversityNotices = noticeRepository.findByMemberUniversityNameAndAffiliationAffiliation(universityName, "총학생회");
        List<Notice> collegeNotices = noticeRepository.findByMemberUniversityNameAndAffiliationAffiliation(universityName, "단과대학학생회");
        List<Notice> departmentNotices = noticeRepository.findByMemberUniversityNameAndAffiliationAffiliation(universityName, "과학생회");

        List<Notice> filteredUniversityNotices = new ArrayList<>();
        for (Notice notice : UniversityNotices) {
            for (NoticeView noticeView : notice.getNoticeViews()) {
                if (noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()) {
                    filteredUniversityNotices.add(notice);
                    break;
                }
            }
        }

        List<Notice> filteredCollegeNotices = new ArrayList<>();
        for (Notice notice : collegeNotices) {
            for (NoticeView noticeView : notice.getNoticeViews()) {
                if (noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()) {
                    filteredCollegeNotices.add(notice);
                    break;
                }
            }
        }

        List<Notice> filteredDepartmentNotices = new ArrayList<>();
        for (Notice notice : departmentNotices) {
            for (NoticeView noticeView : notice.getNoticeViews()) {
                if (noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()) {
                    filteredDepartmentNotices.add(notice);
                    break;
                }
            }
        }

        // 공지사항 필터링

        int universityNameCount = filteredUniversityNotices.size();
        int collegeDepartmentCount = filteredCollegeNotices.size();
        int departmentCount = filteredDepartmentNotices.size();

        List<Notice> notices = noticeRepository.findAllByMemberUniversityName(universityName);


        QuickScanDTO quickScans = new QuickScanDTO(
                universityName + " 총학생회", universityNameCount,
                collegeDepartmentName + " 학생회", collegeDepartmentCount,
                departmentName + " 학생회", departmentCount
        );

        List<NoticeDTO> noticeDTOs = new ArrayList<>();
        for (Notice notice : notices) {
            String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
            NoticeDTO noticeDTO = new NoticeDTO(
                notice.getId(),
                notice.getCreatedAt(),
                notice.getTitle(),
                notice.getNoticeLike(),
                notice.getViewCount(),
                notice.getCategory(),
                image
            );
            noticeDTOs.add(noticeDTO);
        }

        return  quickScans;
    }

    @Transactional
    public List<NoticeDTO>  getAllNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        String universityName = member.getUniversityName();
        List<Notice> notices = noticeRepository.findAllByMemberUniversityName(universityName);

        // 최신순 정렬
        notices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

        List<NoticeDTO> noticeDTOs = new ArrayList<>();
        for (Notice notice : notices) {
            String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
            NoticeDTO noticeDTO = new NoticeDTO(
                    notice.getId(),
                    notice.getCreatedAt(),
                    notice.getTitle(),
                    notice.getNoticeLike(),
                    notice.getViewCount(),
                    notice.getCategory(),
                    image
            );
            noticeDTOs.add(noticeDTO);
        }

        return  noticeDTOs;
    }

    @Transactional
    public List<NoticeDTO> getUniversityNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        String universityName = member.getUniversityName();

        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총 학생회");
        // 최신순 정렬
        universityNotices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

        List<NoticeDTO> noticeResponseDTOs = new ArrayList<>();
        for (Notice notice : universityNotices) {
            String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
            NoticeDTO noticeDTO = new NoticeDTO(
                notice.getId(),
                notice.getCreatedAt(),
                notice.getTitle(),
                notice.getNoticeLike(),
                notice.getViewCount(),
                notice.getCategory(),
                image
            );
            noticeResponseDTOs.add(noticeDTO);
        }



        return  noticeResponseDTOs;
    }

    @Transactional
    public List<NoticeResponseDTO> getCollegeDepartmentNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();

        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, "단과대학 학생회");

        // 최신순 정렬
        universityNotices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

        List<NoticeResponseDTO> noticeResponseDTOs = universityNotices.stream().map(notice -> {
            String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
            return new NoticeResponseDTO(
                notice.getId(),
                notice.getCreatedAt(),
                notice.getTitle(),
                notice.getNoticeLike(),
                notice.getViewCount(),
                notice.getCategory(),
                image
            );
        }).collect(Collectors.toList());
        return noticeResponseDTOs;
    }

    @Transactional
    public    List<NoticeResponseDTO> getDepartmentNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        List<Notice> departmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, departmentName, "학과 학생회");

        // 최신순 정렬
        departmentNotices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

        List<NoticeResponseDTO> noticeResponseDTOs = departmentNotices.stream().map(notice -> {
            String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
            return new NoticeResponseDTO(
                notice.getId(),
                notice.getCreatedAt(),
                notice.getTitle(),
                notice.getNoticeLike(),
                notice.getViewCount(),
                notice.getCategory(),
                image
            );
        }).collect(Collectors.toList());
        return noticeResponseDTOs;
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponseDTO getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

        // notice의 member_id를 통해 Member를 가져옵니다.
        Member member = notice.getMember();

        // Member의 affiliation_id를 통해 Affiliation을 가져옵니다.
        Affiliation affiliation = member.getAffiliation();

        // Affiliation의 affiliation 값을 가져옵니다.
        String writeAffiliation = affiliation.getAffiliation();

        return new NoticeDetailResponseDTO(
                notice.getId(),
                writeAffiliation,
                notice.getTitle(),
                notice.getTarget(),
                notice.getStartTime(),
                notice.getEndTime(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getViewCount(),
                notice.getNoticeImages().stream().map(NoticeImage::getNoticeImage).collect(Collectors.toList())
        );
    }

}