package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(MultipartFile file);           // submission report (cũ)
    String uploadMaterialFile(MultipartFile file);   // tài liệu học tập
    String uploadMaterialVideo(MultipartFile file);
}
