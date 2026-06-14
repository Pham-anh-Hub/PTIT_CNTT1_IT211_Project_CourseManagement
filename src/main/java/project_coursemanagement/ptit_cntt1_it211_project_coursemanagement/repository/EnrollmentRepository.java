package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Enrollment;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudents_IdAndCourse_Id(Long studentId, Long courseId);
    Optional<Enrollment> findByStudents_IdAndCourse_Id(Long studentId, Long courseId);
    boolean existsByStudents_IdAndCourse_IdAndActiveIsTrue(Long studentsId, Long courseId);
}