package sopt.univoice.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.univoice.domain.auth.PrincipalHandler;
import sopt.univoice.domain.auth.repository.AuthRepository;
import sopt.univoice.domain.mypage.dto.response.GetMypageReponseDto;
import sopt.univoice.domain.user.entity.Member;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final AuthRepository authRepository;
    private final PrincipalHandler principalHandler;

    public GetMypageReponseDto getMypage() {
        Long memberId = principalHandler.getUserIdFromPrincipal();

        Member member = authRepository.findById(memberId)
                            .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        GetMypageReponseDto getMypage = new GetMypageReponseDto(
            member.getId(),
            member.getName(),
            member.getCollegeDepartmentName(),
            member.getDepartmentName(),
            member.getAdmissionNumber() + "학번",
            member.getUniversityLogoImage()
        );
            return getMypage;
    }
}
