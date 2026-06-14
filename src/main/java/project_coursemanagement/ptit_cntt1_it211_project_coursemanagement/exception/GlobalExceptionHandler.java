package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 400 Bad Request

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ThrowResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getFieldErrors().forEach(fe ->
                errorMessage.append(fe.getDefaultMessage()).append(", ")
        );
        return build(HttpStatus.BAD_REQUEST, errorMessage.toString(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ThrowResponse> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(AssignmentInvalidException.class)
    public ResponseEntity<ThrowResponse> handleAssignmentInvalid(
            AssignmentInvalidException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(FileNotValidException.class)
    public ResponseEntity<ThrowResponse> handleFileNotValid(
            FileNotValidException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // 401 Unauthorized

    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ThrowResponse> handleTokenInvalid(
            TokenInvalidException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(ValidTokenException.class)
    public ResponseEntity<ThrowResponse> handleValidToken(
            ValidTokenException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    // 403 Forbidden

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<ThrowResponse> handleAccessDenied(
            CustomAccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    // 404 Not Found

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ThrowResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ThrowResponse> handleCourseNotFound(
            CourseNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ThrowResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ThrowResponse> handleEnrollmentNotFound(
            EnrollmentNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ThrowResponse> handleRoleNotFound(
            RoleNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ThrowResponse> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ── 409 Conflict ──────────────────────────────────────────

    @ExceptionHandler(DataDuplicateException.class)
    public ResponseEntity<ThrowResponse> handleDataDuplicate(
            DataDuplicateException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // ── 503 Service Unavailable ───────────────────────────────

    // ── 500 Internal Server Error — bắt tất cả còn lại ───────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ThrowResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Lỗi hệ thống, vui lòng thử lại sau", request);
    }

    // ── PRIVATE HELPER — build response dùng chung ────────────

    private ResponseEntity<ThrowResponse> build(HttpStatus status,
                                                String message,
                                                HttpServletRequest request) {
        ThrowResponse response = ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
