package sopt.univoice.setting.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.config.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
public class TestMember extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int age;

    public static TestMember create(String name,  int age) {
        return TestMember.builder()
                .name(name)
                .age(age)
                .build();
    }

    @Builder
    public TestMember(String name, int age) {
        this.name = name;
        this.age = age;
    }



}