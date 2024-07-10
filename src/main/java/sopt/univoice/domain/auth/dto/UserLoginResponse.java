package sopt.univoice.domain.auth.dto;

public record UserLoginResponse(
        String accessToken,
        String userId
) {

    public static UserLoginResponse of(
            String accessToken,
            String userId
    ) {
        return new UserLoginResponse(accessToken, userId);
    }
}
