package sopt.univoice.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.auth.PrincipalHandler;
import sopt.univoice.domain.auth.repository.AuthRepository;
import sopt.univoice.domain.notice.dto.*;
import sopt.univoice.domain.notice.entity.*;
import sopt.univoice.domain.notice.repository.*;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.domain.affiliation.entity.Role;
import sopt.univoice.infra.common.exception.UnauthorizedException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.external.OpenAiService;
import sopt.univoice.infra.external.S3Service;

import java.io.IOException;
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
    private final NoticeLikeRepository noticeLikeRepository;
    private final SaveNoticeRepository saveNoticeRepository;
    private final NoticeViewRepository noticeViewRepository;

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

    @Transactional
    public void saveNotice(Long noticeId) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        // member와 notice를 가져옵니다.
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

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
                    return new NoticeSaveDTO(
                            notice.getId(),
                            notice.getTitle(),
                            notice.getViewCount(),
                            notice.getNoticeLike(),
                            notice.getCategory(),
                            notice.getStartTime(),
                            notice.getEndTime()
                    );
                })
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
    public GetAllNoticesResponseDTO getAllNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        // 회원 정보 가져오기
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        // 공지사항 필터링
        List<Notice> notices = noticeRepository.findAllByMemberUniversityName(universityName);

        int universityNameCount = (int) notices.stream()
                .filter(notice -> notice.getMember().getAffiliation().getAffiliation().equals("총학생회"))
                .count();

        int collegeDepartmentCount = (int) notices.stream()
                .filter(notice -> notice.getMember().getAffiliation().getAffiliation().equals("단과대학학생회"))
                .count();

        int departmentCount = (int) notices.stream()
                .filter(notice -> notice.getMember().getAffiliation().getAffiliation().equals("과학생회"))
                .count();

        QuickScanDTO quickScans = new QuickScanDTO(
                universityName + " 총학생회", universityNameCount,
                collegeDepartmentName + " 학생회", collegeDepartmentCount,
                departmentName + " 학생회", departmentCount
        );

        List<NoticeDTO> noticeDTOs = notices.stream()
                .map(notice -> new NoticeDTO(
                        notice.getId(),
                        notice.getStartTime(),
                        notice.getEndTime(),
                        notice.getTitle(),
                        notice.getNoticeLike(),
                        notice.getSaveNotices().stream().count(),
                        notice.getCategory().toString() // assuming category is an enum or string
                ))
                .collect(Collectors.toList());

        return new GetAllNoticesResponseDTO(quickScans, noticeDTOs);
    }

    @Transactional
    public GetMainNoticesResponseDTO getUniversityNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        long universityNameCount = noticeRepository.countByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총학생회");
        long collegeDepartmentCount = noticeRepository.countByMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(collegeDepartmentName, "단과대학학생회");
        long departmentCount = noticeRepository.countByMemberDepartmentNameAndMemberAffiliationAffiliation(departmentName, "과학생회");

        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총학생회");

        List<NoticeResponseDTO> noticeResponseDTOs = universityNotices.stream().map(notice -> new NoticeResponseDTO(
                notice.getId(),
                notice.getStartTime(),
                notice.getEndTime(),
                notice.getTitle(),
                notice.getNoticeLike(),
                (long) notice.getSaveNotices().size(),
                notice.getCategory()
        )).collect(Collectors.toList());

        QuickScanResponseDTO quickScans = new QuickScanResponseDTO(
                universityName,
                universityNameCount,
                collegeDepartmentName,
                collegeDepartmentCount,
                departmentName,
                departmentCount
        );

        return new GetMainNoticesResponseDTO(quickScans, noticeResponseDTOs);
    }






    @Transactional
    public GetMainNoticesResponseDTO getCollegeDepartmentNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        long universityNameCount = noticeRepository.countByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총학생회");
        long collegeDepartmentCount = noticeRepository.countByMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(collegeDepartmentName, "단과대학학생회");
        long departmentCount = noticeRepository.countByMemberDepartmentNameAndMemberAffiliationAffiliation(departmentName, "과학생회");

        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "단과대학학생회");

        List<NoticeResponseDTO> noticeResponseDTOs = universityNotices.stream().map(notice -> new NoticeResponseDTO(
                notice.getId(),
                notice.getStartTime(),
                notice.getEndTime(),
                notice.getTitle(),
                notice.getNoticeLike(),
                (long) notice.getSaveNotices().size(),
                notice.getCategory()
        )).collect(Collectors.toList());

        QuickScanResponseDTO quickScans = new QuickScanResponseDTO(
                universityName,
                universityNameCount,
                collegeDepartmentName,
                collegeDepartmentCount,
                departmentName,
                departmentCount
        );

        return new GetMainNoticesResponseDTO(quickScans, noticeResponseDTOs);
    }



    @Transactional
    public GetMainNoticesResponseDTO getDepartmentNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        long universityNameCount = noticeRepository.countByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총학생회");
        long collegeDepartmentCount = noticeRepository.countByMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(collegeDepartmentName, "단과대학학생회");
        long departmentCount = noticeRepository.countByMemberDepartmentNameAndMemberAffiliationAffiliation(departmentName, "과학생회");

        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "과학생회");

        List<NoticeResponseDTO> noticeResponseDTOs = universityNotices.stream().map(notice -> new NoticeResponseDTO(
                notice.getId(),
                notice.getStartTime(),
                notice.getEndTime(),
                notice.getTitle(),
                notice.getNoticeLike(),
                (long) notice.getSaveNotices().size(),
                notice.getCategory()
        )).collect(Collectors.toList());

        QuickScanResponseDTO quickScans = new QuickScanResponseDTO(
                universityName,
                universityNameCount,
                collegeDepartmentName,
                collegeDepartmentCount,
                departmentName,
                departmentCount
        );

        return new GetMainNoticesResponseDTO(quickScans, noticeResponseDTOs);
    }



}
