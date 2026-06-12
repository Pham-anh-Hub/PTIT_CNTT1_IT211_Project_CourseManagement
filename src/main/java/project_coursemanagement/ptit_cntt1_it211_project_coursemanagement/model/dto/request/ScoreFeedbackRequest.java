package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScoreFeedbackRequest {
    @NotNull(message = "submissionId không được để trống")
    private Long submissionId;

    @NotNull(message = "Điểm số không được để trống")
    @Min(value = 0, message = "Điểm không được nhỏ hơn 0")
    @Max(value = 100, message = "Điểm không được lớn hơn 100")
    private Integer score;

    @NotBlank(message = "Nhận xét không được để trống")
    private String feedback;
}
