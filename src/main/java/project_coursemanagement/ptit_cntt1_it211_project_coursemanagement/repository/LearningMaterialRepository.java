package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.LearningMaterial;

import java.util.List;

public interface LearningMaterialRepository extends JpaRepository<LearningMaterial, Long> {
    // Lấy tất cả tài liệu đang active của 1 course (Student/Lecturer xem)
    List<LearningMaterial> findByCourse_IdAndIsActiveTrue(Long courseId);

    // Lấy tất cả tài liệu lecturer đã upload (quản lý của mình)
    List<LearningMaterial> findByUploadedBy_UsernameAndIsActiveTrue(String username);

    // Lấy theo course + lecturer (giảng viên xem tài liệu mình upload trong course đó)
    List<LearningMaterial> findByCourse_IdAndUploadedBy_UsernameAndIsActiveTrue(
            Long courseId,
            String username
    );

    boolean existsByMaterialCode(String materialCode);
}