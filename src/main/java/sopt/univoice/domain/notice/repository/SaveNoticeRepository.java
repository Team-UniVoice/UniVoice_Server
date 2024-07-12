package sopt.univoice.domain.notice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.SaveNotice;
import sopt.univoice.domain.user.entity.Member;

import java.util.List;
import java.util.Optional;

public interface SaveNoticeRepository extends JpaRepository<SaveNotice, Long> {
    boolean existsByNoticeAndMember(Notice notice, Member member);

    Optional<SaveNotice> findByNoticeAndMember(Notice notice, Member member);

    List<SaveNotice> findByMemberOrderByCreatedAtDesc(Member member);
}


