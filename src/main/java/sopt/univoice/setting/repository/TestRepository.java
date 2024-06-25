package sopt.univoice.setting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.setting.domain.TestMember;

public interface TestRepository extends JpaRepository<TestMember, Long> {


}

