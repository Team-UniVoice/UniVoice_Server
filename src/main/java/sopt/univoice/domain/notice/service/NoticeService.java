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
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.external.OpenAiService;
import sopt.univoice.infra.external.S3Service;
import java.util.Comparator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.DayOfWeek;

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
        Member member = getAuthenticatedMember();
        String summarizedContent = summarizeContent(noticeCreateRequest.content());
        checkUserRole(member);

        Notice notice = saveNotice(noticeCreateRequest, member, summarizedContent);
        saveNoticeImages(noticeCreateRequest, notice);
        createNoticeViews(notice, member.getUniversityName());
    }

    @Transactional
    public void likeNotice(Long noticeId) {
        updateNoticeLike(noticeId, 1);
        saveNoticeLike(noticeId);
    }

    @Transactional
    public void likeCancleNotice(Long noticeId) {
        updateNoticeLike(noticeId, -1);
        deleteNoticeLike(noticeId);
    }

    @Transactional
    public void saveNotice(Long noticeId) {
        Member member = getAuthenticatedMember();
        Notice notice = getNoticeById(noticeId);
        saveNotice(notice, member);
    }

    @Transactional
    public void saveCancleNotice(Long noticeId) {
        Member member = getAuthenticatedMember();
        Notice notice = getNoticeById(noticeId);
        deleteSavedNotice(notice, member);
    }

    @Transactional
    public void viewCount(Long noticeId) {
        Member member = getAuthenticatedMember();
        Notice notice = getNoticeById(noticeId);
        increaseViewCount(notice);
        updateNoticeView(notice, member);
    }

    @Transactional
    public void viewCheck(Long noticeId) {
        Member member = getAuthenticatedMember();
        Notice notice = getNoticeById(noticeId);
        updateNoticeView(notice, member);
    }

    @Transactional(readOnly = true)
    public List<NoticeSaveListByUserResponse> getSaveNoticeByUser() {
        Member member = getAuthenticatedMember();
        List<SaveNotice> saveNotices = saveNoticeRepository.findByMember(member);

        return saveNotices.stream()
                .map(this::toNoticeSaveListByUserResponse)
                .sorted(Comparator.comparing(NoticeSaveListByUserResponse::createdAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<QuickNoticeListResponse> getQuickNoticeByUserUniversity(String affiliation) {
        Member member = getAuthenticatedMember();
        List<Notice> filteredNotices = filterAndSortNotices(member, affiliation);

        return filteredNotices.stream()
                .map(notice -> toQuickNoticeListResponse(notice, member, affiliation))
                .collect(Collectors.toList());
    }

    @Transactional
    public QuickScanStoryHeadResponse quickhead() {
        Member member = getAuthenticatedMember();

        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndAffiliationAffiliation(
                member.getUniversityName(), "총학생회");
        List<Notice> collegeNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(
                member.getUniversityName(), member.getCollegeDepartmentName(), "단과대학 학생회");
        List<Notice> departmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberDepartmentNameAndMemberAffiliationAffiliation(
                member.getUniversityName(), member.getCollegeDepartmentName(), member.getDepartmentName(), "학과 학생회");

        return toQuickScanStoryHeadResponse(universityNotices, collegeNotices, departmentNotices, member);
    }

    @Transactional
    public List<NoticeResponseDTO> getAllNoticeByUserUniversity() {
        Member member = getAuthenticatedMember();

        List<Notice> allNotices = getAllNoticesForUserUniversity(member);

        return allNotices.stream()
                .map(this::toNoticeResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<NoticeResponseDTO> getUniversityNoticeByUserUniversity() {
        return getNoticesByAffiliation("총학생회");
    }

    @Transactional
    public List<NoticeResponseDTO> getCollegeDepartmentNoticeByUserUniversity() {
        return getNoticesByAffiliation("단과대학 학생회");
    }

    @Transactional
    public List<NoticeResponseDTO> getDepartmentNoticeByUserUniversity() {
        return getNoticesByAffiliation("학과 학생회");
    }

    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse getQuickNoticeByUserUniversity(Long noticeId) {
        Member member = getAuthenticatedMember();
        Notice notice = getNoticeById(noticeId);

        String writeAffiliation = notice.getMember().getAffiliation().getAffiliation();
        boolean likeCheck = noticeLikeRepository.existsByMemberIdAndNoticeId(member.getId(), noticeId);
        boolean saveCheck = saveNoticeRepository.existsByMemberIdAndNoticeId(member.getId(), noticeId);
        String dayOfWeek = convertDayOfWeekToKorean(notice.getCreatedAt().getDayOfWeek());

        return new NoticeDetailResponse(
                notice.getId(), notice.getTitle(), notice.getContent(), notice.getNoticeLike(), notice.getViewCount(),
                notice.getTarget(), notice.getStartTime(), notice.getEndTime(), notice.getCategory(),
                notice.getContentSummary(), notice.getMember().getId(), writeAffiliation,
                notice.getNoticeImages().stream().map(NoticeImage::getNoticeImage).collect(Collectors.toList()),
                notice.getCreatedAt(), likeCheck, saveCheck, dayOfWeek
        );
    }

    // Private helper methods

    private Member getAuthenticatedMember() {
        Long memberId = principalHandler.getUserIdFromPrincipal();
        return authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    }

    private String summarizeContent(String content) {
        if (content.length() <= 150) {
            return content;
        } else {
            try {
                return openAiService.summarizeText(content);
            } catch (IOException e) {
                throw new RuntimeException("Error summarizing content", e);
            }
        }
    }

    private void checkUserRole(Member member) {
        if (member.getAffiliation().getRole() != Role.APPROVEADMIN) {
            throw new UnauthorizedException(ErrorMessage.JWT_UNAUTHORIZED_EXCEPTION);
        }
    }

    private Notice saveNotice(NoticeCreateRequest request, Member member, String summarizedContent) {
        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .target(request.target())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .member(member)
                .contentSummary(summarizedContent)
                .category("공지사항")
                .build();
        noticeRepository.save(notice);
        return notice;
    }

    private void saveNoticeImages(NoticeCreateRequest request, Notice notice) {
        if (request.noticeImages() != null) {
            for (MultipartFile file : request.noticeImages()) {
                String fileName = storeFile(file);
                NoticeImage noticeImage = NoticeImage.builder()
                        .notice(notice)
                        .noticeImage(fileName)
                        .build();
                notice.addNoticeImage(noticeImage);
                noticeImageRepository.save(noticeImage);
            }
        }
    }

    private String storeFile(MultipartFile file) {
        try {
            return s3Service.uploadImage("notice-images/", file);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    private void createNoticeViews(Notice notice, String universityName) {
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

    private void updateNoticeLike(Long noticeId, int delta) {
        Notice notice = getNoticeById(noticeId);
        notice.setNoticeLike(notice.getNoticeLike() + delta);
        noticeRepository.save(notice);
    }

    private void saveNoticeLike(Long noticeId) {
        Member member = getAuthenticatedMember();
        Notice notice = getNoticeById(noticeId);
        NoticeLike noticeLike = NoticeLike.builder()
                .notice(notice)
                .member(member)
                .build();
        noticeLikeRepository.save(noticeLike);
    }

    private void deleteNoticeLike(Long noticeId) {
        Member member = getAuthenticatedMember();
        Notice notice = getNoticeById(noticeId);
        NoticeLike noticeLike = noticeLikeRepository.findByNoticeAndMember(notice, member)
                .orElseThrow(() -> new RuntimeException("좋아요 정보가 존재하지 않습니다."));
        noticeLikeRepository.delete(noticeLike);
    }

    private void saveNotice(Notice notice, Member member) {
        SaveNotice saveNotice = SaveNotice.builder()
                .notice(notice)
                .member(member)
                .build();
        saveNoticeRepository.save(saveNotice);
    }

    private void deleteSavedNotice(Notice notice, Member member) {
        SaveNotice saveNotice = saveNoticeRepository.findByNoticeAndMember(notice, member)
                .orElseThrow(() -> new RuntimeException("저장된 공지사항이 존재하지 않습니다."));
        saveNoticeRepository.delete(saveNotice);
    }



    private void increaseViewCount(Notice notice) {
        notice.setViewCount(notice.getViewCount() + 1);
        noticeRepository.save(notice);
    }

    private void updateNoticeView(Notice notice, Member member) {
        NoticeView noticeView = noticeViewRepository.findByNoticeAndMember(notice, member)
                .orElseThrow(() -> new RuntimeException("조회 기록이 존재하지 않습니다."));
        noticeView.setReadAt(true);
        noticeViewRepository.save(noticeView);
    }

    private NoticeSaveListByUserResponse toNoticeSaveListByUserResponse(SaveNotice saveNotice) {
        Notice notice = saveNotice.getNotice();
        String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
        return new NoticeSaveListByUserResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getViewCount(),
                notice.getNoticeLike(),
                notice.getCategory(),
                notice.getStartTime(),
                notice.getEndTime(),
                saveNotice.getCreatedAt(),
                image
        );
    }

    private List<Notice> filterAndSortNotices(Member member, String affiliation) {
        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        List<Notice> notices;
        switch (affiliation) {
            case "총학생회":
                notices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총학생회");
                break;
            case "단과대학 학생회":
                notices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, "단과대학 학생회");
                break;
            case "학과 학생회":
                notices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, departmentName, "학과 학생회");
                break;
            default:
                throw new IllegalArgumentException("Invalid affiliation");
        }

        return notices.stream()
                .filter(notice -> notice.getNoticeViews().stream()
                        .anyMatch(noticeView -> noticeView.getMember().getId().equals(member.getId()) && !noticeView.isReadAt()))
                .sorted(Comparator.comparing(Notice::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    private QuickNoticeListResponse toQuickNoticeListResponse(Notice notice, Member member, String affiliation) {
        String writeAffiliation = "";
        if ("총학생회".equals(affiliation)) {
            writeAffiliation = member.getUniversityName() + " 총학생회";
        } else if ("단과대학 학생회".equals(affiliation)) {
            writeAffiliation = member.getCollegeDepartmentName() + " 학생회";
        } else if ("학과 학생회".equals(affiliation)) {
            writeAffiliation = member.getDepartmentName() + " 학생회";
        }

        String logoImage = getLogoImageForAffiliation(notice, affiliation);

        boolean saveCheck = saveNoticeRepository.existsByMemberIdAndNoticeId(member.getId(), notice.getId());

        return new QuickNoticeListResponse(
                notice.getId(),
                notice.getStartTime(),
                notice.getEndTime(),
                notice.getTitle(),
                notice.getTarget(),
                writeAffiliation,
                notice.getContentSummary(),
                notice.getNoticeLike(),
                notice.getViewCount(),
                notice.getCategory(),
                notice.getCreatedAt(),
                logoImage,
                saveCheck
        );
    }

    private String getLogoImageForAffiliation(Notice notice, String affiliation) {
        String logoImage = null;

        Affiliation foundAffiliation = notice.getMember().getAffiliation();
        if (foundAffiliation != null && foundAffiliation.getAffiliation().equals(affiliation)) {
            logoImage = foundAffiliation.getAffiliationLogoImage();
        }

        return logoImage;
    }

    private QuickScanStoryHeadResponse toQuickScanStoryHeadResponse(List<Notice> universityNotices, List<Notice> collegeNotices, List<Notice> departmentNotices, Member member) {
        String universityLogoImage = getLogoImageFromNotices(universityNotices, "총학생회");
        String collegeDepartmentLogoImage = getLogoImageFromNotices(collegeNotices, "단과대학 학생회");
        String departmentLogoImage = getLogoImageFromNotices(departmentNotices, "학과 학생회");

        int universityNameCount = countUnreadNotices(universityNotices, member.getId());
        int collegeDepartmentCount = countUnreadNotices(collegeNotices, member.getId());
        int departmentCount = countUnreadNotices(departmentNotices, member.getId());

        return new QuickScanStoryHeadResponse(
                member.getUniversityName() + " 총학생회", universityNameCount, universityLogoImage,
                member.getCollegeDepartmentName() + " 학생회", collegeDepartmentCount, collegeDepartmentLogoImage,
                member.getDepartmentName() + " 학생회", departmentCount, departmentLogoImage
        );
    }

    private String getLogoImageFromNotices(List<Notice> notices, String affiliation) {
        return notices.stream()
                .map(Notice::getMember)
                .map(Member::getAffiliation)
                .filter(a -> affiliation.equals(a.getAffiliation()))
                .map(Affiliation::getAffiliationLogoImage)
                .findFirst()
                .orElse(null);
    }

    private int countUnreadNotices(List<Notice> notices, Long memberId) {
        int count = 0;
        for (Notice notice : notices) {
            for (NoticeView noticeView : notice.getNoticeViews()) {
                if (noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private List<Notice> getAllNoticesForUserUniversity(Member member) {
        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(member.getUniversityName(), "총학생회");
        List<Notice> collegeDepartmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(member.getUniversityName(), member.getCollegeDepartmentName(), "단과대학 학생회");
        List<Notice> departmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberDepartmentNameAndMemberAffiliationAffiliation(member.getUniversityName(), member.getCollegeDepartmentName(), member.getDepartmentName(), "학과 학생회");

        List<Notice> allNotices = new ArrayList<>();
        allNotices.addAll(universityNotices);
        allNotices.addAll(collegeDepartmentNotices);
        allNotices.addAll(departmentNotices);

        allNotices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());
        return allNotices;
    }

    private List<NoticeResponseDTO> getNoticesByAffiliation(String affiliation) {
        Member member = getAuthenticatedMember();
        List<Notice> notices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(member.getUniversityName(), affiliation);

        return notices.stream()
                .sorted(Comparator.comparing(Notice::getCreatedAt).reversed())
                .map(this::toNoticeResponseDTO)
                .collect(Collectors.toList());
    }

    private NoticeResponseDTO toNoticeResponseDTO(Notice notice) {
        String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
        return new NoticeResponseDTO(
                notice.getId(),
                notice.getStartTime(),
                notice.getEndTime(),
                notice.getTitle(),
                notice.getNoticeLike(),
                notice.getViewCount(),
                notice.getCategory(),
                notice.getCreatedAt(),
                image
        );
    }

    private String convertDayOfWeekToKorean(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월";
            case TUESDAY:
                return "화";
            case WEDNESDAY:
                return "수";
            case THURSDAY:
                return "목";
            case FRIDAY:
                return "금";
            case SATURDAY:
                return "토";
            case SUNDAY:
                return "일";
            default:
                return "";
        }
    }
}
