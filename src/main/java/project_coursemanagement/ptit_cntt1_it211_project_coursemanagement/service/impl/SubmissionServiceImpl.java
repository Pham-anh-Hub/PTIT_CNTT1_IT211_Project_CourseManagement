package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.AssignmentInvalidException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.SubmissionRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.SubmissionResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Assignment;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Submission;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.SubmissionStatus;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.AssignmentRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.SubmissionRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.CloudinaryService;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.SubmissionService;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final UsersRepository usersRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public SubmissionResponse submitAssignment(String username, SubmissionRequest request, MultipartFile reportFile) {
        // kiểm tra sinh viên tồn tại hay k
        Users users = usersRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không tìm thấy sinh viên " + username));

        // Kiểm tra bài tập này coó tồn tại hay k
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId()).orElseThrow(() -> new AssignmentInvalidException("Bài tập không tồn tại, vui lòng kiểm tra lại"));

        if (submissionRepository.existsByStudent_IdAndAssignment_Id(users.getId(), assignment.getId())){
            throw new RuntimeException("Bài tập đã được nộp trước đó, vui lòng kiểm tra lại");
        };

        // 3. Kiểm tra và định dạng chuỗi link GitHub nếu sinh viên có điền thông tin
        String cleanGithubUrl = null;
        if (request.getGithubUrl() != null && !request.getGithubUrl().trim().isEmpty()) {
            cleanGithubUrl = request.getGithubUrl().trim();
            if (!cleanGithubUrl.startsWith("https://github.com")) {
                throw new RuntimeException("Đường dẫn mã nguồn không hợp lệ, vui lòng kiểm tra lại");
            }
        }
        // 4. Đẩy file thô lên đám mây Cloudinary -> Rút về chuỗi secure_url mã hóa HTTPS bảo mật
        String secureReportUrl = cloudinaryService.uploadFile(reportFile);

        // 5. Tự động so khớp thời gian thực tế với Hạn chót (Deadline) của Giảng viên để gán nhãn trạng thái
        SubmissionStatus finalStatus = LocalDateTime.now().isAfter(assignment.getDeadline())
                ? SubmissionStatus.LATE
                : SubmissionStatus.SUBMITTED;

        // 6. Dựng thực thể dữ liệu và lưu xuống bảng Submissions trong Database
        Submission submission = Submission.builder()
                .id(null)
                .student(users)
                .assignment(assignment)
                .githubUrl(cleanGithubUrl)
                .reportUrl(secureReportUrl)
                .status(finalStatus)
                .submittedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Submission savedSubmission = submissionRepository.save(submission);
        return SubmissionResponse.builder()
                .id(savedSubmission.getId())
                .studentName(savedSubmission.getStudent().getFullName())
                .assignmentTitle(savedSubmission.getAssignment().getTitle())
                .githubUrl(savedSubmission.getGithubUrl())
                .reportUrl(savedSubmission.getReportUrl())
                .status(savedSubmission.getStatus().name())
                .submittedAt(savedSubmission.getSubmittedAt())
                .build();

    }
}
