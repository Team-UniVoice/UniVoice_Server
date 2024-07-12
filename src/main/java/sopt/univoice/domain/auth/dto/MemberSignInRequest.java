package sopt.univoice.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignInRequest {
    private String email;
    private String password;
}
