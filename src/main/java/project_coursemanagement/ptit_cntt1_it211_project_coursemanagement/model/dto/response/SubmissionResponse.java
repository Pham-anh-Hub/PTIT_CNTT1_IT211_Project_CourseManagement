package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionResponse {
    private Long id;
    private String studentName;
    private String assignmentTitle;
    private String githubUrl;
    private String reportUrl; // Đường dẫn link HTTPS từ Cloudinary
    private String status;    // SUBMITTED hoặc LATE
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime submittedAt;
}