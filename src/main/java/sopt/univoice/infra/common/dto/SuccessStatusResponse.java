package sopt.univoice.infra.common.dto;

public record SuccessStatusResponse(
        int status,
        String message
) {

    public static SuccessStatusResponse of(SuccessMessage successMessage) {
        return new SuccessStatusResponse(successMessage.getStatus(), successMessage.getMessage());
    }

}
