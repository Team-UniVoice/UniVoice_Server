package sopt.univoice.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sopt.univoice.domain.affiliation.entity.Affiliation;
import sopt.univoice.domain.affiliation.repository.AffiliationRepository;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    @Autowired
    private AffiliationRepository affiliationRepository;

    @Transactional
    public void createPost(NoticeCreateRequest noticeCreateRequest) {
        Long memberId = principalHandler.getUserIdFromPrincipal();
        System.out.println("Authenticated Member ID: " + memberId);
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        System.out.println("Member Role: " + member.getAffiliation().getRole());

        String content = noticeCreateRequest.getContent();
        String summarizedContent = null;
        if (content.length() <= 150) {
            summarizedContent = "150자 이내인 내용 입니다\n" + content;
        } else {
            try {
                summarizedContent = openAiService.summarizeText(content);
                System.out.println("Summarized Content: " + summarizedContent);
            } catch (IOException e) {
                System.err.println("Error summarizing content: " + e.getMessage());
                e.printStackTrace();
            }
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
                .category("공지사항")  // category 값을 '공지사항'으로 설정
                .build();
        noticeRepository.save(notice);
        System.out.println("Notice saved successfully with ID: " + notice.getId());

        // NoticeImage 엔티티 생성 및 저장
        if (noticeCreateRequest.getNoticeImages() != null) {
            for (MultipartFile file : noticeCreateRequest.getNoticeImages()) {
                String fileName = storeFile(file); // 파일 저장 로직 필요.
                System.out.println("Stored file name: " + fileName); // 디버깅 메시지 추가
                NoticeImage noticeImage = NoticeImage.builder()
                        .notice(notice)
                        .noticeImage(fileName)
                        .build();
                notice.addNoticeImage(noticeImage); // 관계 설정
                noticeImageRepository.save(noticeImage);
                System.out.println("NoticeImage saved successfully with file name: " + fileName);
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
                    String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage();
                    return new NoticeSaveDTO(
                            notice.getId(),
                            notice.getTitle(),
                            notice.getViewCount(),
                            notice.getNoticeLike(),
                            notice.getCategory(),
                            notice.getStartTime(),
                            notice.getEndTime(),
                            saveNotice.getCreatedAt(), // 추가된 부분
                            image
                    );
                }).sorted(Comparator.comparing(NoticeSaveDTO::getCreatedAt)) //저장된 날짜 기준 최신순 정렬
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
    public void viewCheck(Long noticeId) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        // member와 notice를 가져옵니다.
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

        // viewCount를 1 증가시킵니다.

        noticeRepository.save(notice);

        // NoticeView에서 해당 member와 notice의 데이터를 찾아 readAt을 true로 설정합니다.
        NoticeView noticeView = noticeViewRepository.findByNoticeAndMember(notice, member)
                .orElseThrow(() -> new RuntimeException("조회 기록이 존재하지 않습니다."));
        noticeView.setReadAt(true);
        noticeViewRepository.save(noticeView);
    }


    // 퀵 스캔  안읽은 공지 리스트  가져오기
    @Transactional
    public List<QuickQueryNoticeDTO> getQuickNoticeByUserUniversity(String affiliation) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        String universityName =         member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        List<Notice> notices;
        if ("총학생회".equals(affiliation)) {
            notices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총학생회");
        } else if ("단과대학학생회".equals(affiliation)) {
            notices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, "단과대학학생회");
        } else if ("과학생회".equals(affiliation)) {
            notices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, departmentName, "과학생회");
        } else {
            throw new IllegalArgumentException("Invalid affiliation");
        }

        List<Notice> filteredNotices = notices.stream()
                                           .filter(notice -> notice.getNoticeViews().stream()
                                                                 .anyMatch(noticeView -> noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()))
                                           .sorted(Comparator.comparing(Notice::getCreatedAt).reversed()) // 역순 정렬
                                           .collect(Collectors.toList());

        return filteredNotices.stream().map(notice -> {
            String writeAffiliation = "";
            if ("총학생회".equals(affiliation)) {
                writeAffiliation = "총학생회 " + notice.getMember().getAffiliation().getAffiliationName();
            } else if ("단과대학학생회".equals(affiliation)) {
                writeAffiliation = member.getCollegeDepartmentName() + " 학생회 " + notice.getMember().getAffiliation().getAffiliationName();
            } else if ("과학생회".equals(affiliation)) {
                writeAffiliation = member.getDepartmentName() + " 학생회 " + notice.getMember().getAffiliation().getAffiliationName();
            }

            return new QuickQueryNoticeDTO(
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
                notice.getCreatedAt()
            );
        }).collect(Collectors.toList());
    }

    // 퀵 스캔 헤드 가져오기
    @Transactional
    public QuickScanDTO quickhead() {
        Long memberId = principalHandler.getUserIdFromPrincipal();
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndAffiliationAffiliation(universityName, "총학생회");
        List<Notice> collegeNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, "단과대학학생회");
        List<Notice> departmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, departmentName, "과학생회");

        String universityLogoImage = null;
        String collegeDepartmentLogoImage = null;
        String departmentLogoImage = null;

        // '총학생회'에 대한 로고 이미지 가져오기
        Affiliation universityAffiliation = affiliationRepository.findByAffiliationAndAffiliationUniversityName("총학생회", universityName);
        if (universityAffiliation != null) {
            universityLogoImage = universityAffiliation.getAffiliationLogoImage();
        }

        // '단과대학학생회'에 대한 로고 이미지 가져오기
        Affiliation collegeAffiliation = affiliationRepository.findByAffiliationAndAffiliationUniversityName("단과대학학생회", universityName);
        if (collegeAffiliation != null) {
            collegeDepartmentLogoImage = collegeAffiliation.getAffiliationLogoImage();
        }

        // '과학생회'에 대한 로고 이미지 가져오기
        Affiliation departmentAffiliation = affiliationRepository.findByAffiliationAndAffiliationUniversityName("과학생회", universityName);
        if (departmentAffiliation != null) {
            departmentLogoImage = departmentAffiliation.getAffiliationLogoImage();
        }

        // 읽지 않은 총학생회 공지 필터링 및 카운트
        int universityNameCount = 0;
        for (Notice notice : universityNotices) {
            for (NoticeView noticeView : notice.getNoticeViews()) {
                if (noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()) {
                    universityNameCount++;
                    break;
                }
            }
        }

        // 읽지 않은 단과대학 공지 필터링 및 카운트
        int collegeDepartmentCount = 0;
        for (Notice notice : collegeNotices) {
            for (NoticeView noticeView : notice.getNoticeViews()) {
                if (noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()) {
                    collegeDepartmentCount++;
                    break;
                }
            }
        }

        // 읽지 않은 학과 공지 필터링 및 카운트
        int departmentCount = 0;
        for (Notice notice : departmentNotices) {
            for (NoticeView noticeView : notice.getNoticeViews()) {
                if (noticeView.getMember().getId().equals(memberId) && !noticeView.isReadAt()) {
                    departmentCount++;
                    break;
                }
            }
        }

        QuickScanDTO quickScans = new QuickScanDTO(
                universityName + " 총학생회", universityNameCount, universityLogoImage,
                collegeDepartmentName + " 학생회", collegeDepartmentCount, collegeDepartmentLogoImage,
                departmentName + " 학생회", departmentCount, departmentLogoImage
        );

        return quickScans;
    }

    @Transactional
    public List<NoticeResponseDTO> getAllNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        String universityName = member.getUniversityName();
        String collegeDepartmentName = member.getCollegeDepartmentName();
        String departmentName = member.getDepartmentName();

        // 총학생회 공지 조회
        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총학생회");

        // 단과대학 공지 조회
        List<Notice> collegeDepartmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, "단과대학학생회");

        // 학과 공지 조회
        List<Notice> departmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, departmentName, "과학생회");

        List<Notice> allNotices = new ArrayList<>();
        allNotices.addAll(universityNotices);
        allNotices.addAll(collegeDepartmentNotices);
        allNotices.addAll(departmentNotices);

        // 최신순 정렬
        allNotices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

        // NoticeResponseDTO 리스트로 변환
        List<NoticeResponseDTO> noticeResponseDTOs = allNotices.stream()
                                                         .map(notice -> {
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
                                                                 image // 이미지 추가
                                                             );
                                                         }).collect(Collectors.toList());

        return noticeResponseDTOs;
    }

    @Transactional
    public List<NoticeDTO> getUniversityNoticeByUserUniversity() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        String universityName = member.getUniversityName();

        List<Notice> universityNotices = noticeRepository.findByMemberUniversityNameAndMemberAffiliationAffiliation(universityName, "총학생회");

        // 최신순 정렬 (즉, 제일 최근에 올린 공지가 맨 위, 전에 올렸던 공지는 아래)
        universityNotices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

        List<NoticeDTO> noticeResponseDTOs = new ArrayList<>();
        for (Notice notice : universityNotices) {
            String image = notice.getNoticeImages().isEmpty() ? null : notice.getNoticeImages().get(0).getNoticeImage(); // 이미지 리스트 중 0번째 인덱스 값 가져옴, 비어있으면 null 반환
            NoticeDTO noticeDTO = new NoticeDTO(
                    notice.getId(),
                    notice.getStartTime(),
                    notice.getEndTime(),
                    notice.getTitle(),
                    notice.getNoticeLike(),
                    notice.getViewCount(), // 조회수로 수정
                    notice.getCategory(),
                    notice.getCreatedAt(),
                    image // 이미지 추가
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
        String collegeDepartmentName = member.getCollegeDepartmentName(); // 추가된 부분

        List<Notice> collegeDepartmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, "단과대학학생회");

        // 최신순 정렬
        collegeDepartmentNotices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

        List<NoticeResponseDTO> noticeResponseDTOs = collegeDepartmentNotices.stream().map(notice -> {
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
                        image // 이미지 추가
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

        List<Notice> departmentNotices = noticeRepository.findByMemberUniversityNameAndMemberCollegeDepartmentNameAndMemberDepartmentNameAndMemberAffiliationAffiliation(universityName, collegeDepartmentName, departmentName, "과학생회");

        // 최신순 정렬
        departmentNotices.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

        List<NoticeResponseDTO> noticeResponseDTOs = departmentNotices.stream()
                .map(notice -> {
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
                        image // 이미지 추가
                    );
                }).collect(Collectors.toList());
        return noticeResponseDTOs;
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponseDTO getNoticeById(Long noticeId) {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));

        // notice의 member_id를 통해 Member를 가져옵니다.
        Member member = notice.getMember();

        // Member의 affiliation_id를 통해 Affiliation을 가져옵니다.
        Affiliation affiliation = member.getAffiliation();

        // Affiliation의 affiliation 값을 가져옵니다.
        String writeAffiliation = affiliation.getAffiliation();

        // likeCheck 로직 추가
        boolean likeCheck = noticeLikeRepository.existsByMemberIdAndNoticeId(memberId, noticeId);

        // saveCheck 로직 추가
        boolean saveCheck = saveNoticeRepository.existsByMemberIdAndNoticeId(memberId, noticeId);

        // 요일 계산
        DayOfWeek dayOfWeekEnum = notice.getCreatedAt().getDayOfWeek();
        String dayOfWeek = convertDayOfWeekToKorean(dayOfWeekEnum);

        return new NoticeDetailResponseDTO(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getNoticeLike(),
                notice.getViewCount(),
                notice.getTarget(),
                notice.getStartTime(),
                notice.getEndTime(),
                notice.getCategory(),
                notice.getContentSummary(),
                notice.getMember().getId(),
                writeAffiliation, // 추가된 부분
                notice.getNoticeImages().stream().map(NoticeImage::getNoticeImage).collect(Collectors.toList()),
                notice.getCreatedAt(),
                likeCheck,
                saveCheck, // saveCheck 로직 추가
                dayOfWeek // dayOfWeek 추가
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