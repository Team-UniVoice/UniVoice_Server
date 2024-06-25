package sopt.univoice.setting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.univoice.setting.domain.TestMember;
import sopt.univoice.setting.repository.TestRepository;
import sopt.univoice.setting.service.dto.SettingCreateDto;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final TestRepository memberRepository;

    @Transactional
    public String createMember(
            SettingCreateDto memberCreateDto
    ) {
        TestMember member = TestMember.create(memberCreateDto.name(), memberCreateDto.age());
        memberRepository.save(member);
        return member.getId().toString();
    }

}
