package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllNoticesResponseDTO {
    private QuickScanDTO quickScans;
    private List<NoticeDTO> notices;
}

