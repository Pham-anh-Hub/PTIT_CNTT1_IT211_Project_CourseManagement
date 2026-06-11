package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.student;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.EnrollmentRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.EnrollmentService;

import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;

@RestController
@RequestMapping("/api/v1/student/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<?> enrollCourse(
            @AuthenticationPrincipal UserPrinciple principal,
            @Valid @RequestBody EnrollmentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enrollCourse(principal.getUserId(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyEnrollments(
            @AuthenticationPrincipal UserPrinciple principal
    ) {
        return ResponseEntity.ok(
                enrollmentService.getMyEnrollments(principal.getUserId())
        );
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> cancelEnrollment(
            @AuthenticationPrincipal UserPrinciple principal,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(
                enrollmentService.cancelEnrollment(principal.getUserId(), courseId)
        );
    }
}