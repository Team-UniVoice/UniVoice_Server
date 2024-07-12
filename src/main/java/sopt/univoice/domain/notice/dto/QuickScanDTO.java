package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuickScanDTO {
    private String universityName;
    private int universityNameCount;
    private String collegeDepartmentName;
    private int collegeDepartmentCount;
    private String departmentName;
    private int departmentCount;
}
