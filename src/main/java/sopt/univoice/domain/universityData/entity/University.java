package sopt.univoice.domain.universityData.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String universityName;

    @OneToMany(mappedBy = "university")
    private List<CollegeDepartment> collegeDepartments;
}
