package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;

import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.SubmissionRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.SubmissionResponse;

public interface SubmissionService {
    SubmissionResponse submitAssignment(String username, SubmissionRequest request, MultipartFile file);
    SubmissionResponse updateSubmission(String username, Long submissionId, SubmissionRequest request, MultipartFile reportFile);
}
