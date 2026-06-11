package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.CourseRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.CourseResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Courses;

import java.util.List;

public interface CourseService {
    // CRUD
    // getAll Course với phân trang, tìm kiếm
    List<CourseResponse> getAllCourseByNameOrLecturer(String courseName, String lecturer, Pageable pageable);

    // C
    CourseResponse createNewCourse(CourseRequest courseRequest);

    // R
    CourseResponse getCourseByIdOrCourseCode(String keyword);

    // U
    CourseResponse updateCourseInfo(Long courseId, CourseRequest courseRequest);
    CourseResponse patchCourseInfo(Long courseId, CourseRequest courseRequest);

    // D
    ThrowResponse deleteCourse(Long id, HttpServletRequest request);

}
