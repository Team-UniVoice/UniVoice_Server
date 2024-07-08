package sopt.univoice.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.auth.repository.AuthRepository;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthRepository authRepository;

    public boolean isDuplicateEmail(CheckEmailRequest checkEmailRequest) {
        return authRepository.existsByEmail(checkEmailRequest.getEmail());
    }



}
