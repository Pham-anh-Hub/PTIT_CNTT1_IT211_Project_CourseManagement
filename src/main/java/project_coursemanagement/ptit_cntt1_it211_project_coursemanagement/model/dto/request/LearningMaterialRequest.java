package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.MaterialType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LearningMaterialRequest {

    @NotNull(message = "courseId không được để trống")
    private Long courseId;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
    private String title;

    private String description;

    @NotNull(message = "Loại tài liệu không được để trống")
    private MaterialType materialType;

    // Chỉ dùng khi materialType = YOUTUBE
    private String youtubeUrl;

    // Chỉ dùng khi materialType = READING
    private String readingContent;
}