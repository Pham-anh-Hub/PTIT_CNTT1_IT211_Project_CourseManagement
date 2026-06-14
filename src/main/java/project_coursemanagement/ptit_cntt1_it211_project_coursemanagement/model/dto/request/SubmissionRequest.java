package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionRequest {
    @NotNull(message = "Vui lòng không để trống mã bài tập cần nộp")
    private Long assignmentId;
    private String submissionCode;
    private String githubUrl;
}
