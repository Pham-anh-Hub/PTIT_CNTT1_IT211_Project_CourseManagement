package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.FileNotValidException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.CloudinaryService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Value("${spring.servlet.multipart.max-file-size}")
    private Long maxFileSize;
    private static final long MAX_VIDEO_SIZE = 100L * 1024 * 1024; // 100MB

    // chỉ cho phép những đinh dạng tài liệu cho phép
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            // Word
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            // PowerPoint
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            // ZIP
            "application/zip",
            "application/x-zip-compressed"
    );

    @Override
    public String uploadFile(MultipartFile file) {
        // kiểm tra file trống
        validateFile(file, ALLOWED_TYPES);
        return doUpload(file, "student_submission", "raw");
    }

    @Override
    public String uploadMaterialFile(MultipartFile file) {
        validateFile(file, ALLOWED_TYPES);
        return doUpload(file, "learning_materials/files", "raw");
    }

    @Override
    public String uploadMaterialVideo(MultipartFile file) {
        validateFile(file, ALLOWED_TYPES);
        return doUpload(file, "learning_materials/videos", "video");
    }


    public void validateFile(MultipartFile file, Set<String> allowedTypes){
        if(file == null || file.isEmpty()){
            throw new FileNotValidException("File không hợp lệ, vui lòng kiểm tra lại");
        }
        // Kiểm tra đinh dạng file
        if (!allowedTypes.contains(file.getContentType())){
            throw new FileNotValidException("Định dạng file không hợp lệ, vui lòng kiểm tra lại");
        }

        if (file.getSize() > maxFileSize){
            throw new FileNotValidException("Dung lượng file vượt quá giới hạn, tối đa 15MB");
        }
    }


    public String doUpload(MultipartFile file, String folder, String resourceType){
        try{
            // hứng keết quả trả về của phương thức upload trong cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto",       // BẮT BUỘC: Tự động nhận diện định dạng file tài liệu (PDF, DOCX, ZIP...)
                    "use_filename", true,         // BẮT BUỘC: Giữ lại tên file gốc ban đầu của người dùng
                    "unique_filename", true  // Thêm hậu tố ngẫu nhiên để tránh việc các file trùng tên ghi đè lên nhau
            )); // chuyển file về dạng nhị phân
            return uploadResult.get("secure_url").toString();
        }catch (Exception e){
            throw new RuntimeException("Lỗi kết nối Cloud, vui lòng thử lại");
        }

    }
}
