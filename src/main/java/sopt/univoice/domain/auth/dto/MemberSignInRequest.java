package sopt.univoice.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public record MemberSignInRequest(String email, String password) {
}
