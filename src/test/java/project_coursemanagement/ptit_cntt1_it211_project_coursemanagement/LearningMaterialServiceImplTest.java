package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.CustomAccessDeniedException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.FileNotValidException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
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
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.CloudinaryService;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl.LearningMaterialServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningMaterialServiceImplTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private CloudinaryService cloudinaryService;
    @Mock private LearningMaterialRepository learningMaterialRepository;
    @Mock private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private LearningMaterialServiceImpl learningMaterialService;

    @BeforeEach
    void setUp() {
        // Inject @Value field thủ công vì @Value không hoạt động với @ExtendWith
        ReflectionTestUtils.setField(learningMaterialService, 
            "maxFileSize", 15728640L); // 15MB
    }

    // ── Dữ liệu dùng chung ───────────────────────────────────

    private Users mockLecturer() {
        Users lecturer = new Users();
        lecturer.setId(1L);
        lecturer.setUsername("lecturer01");
        lecturer.setEmail("lecturer@ptit.edu.vn");
        lecturer.setFullName("Nguyễn Văn A");
        return lecturer;
    }

    private Courses mockCourse() {
        Courses course = new Courses();
        course.setId(1L);
        course.setCourseName("Lập trình Java");
        return course;
    }

    private LearningMaterial mockMaterial(Users lecturer, Courses course) {
        LearningMaterial m = new LearningMaterial();
        m.setId(1L);
        m.setMaterialCode("MAT-001");
        m.setTitle("Bài giảng tuần 1");
        m.setDescription("Mô tả");
        m.setFileUrl("https://cloudinary.com/file.pdf");
        m.setFileName("file.pdf");
        m.setIsActive(true);
        m.setUploadedBy(lecturer);
        m.setCourse(course);
        m.setCreatedAt(LocalDateTime.now());
        m.setUpdatedAt(LocalDateTime.now());
        return m;
    }

    // ── TC-SV-01: Tạo tài liệu với file thành công ───────────
    @Test
    @DisplayName("TC-SV-01: Tạo tài liệu kèm file thành công")
    void createUploadMaterial_withFile_success() {
        // Arrange
        Users lecturer = mockLecturer();
        Courses course = mockCourse();

        LearningMaterialRequest request = new LearningMaterialRequest();
        request.setCourseId(1L);
        request.setTitle("Bài giảng tuần 1");
        request.setDescription("Mô tả bài giảng");

        MultipartFile materialFile = mock(MultipartFile.class);
        when(materialFile.isEmpty()).thenReturn(false);
        when(materialFile.getSize()).thenReturn(1024L);
        when(materialFile.getOriginalFilename()).thenReturn("slide.pdf");

        when(usersRepository.findByUsernameAndEmail("lecturer01", "lecturer@ptit.edu.vn"))
            .thenReturn(Optional.of(lecturer));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(cloudinaryService.uploadMaterialFile(materialFile))
            .thenReturn("https://cloudinary.com/slide.pdf");
        when(learningMaterialRepository.existsByMaterialCode(anyString()))
            .thenReturn(false);
        when(learningMaterialRepository.save(any(LearningMaterial.class)))
            .thenAnswer(inv -> {
                LearningMaterial m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });

        // Act
        LearningMaterialResponse response = learningMaterialService.createUploadMaterial(
            "lecturer01", "lecturer@ptit.edu.vn", request, materialFile, null
        );

        // Assert
        assertNotNull(response);
        assertEquals("Bài giảng tuần 1", response.getTitle());
        assertEquals("https://cloudinary.com/slide.pdf", response.getFileUrl());
        assertEquals("slide.pdf", response.getFileName());
        verify(cloudinaryService, times(1)).uploadMaterialFile(materialFile);
        verify(learningMaterialRepository, times(1)).save(any());
    }

    // ── TC-SV-02: Giảng viên không tồn tại → UserNotFoundException ──
    @Test
    @DisplayName("TC-SV-02: Giảng viên không tồn tại ném UserNotFoundException")
    void createUploadMaterial_lecturerNotFound_throwsException() {
        // Arrange
        when(usersRepository.findByUsernameAndEmail("wrong", "wrong@email.com"))
            .thenReturn(Optional.empty());

        LearningMaterialRequest request = new LearningMaterialRequest();
        request.setCourseId(1L);
        request.setTitle("Test");

        // Act + Assert
        assertThrows(UserNotFoundException.class, () ->
            learningMaterialService.createUploadMaterial(
                "wrong", "wrong@email.com", request, null, null
            )
        );
        verify(courseRepository, never()).findById(any());
    }

    // ── TC-SV-03: File vượt quá 15MB → FileNotValidException ─
    @Test
    @DisplayName("TC-SV-03: File vượt 15MB ném FileNotValidException")
    void createUploadMaterial_fileTooLarge_throwsException() {
        // Arrange
        Users lecturer = mockLecturer();
        Courses course = mockCourse();

        when(usersRepository.findByUsernameAndEmail("lecturer01", "lecturer@ptit.edu.vn"))
            .thenReturn(Optional.of(lecturer));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        MultipartFile bigFile = mock(MultipartFile.class);
        when(bigFile.isEmpty()).thenReturn(false);
        when(bigFile.getSize()).thenReturn(20 * 1024 * 1024L); // 20MB

        LearningMaterialRequest request = new LearningMaterialRequest();
        request.setCourseId(1L);
        request.setTitle("Test");

        // Act + Assert
        assertThrows(FileNotValidException.class, () ->
            learningMaterialService.createUploadMaterial(
                "lecturer01", "lecturer@ptit.edu.vn", request, bigFile, null
            )
        );
        verify(cloudinaryService, never()).uploadMaterialFile(any());
    }

    // ── TC-SV-04: YouTube URL sai định dạng → RuntimeException ──
    @Test
    @DisplayName("TC-SV-04: URL YouTube sai định dạng ném RuntimeException")
    void createUploadMaterial_invalidYoutubeUrl_throwsException() {
        // Arrange
        Users lecturer = mockLecturer();
        Courses course = mockCourse();

        when(usersRepository.findByUsernameAndEmail("lecturer01", "lecturer@ptit.edu.vn"))
            .thenReturn(Optional.of(lecturer));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        LearningMaterialRequest request = new LearningMaterialRequest();
        request.setCourseId(1L);
        request.setTitle("Test");
        request.setYoutubeUrl("https://facebook.com/video/123"); // ← sai

        // Act + Assert
        assertThrows(RuntimeException.class, () ->
            learningMaterialService.createUploadMaterial(
                "lecturer01", "lecturer@ptit.edu.vn", request, null, null
            )
        );
    }

    // ── TC-SV-05: Lecturer khác sửa tài liệu → CustomAccessDeniedException ──
    @Test
    @DisplayName("TC-SV-05: Lecturer khác sửa tài liệu ném CustomAccessDeniedException")
    void updateMaterial_notOwner_throwsAccessDenied() {
        // Arrange
        Users owner = mockLecturer(); // username = lecturer01
        Courses course = mockCourse();
        LearningMaterial material = mockMaterial(owner, course);

        when(learningMaterialRepository.findById(1L))
            .thenReturn(Optional.of(material));

        LearningMaterialUpdateRequest updateRequest = new LearningMaterialUpdateRequest();
        updateRequest.setTitle("Tiêu đề mới");

        // Act + Assert — lecturer02 cố sửa tài liệu của lecturer01
        assertThrows(CustomAccessDeniedException.class, () ->
            learningMaterialService.updateMaterial("lecturer02", 1L, updateRequest)
        );
        verify(learningMaterialRepository, never()).save(any());
    }
}