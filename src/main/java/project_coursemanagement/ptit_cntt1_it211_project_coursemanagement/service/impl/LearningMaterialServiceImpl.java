package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.*;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LearningMaterialRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LearningMaterialUpdateRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.LearningMaterialResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Courses;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.LearningMaterial;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.CourseRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.EnrollmentRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.LearningMaterialRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.CloudinaryService;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.LearningMaterialService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class LearningMaterialServiceImpl implements LearningMaterialService {
    private final UsersRepository usersRepository;
    private final CourseRepository courseRepository;
    private final CloudinaryService cloudinaryService;
    private final LearningMaterialRepository learningMaterialRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Value("${spring.servlet.multipart.max-file-size}")
    private Long maxFileSize;
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;

    @Override
    public LearningMaterialResponse createUploadMaterial(String lecturerUsername, String lecturerEmail, LearningMaterialRequest materialRequest, MultipartFile materialFile, MultipartFile videoFile) {
        // Kiểm tra giảng viên có tồn tại không
        Users lecturer = usersRepository.findByUsernameAndEmail(lecturerUsername, lecturerEmail).orElseThrow(() -> new UserNotFoundException("Giảng viên không tồn tại, vui lòng kiểm tra lại!"));
        // Kiểm tra khóa học có tồn tại k
        Courses courses = courseRepository.findById(materialRequest.getCourseId()).orElseThrow(() -> new CourseNotFoundException("Không tìm thấy khóa học, vui lòng kiểm tra lại"));

        String fileUrl = null;
        String fileName = null;
        String videoUrl = null;

        // upload file
        if (materialFile != null && !materialFile.isEmpty()) {
            if (materialFile.getSize() > maxFileSize) {
                throw new FileNotValidException("Tài liệu vượt quá dung lượng tối đa cho phép (15MB).");
            }
            fileUrl = cloudinaryService.uploadMaterialFile(materialFile);
            fileName = materialFile.getOriginalFilename(); // Lưu tên file gốc phục vụ hiển thị ở Client
        }

        if (videoFile != null && !videoFile.isEmpty()) {
            if (videoFile.getSize() > MAX_VIDEO_SIZE) {
                throw new FileNotValidException("Video vượt quá dung lượng tối đa cho phép (15MB).");
            }
            videoUrl = cloudinaryService.uploadMaterialVideo(videoFile);
        }

        if (materialRequest.getYoutubeUrl() != null && !materialRequest.getYoutubeUrl().isBlank()) {
            if (!materialRequest.getYoutubeUrl().contains("youtube.com") && !materialRequest.getYoutubeUrl().contains("youtu.be")) {
                throw new RuntimeException("Đường dẫn YouTube không đúng định dạng hợp lệ.");
            }
        }

        // 1.7 Xây dựng thực thể dữ liệu và lưu vào DB
            LearningMaterial material = new LearningMaterial();
        material.setMaterialCode(generateMaterialCode());
        material.setTitle(materialRequest.getTitle());
        material.setDescription(materialRequest.getDescription());

        // Nhận đồng thời toàn bộ các trường (Trường nào không gửi sẽ tự động mang giá trị NULL)
        material.setFileUrl(fileUrl);
        material.setFileName(fileName);
        material.setVideoUrl(videoUrl);
        material.setFileSize(materialFile != null && videoFile != null ? materialFile.getSize() + videoFile.getSize() : 0);
        material.setYoutubeUrl(materialRequest.getYoutubeUrl());
        material.setReadingContent(materialRequest.getReadingContent());

        material.setIsActive(true);
        material.setUploadedBy(lecturer);
        material.setCourse(courses);
        material.setCreatedAt(LocalDateTime.now());
        material.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(learningMaterialRepository.save(material));
    }

    @Override
    public LearningMaterialResponse updateMaterial(String lecturerUsername, Long materialId, LearningMaterialUpdateRequest request) {
        // Lấy ra tài liệu để kiểm tra xem có phải được giảng viên này phụ trách hay không
        LearningMaterial material = getMaterialAndCheckOwner(lecturerUsername, materialId);

        // 2. Chỉ cập nhật Tiêu đề nếu có gửi lên và không bị trống rỗng
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            material.setTitle(request.getTitle());
        }
        // 3. Chỉ cập nhật Mô tả nếu trường này được truyền lên (Cho phép giảng viên xóa mô tả bằng cách gửi chuỗi rỗng nếu muốn)
        if (request.getDescription() != null) {
            material.setDescription(request.getDescription());
        }
        // 4. Chỉ cập nhật Link Youtube nếu được gửi lên
        if (request.getYoutubeUrl() != null) {
            // Nếu có nhập link, kiểm tra định dạng trước khi cập nhật dữ liệu cũ
            if (!request.getYoutubeUrl().isBlank()) {
                if (!request.getYoutubeUrl().contains("youtube.com") && !request.getYoutubeUrl().contains("youtu.be")) {
                    throw new BadRequestException("Đường dẫn YouTube không đúng định dạng hợp lệ.");
                }
            }
            material.setYoutubeUrl(request.getYoutubeUrl());
        }

        // 5. Chỉ cập nhật Nội dung bài đọc nếu được gửi lên
        if (request.getReadingContent() != null) {
            material.setReadingContent(request.getReadingContent());
        }

        // Lấy file từ trong request DTO ra để kiểm tra
        MultipartFile newMaterialFile = request.getMaterialFile();
        if (newMaterialFile != null && !newMaterialFile.isEmpty()) {
            // Kiểm tra dung lượng file mới (maxFileSize)
            if (newMaterialFile.getSize() > maxFileSize) {
                throw new FileNotValidException("Tài liệu vượt quá dung lượng tối đa cho phép (15MB).");
            }
            // Đẩy file mới lên Cloudinary lấy URL mới
            String newFileUrl = cloudinaryService.uploadMaterialFile(newMaterialFile);

            // Ghi đè URL cũ và Tên file cũ bằng thông tin của file mới
            material.setFileUrl(newFileUrl);
            material.setFileName(newMaterialFile.getOriginalFilename());
        } // Nếu newMaterialFile == null, khối lệnh bị bỏ qua và file cũ trong DB được giữ nguyên vẹn

        // 7. NGHIỆP VỤ: Cập nhật FILE VIDEO (.mp4)
        MultipartFile newVideoFile = request.getVideoFile();
        if (newVideoFile != null && !newVideoFile.isEmpty()) {
            // Kiểm tra dung lượng file video mới
            if (newVideoFile.getSize() > MAX_VIDEO_SIZE) {
                throw new FileNotValidException("Video vượt quá dung lượng tối đa cho phép (15MB).");
            }

            // Đẩy video mới lên Cloudinary
            String newVideoUrl = cloudinaryService.uploadMaterialVideo(newVideoFile);

            // Ghi đè dữ liệu video mới vào thực thể
            material.setVideoUrl(newVideoUrl);
        } // Nếu không truyền videoFile mới, video cũ được giữ lại hoàn toàn

        material.setFileSize(newMaterialFile != null && newVideoFile != null ? newMaterialFile.getSize() + newVideoFile.getSize() : 0);


        // 8. Cập nhật thời gian chỉnh sửa mới nhất
        material.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(learningMaterialRepository.save(material));
    }

    // Lấy danh sách tất cả tài liệu học tập (is active)
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    public List<LearningMaterialResponse> getAllActiveMaterial(){
        List<LearningMaterial> resultGet = learningMaterialRepository.getAllByIsActiveTrue();
        List<LearningMaterialResponse> responseList = new ArrayList<>();
        resultGet.forEach(material -> responseList.add(mapToResponse(material)));
        return responseList;
    }


    // Lấy danh sách tài liệu học tập cụ thể theo khóa học cụ thể
    @Override
    public LearningMaterialResponse getMaterialByCourseIdAndCourseLecture(UserPrinciple userPrinciple, Long materialId, Long courseId) {
        // 1. Kiểm tra khóa học có tồn tại không
        Courses course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Không tìm thấy khóa học, vui lòng kiểm tra lại"));

        // 2. Kiểm tra tài liệu học tập có tồn tại trong khóa học này không
        LearningMaterial material = learningMaterialRepository.findByIdAndCourse_IdAndIsActiveIsTrue(materialId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài liệu học tập không tồn tại hoặc không thuộc khóa học này."));

        // 3. XỬ LÝ PHÂN QUYỀN ĐỘNG THEO VAI TRÒ (ROLE) CỦA USER HIỆN TẠI
        String currentRole = userPrinciple.getUser().getRole().getRoleName(); // Giả định bạn có hàm lấy tên Role (ROLE_STUDENT, ROLE_LECTURER, ROLE_ADMIN)
        Long currentUserId = userPrinciple.getUserId();

        // Rẽ nhánh: Nếu là Sinh viên (STUDENT), bắt buộc phải kiểm tra xem đã tham gia (Enrollment) khóa học chưa
        if ("ROLE_STUDENT".equals(currentRole)) {
            boolean isEnrolled = enrollmentRepository.existsByStudents_IdAndCourse_IdAndActiveIsTrue(currentUserId, courseId);
            if (!isEnrolled) {
                throw new CustomAccessDeniedException("Thao tác bị từ chối! Bạn không tham gia khóa học này nên không thể xem tài liệu.");
            }
        }

        // 4. Ánh xạ dữ liệu sang DTO và phản hồi về cho Client
        return mapToResponse(material);
    }

    @Override
    @PreAuthorize("isAuthenticated()") // Ép buộc tất cả các đối tượng gọi vào đều phải qua bộ lọc Login
    public List<LearningMaterialResponse> getMaterialsByCourseWithRoleCheck(UserPrinciple userPrinciple, Long courseId) {

        // 1. Kiểm tra sự tồn tại của Khóa học
        Courses course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Không tìm thấy khóa học, vui lòng kiểm tra lại"));

        // 2. XỬ LÝ PHÂN QUYỀN ĐỘNG THEO VAI TRÒ (ROLE) TRONG HỆ THỐNG
        String currentRole = userPrinciple.getUser().getRole().getRoleName(); // Lấy tên quyền (ROLE_STUDENT, ROLE_LECTURER, ROLE_ADMIN)
        Long currentUserId = userPrinciple.getUserId();

        // Rẽ nhánh: Nếu người dùng đăng nhập hiện tại là Sinh viên (STUDENT)
        if ("ROLE_STUDENT".equals(currentRole)) {
            // Kiểm tra xem sinh viên có đăng ký (Enrollment) khóa học này hay không
            boolean isEnrolled = enrollmentRepository.existsByStudents_IdAndCourse_IdAndActiveIsTrue(currentUserId, courseId);
            if (!isEnrolled) {
                throw new CustomAccessDeniedException("Thao tác bị từ chối! Bạn không tham gia khóa học này nên không thể xem tài liệu.");
            }
        }

        // Rẽ nhánh: Nếu là LECTURER hoặc ADMIN thì không bị chặn (Được xem danh sách công khai)

        // 3. Lấy danh sách tài liệu từ Repository
        List<LearningMaterial> materials = learningMaterialRepository.findByCourse_IdAndIsActiveTrueOrderByCreatedAtDesc(courseId);

        // 4. Áp dụng Java Stream API chuyển đổi mảng Thực thể sang cấu trúc DTO trả về cho Client
        return materials.stream().map(this::mapToResponse).collect(Collectors.toList()); // Dùng hàm mapToResponse đã ánh xạ đầy đủ trường (bao gồm cả fileName)

    }


    private String generateMaterialCode() {
        String code;
        do {
            code = "MAT-" + System.currentTimeMillis();
        } while (learningMaterialRepository.existsByMaterialCode(code));
        return code;
    }

    private LearningMaterialResponse mapToResponse(LearningMaterial m) {
        return LearningMaterialResponse.builder()
                .id(m.getId())
                .materialCode(m.getMaterialCode())
                .title(m.getTitle())
                .description(m.getDescription())
                .fileUrl(m.getFileUrl())
                .fileName(m.getFileName())
                .videoUrl(m.getVideoUrl())
                .youtubeUrl(m.getYoutubeUrl())
                .readingContent(m.getReadingContent())
                .isActive(m.getIsActive())
                .uploadedByName(m.getUploadedBy().getFullName())
                .uploadedByUsername(m.getUploadedBy().getUsername())
                .courseId(m.getCourse().getId())
                .courseTitle(m.getCourse().getCourseName()) // Đã chuẩn hóa khớp với tên trường trong Entity Courses
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private LearningMaterial getMaterialAndCheckOwner(String lecturerUsername, Long materialId) {
        LearningMaterial material = learningMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu giảng dạy có ID: " + materialId));
        if (!material.getUploadedBy().getUsername().equals(lecturerUsername)) {
            throw new CustomAccessDeniedException("Thao tác bị từ chối! Bạn không thể can thiệp vào tài liệu của giảng viên khác.");
        }
        if (!material.getIsActive()) {
            throw new BadRequestException("Tài liệu giảng dạy này đã bị gỡ bỏ hoặc ngưng hoạt động từ trước.");
        }
        return material;
    }
}
