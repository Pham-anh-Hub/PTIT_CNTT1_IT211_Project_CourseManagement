package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;

import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LearningMaterialRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LearningMaterialUpdateRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.LearningMaterialResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.LearningMaterial;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;

import java.util.List;

public interface LearningMaterialService {
    // Tạo mới upload tài liệu
    LearningMaterialResponse createUploadMaterial(String lecturerUsername, String lecturerEmail, LearningMaterialRequest materialRequest, MultipartFile materialFile, MultipartFile videoFile);

    // Cập nhật tài liệu
    LearningMaterialResponse updateMaterial(String lecturerUsername, Long materialId, LearningMaterialUpdateRequest request);

    // xem taì liệu cụ thể của khóa học cụ thể
    LearningMaterialResponse getMaterialByCourseIdAndCourseLecture(UserPrinciple userPrinciple, Long materialId, Long courseId);

    List<LearningMaterialResponse> getMaterialsByCourseWithRoleCheck(UserPrinciple userPrinciple, Long courseId);
    // Sinh viên xem danh sách tài liệu của một khóa học cụ thể

}
