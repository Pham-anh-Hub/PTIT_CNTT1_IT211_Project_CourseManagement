package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long enrollmentId;
    private Long studentId;
    private String studentUsername;
    private Long courseId;
    private String courseCode;
    private String courseName;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime enrolledAt;
    private boolean active;
}