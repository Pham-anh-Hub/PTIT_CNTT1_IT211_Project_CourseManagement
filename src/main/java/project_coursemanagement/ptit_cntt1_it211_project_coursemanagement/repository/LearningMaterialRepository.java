package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.LearningMaterial;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;

import java.util.List;
import java.util.Optional;

public interface LearningMaterialRepository extends JpaRepository<LearningMaterial, Long> {
    // Lấy tất cả tài liệu đang active của 1 course (Student/Lecturer xem)
    List<LearningMaterial> findByCourse_IdAndIsActiveTrue(Long courseId);

    List<LearningMaterial> findByIsActiveAndCourse_Lecturer_Username(Boolean isActive, String courseLecturerUsername);

    // Lấy danh sách tài liệu học tập theo khóa học và khóa học do giảng viên phụ trách


    // Sinh viên xem danh sách tài liệu khóa học cụ thể (nhưng phai rkhoas học đã đăng ký)
    List<LearningMaterial> findByCourse_IdAndIsActiveTrueOrderByCreatedAtDesc(Long courseId);

    Optional<LearningMaterial> findByIdAndCourse_IdAndIsActiveIsTrue(Long id, Long courseId);

    // 2. Hàm thứ hai: Lấy danh sách toàn bộ tài liệu của khóa học cụ thể mà giảng viên phụ trách
    List<LearningMaterial> findByCourse_IdAndCourse_Lecturer_UsernameAndIsActiveTrue(Long courseId, String lecturerUsername);


    List<LearningMaterial> getAllByIsActiveTrue();

    List<LearningMaterial> findByUploadedByAndIsActive(Users uploadedBy, Boolean isActive);

    boolean existsByMaterialCode(String materialCode);
}