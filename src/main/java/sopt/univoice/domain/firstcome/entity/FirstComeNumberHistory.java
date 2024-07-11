package sopt.univoice.domain.firstcome.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.univoice.infra.persistence.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
public class FirstComeNumberHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_come_id")
    private FirstCome firstCome;

}
