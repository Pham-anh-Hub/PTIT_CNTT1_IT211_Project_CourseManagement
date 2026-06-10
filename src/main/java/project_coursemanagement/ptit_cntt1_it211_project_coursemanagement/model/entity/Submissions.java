package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.SubmissionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;


    @Column(length = 1000)
    private String reportUrl;


    private Double score;

    @Column(length = 2000)
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gradedBy")
    private Users gradedBy;

    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}