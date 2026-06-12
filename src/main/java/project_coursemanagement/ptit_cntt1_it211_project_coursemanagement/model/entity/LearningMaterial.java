package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.MaterialType;

import java.time.LocalDateTime;


@Entity
@Table(name = "learning_material")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LearningMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Mã tài liệu
    private String materialCode;
    // Tiêu đề tài liệu
    private String title;
    // Mô tả
    @Column(columnDefinition = "TEXT")
    private String description;
    // Link file tài liệu/slide (PDF, DOCX, ZIP...) từ Cloudinary
    private String fileUrl;
    // Link video Youtube
    private String youtubeUrl;
    // Video upload trực tiếp lên Cloudinary
    private String videoUrl;
    // Nội dung bài đọc / markdown / html
    @Column(columnDefinition = "LONGTEXT")
    private String readingContent;
    // Tên file gốc
    private String fileName;
    // Loại tài liệu
    @Enumerated(EnumType.STRING)
    private MaterialType materialType;

    // Kích thước file
    private Long fileSize;
    // Trạng thái hoạt động
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Lecturer upload
    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private Users uploadedBy;

    // Thuộc course nào
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Courses course;
}

