package sopt.univoice.domain.notice.dto;

public class AffiliationRequestDTO {
    private String affiliation;

    // 기본 생성자
    public AffiliationRequestDTO() {}

    // 파라미터가 있는 생성자
    public AffiliationRequestDTO(String affiliation) {
        this.affiliation = affiliation;
    }

    // getter와 setter 메소드
    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
}

