package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ExceptionResponse;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> MethodArgumentNotValidHandle(MethodArgumentNotValidException ex, HttpServletRequest request ){
        StringBuilder errorMessage =  new StringBuilder();
        ex.getFieldErrors().forEach(fe -> {
            errorMessage.append(fe.getDefaultMessage()).append(", ");
        });
        ExceptionResponse response = ExceptionResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage.toString())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataDuplicateException.class)
    public ResponseEntity<ExceptionResponse> DataDuplicateExceptionHandle(DataDuplicateException ex, HttpServletRequest request ){
        ExceptionResponse response = ExceptionResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }
}
