package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;

import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LearningMaterialRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LearningMaterialUpdateRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.LearningMaterialResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.LearningMaterial;

import java.util.List;

public interface LearningMaterialService {
    // Tạo mới upload tài liệu
    LearningMaterialResponse createUploadMaterial(String lecturerUsername, String lecturerEmail, LearningMaterialRequest materialRequest, MultipartFile materialFile, MultipartFile videoFile);

    // Cập nhật tài liệu
    LearningMaterialResponse updateMaterial(String lecturerUsername, Long materialId, LearningMaterialUpdateRequest request);

    // xem danh sách tài liệu của giảng viên
//    List<LearningMaterial> getLecturerMaterials();

    // Sinh viên xem
}
