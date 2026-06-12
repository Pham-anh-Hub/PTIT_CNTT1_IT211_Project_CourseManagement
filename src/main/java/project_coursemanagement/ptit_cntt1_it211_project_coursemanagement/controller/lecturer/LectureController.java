package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.lecturer;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LearningMaterialRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LearningMaterialUpdateRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.ScoreFeedbackRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.LearningMaterialResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ScoreFeedbackResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.LearningMaterialService;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.SetPointFeedbackNoteService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
public class LectureController {
    private final SetPointFeedbackNoteService setPointFeedbackNoteService;
    private final LearningMaterialService learningMaterialService;

    @PostMapping("/grades" )
    public ResponseEntity<ScoreFeedbackResponse> scoreFeedbackSubmission(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @Valid @RequestBody ScoreFeedbackRequest request
            ){
        String lecturerName = userPrinciple.getUsername();
        ScoreFeedbackResponse response = setPointFeedbackNoteService.gradeSubmission(lecturerName, request);
        return ResponseEntity.ok(response);
    }


    @PostMapping(value = "/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // Đảm bảo chỉ có Giảng viên mới được phép gọi API này theo đúng Ma trận phân quyền SRS
    public ResponseEntity<Map<String, Object>> createUploadMaterial(
            @AuthenticationPrincipal UserPrinciple userPrinciple, // Lấy thông tin giảng viên đang đăng nhập từ JWT
            @Valid @ModelAttribute LearningMaterialRequest materialRequest, // Map data text từ form-data vào DTO
            @RequestParam(value = "materialFile", required = false) MultipartFile materialFile, // Cho phép null nếu không đính kèm file
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile // Cho phép null nếu không đính kèm video
    ) {
        // Trích xuất thông tin định danh cá nhân của Giảng viên từ hệ thống bảo mật Security Context
        String lecturerUsername = userPrinciple.getUsername();
        String lecturerEmail = userPrinciple.getUser().getEmail(); // Hãy đảm bảo đối tượng UserPrinciple của bạn có chứa hàm getEmail()

        // Gọi xuống tầng nghiệp vụ xử lý tích hợp dữ liệu và đám mây
        LearningMaterialResponse response = learningMaterialService.createUploadMaterial(
                lecturerUsername,
                lecturerEmail,
                materialRequest,
                materialFile,
                videoFile
        );

        // Trả về mã trạng thái 201 Created chuẩn HTTP Status Code Standard của tài liệu đề ra
        Map<String, Object> successBody = new LinkedHashMap<>();
        successBody.put("success", true);
        successBody.put("message", "Tải lên tài liệu học tập thành công!");
        successBody.put("data", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(successBody);
    }

    @PutMapping(value = "/materials/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMaterial(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @PathVariable("id") Long materialId,
            @Valid @ModelAttribute LearningMaterialUpdateRequest updateRequest
    ) {
        String lecturerUsername = userPrinciple.getUsername();

        // Gọi Service xử lý cập nhật động các trường chữ
        LearningMaterialResponse dataResponse = learningMaterialService.updateMaterial(
                lecturerUsername, materialId, updateRequest
        );

        // Đóng gói JSON trả về khớp 100% biểu mẫu thành công của tài liệu đặc tả
        Map<String, Object> successBody = new LinkedHashMap<>();
        successBody.put("success", true);
        successBody.put("message", "Cập nhật thông tin tài liệu học tập thành công!");
        successBody.put("data", dataResponse);

        return ResponseEntity.ok(successBody); // Trả về mã HTTP 200 OK
    }
}
