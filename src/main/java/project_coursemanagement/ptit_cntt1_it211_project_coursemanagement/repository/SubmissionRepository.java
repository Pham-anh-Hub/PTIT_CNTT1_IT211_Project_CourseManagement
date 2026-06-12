package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean findByStudent_IdAndAssignment_Id(Long studentId, Long assignmentId);

    boolean existsByStudent_IdAndAssignment_Id(Long studentId, Long assignmentId);
}
