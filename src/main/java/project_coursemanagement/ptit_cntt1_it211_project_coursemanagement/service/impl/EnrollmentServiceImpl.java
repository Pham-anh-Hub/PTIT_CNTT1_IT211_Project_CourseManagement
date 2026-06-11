package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.CourseNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.DataDuplicateException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.EnrollmentNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.EnrollmentRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.EnrollmentResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Courses;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Enrollment;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.CourseRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.EnrollmentRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.EnrollmentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UsersRepository usersRepository;
    private final CourseRepository courseRepository;

    @Override
    public EnrollmentResponse enrollCourse(Long studentId, EnrollmentRequest request) {

        Users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy sinh viên"));

        if (student.getRole() == null || student.getRole().getCode() != RoleName.STUDENT) {
            throw new IllegalArgumentException("Người dùng không phải sinh viên");
        }

        Courses course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Không tìm thấy khóa học"));

        if (!course.isActive()) {
            throw new IllegalStateException("Khóa học đã bị vô hiệu hóa");
        }

        if (enrollmentRepository.existsByStudents_IdAndCourse_Id(studentId, course.getId())) {
            throw new DataDuplicateException("Sinh viên đã đăng ký khóa học này rồi");
        }

        Enrollment enrollment = Enrollment.builder()
                .students(student)
                .course(course)
                .active(true)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        return mapToResponse(saved);
    }

    @Override
    public List<EnrollmentResponse> getMyEnrollments(Long studentId) {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getStudents().getId().equals(studentId))
                .filter(Enrollment::isActive)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ThrowResponse cancelEnrollment(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudents_IdAndCourse_Id(studentId, courseId).orElseThrow(() -> new EnrollmentNotFoundException("Không tìm thấy đăng ký khóa học nào"));

        enrollment.setActive(false);
        enrollmentRepository.save(enrollment);

        return ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Hủy đăng ký khóa học thành công")
                .path("/api/v1/student/enrollments/" + courseId)
                .build();
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .studentId(enrollment.getStudents().getId())
                .studentUsername(enrollment.getStudents().getUsername())
                .courseId(enrollment.getCourse().getId())
                .courseCode(enrollment.getCourse().getCourseCode())
                .courseName(enrollment.getCourse().getCourseName())
                .enrolledAt(enrollment.getEnrolledAt())
                .active(enrollment.isActive())
                .build();
    }
}