package sopt.univoice.domain.notice.dto.response;

import sopt.univoice.domain.notice.entity.Notice;

import java.awt.print.Book;

public record NoticeRegisterResponseDto(
    Long noticeIdx
) {
    public static NoticeRegisterResponseDto of(Notice notice) {
        return new NoticeRegisterResponseDto(notice.getNoticeIdx());
    }
}