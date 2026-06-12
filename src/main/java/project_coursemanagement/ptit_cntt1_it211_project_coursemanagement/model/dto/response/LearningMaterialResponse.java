package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import lombok.*;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.MaterialType;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LearningMaterialResponse {

    private Long id;
    private String materialCode;
    private String title;
    private String description;
    private MaterialType materialType;

    // Tuỳ materialType mà field nào có giá trị, field còn lại null
    private String fileUrl;        // FILE
    private String videoUrl;       // VIDEO
    private String youtubeUrl;     // YOUTUBE
    private String readingContent; // READING

    private String fileName;
    private Long fileSize;
    private Boolean isActive;

    // Thông tin người upload
    private String uploadedByName;
    private String uploadedByUsername;

    // Thông tin khóa học
    private Long courseId;
    private String courseTitle;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}