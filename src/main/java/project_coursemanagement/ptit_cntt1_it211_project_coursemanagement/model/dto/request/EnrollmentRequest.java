package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    @NotNull(message = "courseId không được để trống")
    private Long courseId;
}