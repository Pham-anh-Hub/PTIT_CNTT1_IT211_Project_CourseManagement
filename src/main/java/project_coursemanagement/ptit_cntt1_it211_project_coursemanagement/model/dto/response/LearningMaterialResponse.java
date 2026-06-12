package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.MaterialType;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LearningMaterialResponse {
    private Long id;
    private String materialCode;
    private String title;
    private String description;
    private MaterialType materialType;
    private String fileUrl;       // FILE
    private String youtubeUrl;    // YOUTUBE
    private String videoUrl;      // VIDEO
    private String readingContent;// READING
    private String fileName;
    private Long fileSize;
    private Boolean isActive;
    private String uploadedByName;
    private Long courseId;
    private String courseTitle;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
