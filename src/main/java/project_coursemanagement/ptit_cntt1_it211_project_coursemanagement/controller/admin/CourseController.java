package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.CourseRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.CourseResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<?> getAllCourses(
            @RequestParam(defaultValue = "") String courseName,
            @RequestParam(defaultValue = "") String lecturer,
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        List<CourseResponse> responses = courseService.getAllCourseByNameOrLecturer(courseName, lecturer, pageable);
        return ResponseEntity.ok(responses);
    }


    @PostMapping
    public ResponseEntity<?> createNewCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createNewCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{keyword}")
    public ResponseEntity<?> getCourseByIdOrCode(@PathVariable String keyword) {
        CourseResponse response = courseService.getCourseByIdOrCourseCode(keyword);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.updateCourseInfo(courseId, request);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{courseId}")
    public ResponseEntity<CourseResponse> patchCourse(@PathVariable Long courseId, @RequestBody CourseRequest request) {
        CourseResponse response = courseService.patchCourseInfo(courseId, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId, HttpServletRequest request) {
        ThrowResponse response = courseService.deleteCourse(courseId, request);
        return ResponseEntity.ok(response);
    }
}
