package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.student;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.LearningMaterialResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.LearningMaterialService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class MaterialsController {

    private final LearningMaterialService learningMaterialService;


    @GetMapping("/{courseId}/materials/{materialId}")
    public ResponseEntity<?> getMaterialDetailForStudent(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @PathVariable Long courseId,
            @PathVariable Long materialId
    ) {
        LearningMaterialResponse data = learningMaterialService.getMaterialByCourseIdAndCourseLecture(userPrinciple, materialId, courseId);

        Map<String, Object> successBody = new LinkedHashMap<>();
        successBody.put("success", true);
        successBody.put("message", "Tải tài liệu học tập thành công!");
        successBody.put("data", data);
        return ResponseEntity.ok(successBody);
    }

    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getCourseMaterialsForStudent(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @PathVariable Long courseId
    ) {
        List<LearningMaterialResponse> dataList = learningMaterialService.getMaterialsByCourseWithRoleCheck(userPrinciple, courseId);

        Map<String, Object> successBody = new LinkedHashMap<>();
        successBody.put("success", true);
        successBody.put("message", "Tải danh sách tài liệu học tập của khóa học thành công!");
        successBody.put("data", dataList); // Phản hồi mảng [ {}, {} ]

        return ResponseEntity.ok(successBody);
    }
}
