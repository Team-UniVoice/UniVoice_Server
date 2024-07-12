package sopt.univoice.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuickScanResponseDTO {
    private String universityName;
    private long universityNameCount;
    private String collegeDepartmentName;
    private long collegeDepartmentCount;
    private String departmentName;
    private long departmentCount;
}