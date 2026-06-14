package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.BadRequestException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.CustomAccessDeniedException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.SubmissionRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Assignment;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Courses;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Submission;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.SubmissionStatus;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.AssignmentRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.EnrollmentRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.SubmissionRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.CloudinaryService;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl.SubmissionServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock private UsersRepository usersRepository;
    @Mock private AssignmentRepository assignmentRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private CloudinaryService cloudinaryService;
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private SubmissionServiceImpl submissionService;

    // ── Dữ liệu dùng chung ───────────────────────────────────

    private Users mockStudent() {
        Users student = new Users();
        student.setId(1L);
        student.setUsername("student01");
        student.setFullName("Nguyễn Văn B");
        return student;
    }

    private Courses mockCourse() {
        Courses course = new Courses();
        course.setId(1L);
        return course;
    }

    private Assignment mockAssignment(boolean isExpired) {
        Assignment assignment = new Assignment();
        assignment.setId(1L);
        assignment.setTitle("Bài tập tuần 1");
        assignment.setCourse(mockCourse());
        assignment.setDeadline(isExpired
                ? LocalDateTime.now().minusDays(1)   // đã hết hạn
                : LocalDateTime.now().plusDays(7));   // còn hạn
        return assignment;
    }
    // ── TC-SV-06: Sinh viên chưa đăng ký khóa học → CustomAccessDeniedException ──
    @Test
    @DisplayName("TC-SV-06: Sinh viên chưa enroll khóa học ném CustomAccessDeniedException")
    void submitAssignment_notEnrolled_throwsAccessDenied() {
        // Arrange
        Users student = mockStudent();
        Assignment assignment = mockAssignment(false);

        SubmissionRequest request = new SubmissionRequest();
        request.setAssignmentId(1L);

        when(usersRepository.findByUsername("student01"))
                .thenReturn(Optional.of(student));
        when(assignmentRepository.findById(1L))
                .thenReturn(Optional.of(assignment));

        // Sinh viên CHƯA đăng ký khóa học → trả về false
        when(enrollmentRepository.existsByStudents_IdAndCourse_IdAndActiveIsTrue(1L, 1L))
                .thenReturn(false);

        // Act + Assert
        assertThrows(CustomAccessDeniedException.class, () ->
                submissionService.submitAssignment("student01", request, null)
        );

        // Đảm bảo không upload file và không lưu DB
        verify(cloudinaryService, never()).uploadFile(any());
        verify(submissionRepository, never()).save(any());
    }

    // ── TC-SV-07: Cập nhật bài nộp sau deadline → BadRequestException ──
    @Test
    @DisplayName("TC-SV-07: Cập nhật bài nộp sau deadline ném BadRequestException")
    void updateSubmission_afterDeadline_throwsBadRequest() {
        // Arrange
        Users student = mockStudent();
        Assignment assignment = mockAssignment(true); // đã hết hạn

        Submission existingSubmission = Submission.builder()
                .id(1L)
                .student(student)
                .assignment(assignment)
                .status(SubmissionStatus.SUBMITTED)
                .build();

        SubmissionRequest request = new SubmissionRequest();
        request.setAssignmentId(1L);

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(existingSubmission));

        // Act + Assert
        assertThrows(BadRequestException.class, () ->
                submissionService.updateSubmission("student01", 1L, request, null)
        );

        // Đảm bảo không upload file và không lưu DB
        verify(cloudinaryService, never()).uploadFile(any());
        verify(submissionRepository, never()).save(any());
    }
}
