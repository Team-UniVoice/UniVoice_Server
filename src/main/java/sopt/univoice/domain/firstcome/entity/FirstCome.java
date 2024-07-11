package sopt.univoice.domain.firstcome.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.infra.persistence.BaseTimeEntity;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class FirstCome extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long FirstComeNumberLimit;


    private Long FirstComeNumberCurrent;

    @OneToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @OneToMany(mappedBy = "firstCome", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FirstComeNumberHistory> firstComeNumberHistories;







}
