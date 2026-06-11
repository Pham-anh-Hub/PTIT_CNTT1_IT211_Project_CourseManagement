package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;

import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.EnrollmentRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.EnrollmentResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enrollCourse(Long studentId, EnrollmentRequest request);
    List<EnrollmentResponse> getMyEnrollments(Long studentId);
    ThrowResponse cancelEnrollment(Long studentId, Long courseId);
}