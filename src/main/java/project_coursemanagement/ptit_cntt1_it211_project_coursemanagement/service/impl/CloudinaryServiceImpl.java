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

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Value("${spring.servlet.multipart.max-file-size}")
    private Long maxFileSize;

    // chỉ cho phép những đinh dạng tài liệu cho phép
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/x-zip-compressed",
            "application/zip",
            "application/x-rar-compressed"
    );

    @Override
    public String uploadFile(MultipartFile file) {
        // kiểm tra file trống
        if(file == null || file.isEmpty()){
            throw new FileNotValidException("File không hợp lệ, vui lòng kiểm tra lại");
        }
        // Kiểm tra đinh dạng file
        if (!ALLOWED_TYPES.contains(file.getContentType())){
            throw new FileNotValidException("Định dạng file không hợp lệ, vui lòng kiểm tra lại");
        }

        if (file.getSize() > maxFileSize){
            throw new FileNotValidException("Dung lượng file vượt quá giới hạn, tối đa 5MB");
        }

        try{
            // hứng keết quả trả về của phương thức upload trong cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "student_submissions",
                    "resource_type", "raw"
            )); // chuyển file về dạng nhị phân
            return uploadResult.get("secure_url").toString();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }
}
