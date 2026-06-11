package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.CourseNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.DataDuplicateException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.CourseRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.CourseResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Courses;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.CourseRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.CourseService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UsersRepository usersRepository;

    @Override
    public List<CourseResponse> getAllCourseByNameOrLecturer(String courseName, String lecturer, Pageable pageable) {
        Page<Courses> coursesPage =
                courseRepository.searchCourse(
                        courseName,
                        lecturer,
                        pageable
                );

        return coursesPage.map(this::mapToResponse).toList();
    }

    @Override
    public CourseResponse createNewCourse(CourseRequest courseRequest) {
        // code khoá học gen tự động

        // tìm lecturer
        Users lecturer = usersRepository.findLecturerByEmailAndPhone(courseRequest.getLecturerEmail(), courseRequest.getLecturerPhone()).orElseThrow(() -> new UserNotFoundException("Không tìm thấy giảng viên"));

        Courses newCourse = Courses.builder()
                .courseCode(null)
                .courseName(courseRequest.getCourseName())
                .credit(courseRequest.getCredits())
                .lecturer(lecturer)
                .build();

        Courses saved = courseRepository.save(newCourse);
        saved.setCourseCode(("COURSE" + String.format("-%03d-%03d", saved.getId(), saved.getLecturer().getId())));

        return mapToResponse(courseRepository.save(saved));
    }

    @Override
    public CourseResponse getCourseByIdOrCourseCode(String keyword) {
        Courses target;
        // nếu là số -> tìm theo id
        if (keyword.matches("\\d+")) {
            Long id = Long.parseLong(keyword);
            target = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Không tìm thấy khóa học"));
        } else {
            target = courseRepository.findByCourseCode(keyword).orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        }
        return mapToResponse(target);
    }

    @Override
    public CourseResponse updateCourseInfo(Long courseId, CourseRequest courseRequest) {
        Courses target = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        // duplicate course code
        if (!target.getCourseCode().equals(courseRequest.getCourseCode()) && courseRepository.existsByCourseCode(courseRequest.getCourseCode())) {
            throw new DataDuplicateException("Mã khóa học đã tồn tại");
        }

        Users lecturer = usersRepository.findLecturerByEmailAndPhone(courseRequest.getLecturerEmail(), courseRequest.getLecturerPhone()).orElseThrow(() -> new UserNotFoundException("Không tìm thấy giảng viên"));
        target.setCourseCode(courseRequest.getCourseCode());
        target.setCourseName(courseRequest.getCourseName());
        target.setCredit(courseRequest.getCredits());
        target.setLecturer(lecturer);
        Courses saved = courseRepository.save(target);

        return mapToResponse(saved);
    }

    @Override
    public CourseResponse patchCourseInfo(Long courseId, CourseRequest request) {
        Courses target = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        // course code
        if (request.getCourseCode() != null) {
            if (!target.getCourseCode().equals(request.getCourseCode()) &&
                    courseRepository.existsByCourseCode(request.getCourseCode())) {
                throw new DataDuplicateException("Mã khóa học đã tồn tại");
            }
            target.setCourseCode(request.getCourseCode());
        }
        // course name
        if (request.getCourseName() != null) {
            target.setCourseName(request.getCourseName());
        }
        // credits
        if (request.getCredits() > 0) {
            target.setCredit(request.getCredits());
        }

        // lecturer
        if (request.getLecturerEmail() != null && request.getLecturerPhone() != null) {
            Users lecturer = usersRepository.findLecturerByEmailAndPhone(request.getLecturerEmail(), request.getLecturerPhone()).orElseThrow(() -> new UserNotFoundException("Không tìm thấy giảng viên"));
            target.setLecturer(lecturer);
        }

        Courses saved = courseRepository.save(target);

        return mapToResponse(saved);
    }

    @Override
    public ThrowResponse deleteCourse(Long id, HttpServletRequest request) {
        Courses target = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Không tìm thấy khóa học"));
        target.setActive(false);
        courseRepository.save(target);
        return ThrowResponse.builder().catchTime(LocalDateTime.now()).code(401).error(null).message("Xóa khóa học thành công").path(request.getContextPath()).build();
    }

    private CourseResponse mapToResponse(Courses course) {
        return CourseResponse.builder()
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credits(course.getCredit())
                .lecturerFullName(course.getLecturer().getFullName())
                .lecturerEmail(course.getLecturer().getEmail())
                .lecturerPhone(course.getLecturer().getPhone())
                .build();
    }
}
