package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.*;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.SubmissionRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.SubmissionResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Assignment;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Submission;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.SubmissionStatus;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.AssignmentRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.EnrollmentRepository;
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
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public SubmissionResponse submitAssignment(String username, SubmissionRequest request, MultipartFile reportFile) {
        // kiểm tra sinh viên tồn tại hay k
        Users users = usersRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không tìm thấy sinh viên " + username));

        // Kiểm tra bài tập này coó tồn tại hay k
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId()).orElseThrow(() -> new AssignmentInvalidException("Bài tập không tồn tại, vui lòng kiểm tra lại"));

        // Lấy ID của khóa học mà bài tập này trực thuộc
        Long courseId = assignment.getCourse().getId();

        // Kiểm tra xem sinh viên có đăng ký (Enrollment) khóa học này hay không
        // (Bạn cần khai báo hàm này trong EnrollmentRepository hoặc thông qua Enrollment service)
        boolean isEnrolled = enrollmentRepository.existsByStudents_IdAndCourse_IdAndActiveIsTrue(users.getId(), courseId);

        if (!isEnrolled) {
            throw new CustomAccessDeniedException("Thao tác bị từ chối! Bạn không tham gia khóa học này nên không thể nộp bài tập.");
        }

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

    @Override
    public SubmissionResponse updateSubmission(String username, Long submissionId, SubmissionRequest request, MultipartFile reportFile) {
        // 1. Kiểm tra bài nộp (Submission) có tồn tại trong hệ thống hay không
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin bài nộp này!"));

        // 2. Bảo mật: Xác thực xem người đang sửa có đúng là sinh viên đã nộp bài này hay không
        if (!submission.getStudent().getUsername().equals(username)) {
            throw new CustomAccessDeniedException("Thao tác bị từ chối! Bạn không có quyền chỉnh sửa bài nộp của sinh viên khác.");
        }

        // 3. KIỂM TRA THỜI HẠN: Chỉ cho phép sửa khi chưa hết hạn chót (Deadline)
        Assignment assignment = submission.getAssignment();
        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            throw new BadRequestException("Đã quá hạn chót nộp bài! Bạn không thể chỉnh sửa bài nộp này nữa.");
        }

        // 4. Cập nhật link GitHub (áp dụng cơ chế "gửi gì cập nhật nấy")
        if (request.getGithubUrl() != null) {
            String cleanGithubUrl = request.getGithubUrl().trim();
            if (!cleanGithubUrl.isEmpty()) {
                if (!cleanGithubUrl.startsWith("https://github.com")) {
                    throw new BadRequestException("Đường dẫn mã nguồn GitHub không hợp lệ, vui lòng kiểm tra lại");
                }
                submission.setGithubUrl(cleanGithubUrl);
            } else {
                submission.setGithubUrl(null); // Cho phép xóa link Github cũ nếu truyền chuỗi rỗng
            }
        }

        // 5. Cập nhật File báo cáo mới (Nếu có truyền file mới lên thì ghi đè, không thì giữ nguyên file cũ)
        if (reportFile != null && !reportFile.isEmpty()) {
            // Tùy chọn: Nếu bạn có hàm xóa file cũ trên Cloudinary thì gọi ở đây trước khi ghi đè để sạch bộ nhớ
            String secureReportUrl = cloudinaryService.uploadFile(reportFile);
            submission.setReportUrl(secureReportUrl);
        }

        // 6. Cập nhật các mốc thời gian hệ thống và trạng thái nộp bài
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setUpdatedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED); // Đảm bảo trạng thái luôn là SUBMITTED vì đang trong hạn

        // 7. Lưu lại sự thay đổi xuống database
        Submission savedSubmission = submissionRepository.save(submission);

        // 8. Trả về thông tin DTO kết quả sau khi cập nhật thành công
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
