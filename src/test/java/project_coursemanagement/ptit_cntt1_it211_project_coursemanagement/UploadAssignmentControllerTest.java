package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.student.UploadAssignmentController;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.DataDuplicateException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.FileNotValidException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.SubmissionRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.SubmissionResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.SubmissionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadAssignmentControllerTest {

    @Mock
    private SubmissionService submissionService;

    @InjectMocks
    private UploadAssignmentController controller;

    // TC-01
    @Test
    @DisplayName("TC-01: Nộp bài thành công trả về 200 OK")
    void submitAssignment_success() {
        SubmissionRequest request = new SubmissionRequest();
        request.setAssignmentId(1L);

        MultipartFile mockFile = mock(MultipartFile.class);

        UserPrinciple userPrinciple = mock(UserPrinciple.class);
        when(userPrinciple.getUsername()).thenReturn("student01");

        SubmissionResponse response = SubmissionResponse.builder()
                .id(1L)
                .reportUrl("https://cloudinary.com/file.pdf")
                .status("SUBMITTED")
                .build();

        when(submissionService.submitAssignment(
                eq("student01"), any(SubmissionRequest.class), any(MultipartFile.class)
        )).thenReturn(response);

        ResponseEntity<SubmissionResponse> result =
                controller.submitAssignment(userPrinciple, request, mockFile);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        assertEquals("https://cloudinary.com/file.pdf", result.getBody().getReportUrl());
    }

    // TC-02
    @Test
    @DisplayName("TC-02: File rỗng → Service ném FileNotValidException")
    void submitAssignment_emptyFile_throwsException() {
        SubmissionRequest request = new SubmissionRequest();
        request.setAssignmentId(1L);

        MultipartFile emptyFile = mock(MultipartFile.class);
        UserPrinciple userPrinciple = mock(UserPrinciple.class);
        when(userPrinciple.getUsername()).thenReturn("student01");

        when(submissionService.submitAssignment(anyString(), any(), any()))
                .thenThrow(new FileNotValidException("File không hợp lệ"));

        assertThrows(FileNotValidException.class, () ->
                controller.submitAssignment(userPrinciple, request, emptyFile)
        );
        verify(submissionService, times(1))
                .submitAssignment(anyString(), any(), any());
    }

    // TC-03
    @Test
    @DisplayName("TC-03: Nộp bài trùng → Service ném DataDuplicateException")
    void submitAssignment_duplicate_throwsException() {
        SubmissionRequest request = new SubmissionRequest();
        request.setAssignmentId(1L);

        MultipartFile mockFile = mock(MultipartFile.class);
        UserPrinciple userPrinciple = mock(UserPrinciple.class);
        when(userPrinciple.getUsername()).thenReturn("student01");

        when(submissionService.submitAssignment(anyString(), any(), any()))
                .thenThrow(new DataDuplicateException("Bài tập đã được nộp trước đó"));

        assertThrows(DataDuplicateException.class, () ->
                controller.submitAssignment(userPrinciple, request, mockFile)
        );
    }
}