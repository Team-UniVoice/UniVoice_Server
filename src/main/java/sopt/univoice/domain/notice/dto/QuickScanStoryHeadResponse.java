package sopt.univoice.domain.notice.dto;

public record QuickScanStoryHeadResponse(
        String universityName,
        int universityNameCount,
        String universityLogoImage,
        String collegeDepartmentName,
        int collegeDepartmentCount,
        String collegeDepartmentLogoImage,
        String departmentName,
        int departmentCount,
        String departmentLogoImage
) {
}
