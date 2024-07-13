package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMainNoticesResponseDTO {
//    private QuickScanResponseDTO quickScans;
private QuickScanDTO quickScans;
    private List<NoticeResponseDTO> notices;
}
