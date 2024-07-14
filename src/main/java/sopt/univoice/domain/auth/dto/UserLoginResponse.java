package sopt.univoice.domain.auth.dto;

public record UserLoginResponse(
        String accessToken

) {

    public static UserLoginResponse of(
            String accessToken
    ) {
        return new UserLoginResponse(accessToken);
    }
}
