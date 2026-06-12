package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ScoreFeedbackResponse {
    private Long submissionId;
    private String studentName;
    private String assignmentTitle;
    private Integer score;
    private String feedback;
    private String status;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime gradedAt;
    private String gradedByName;
}