package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.student;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.SubmissionRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.SubmissionResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.SubmissionService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/student/submissions")
@RequiredArgsConstructor
public class UploadAssignmentController {

    private final SubmissionService submissionService;

    // API tiếp nhận nộp đồ án/bài tập qua luồng dữ liệu multipart/form-data
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<SubmissionResponse> submitAssignment(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @Valid @RequestPart("metadata") SubmissionRequest request, // Phần nhận dữ liệu JSON
            @RequestPart("file") MultipartFile file                     // Phần nhận tệp nhị phân thô
    ) {
        String username = userPrinciple.getUsername(); // Trích xuất tự động Username từ Token đang đăng nhập
        SubmissionResponse response = submissionService.submitAssignment(username, request, file);
        return ResponseEntity.ok(response);
    }
}
