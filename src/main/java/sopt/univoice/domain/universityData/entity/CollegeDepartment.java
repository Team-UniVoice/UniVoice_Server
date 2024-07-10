package sopt.univoice.domain.universityData.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(schema = "public")
public class CollegeDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String collegeDepartmentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;


    @OneToMany(mappedBy = "collegeDepartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> departments;
}
