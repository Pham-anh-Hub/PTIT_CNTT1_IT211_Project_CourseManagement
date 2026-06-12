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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ThrowResponse> MethodArgumentNotValidHandle(MethodArgumentNotValidException ex, HttpServletRequest request ){
        StringBuilder errorMessage =  new StringBuilder();
        ex.getFieldErrors().forEach(fe -> {
            errorMessage.append(fe.getDefaultMessage()).append(", ");
        });
        ThrowResponse response = ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage.toString())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataDuplicateException.class)
    public ResponseEntity<ThrowResponse> DataDuplicateExceptionHandle(DataDuplicateException ex, HttpServletRequest request ){
        ThrowResponse response = ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ThrowResponse> NotFoundUserHandle(UserNotFoundException ex, HttpServletRequest request){
        ThrowResponse response = ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ThrowResponse> NoResourceFoundHandle(NoResourceFoundException ex, HttpServletRequest request){
        ThrowResponse response = ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ThrowResponse> NotFoundUserHandle(CourseNotFoundException ex, HttpServletRequest request){
        ThrowResponse response = ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }


    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ThrowResponse> TokenInvalidHandle(TokenInvalidException ex, HttpServletRequest request){
        ThrowResponse response = ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

    }

    @ExceptionHandler(FileNotValidException.class)
    public ResponseEntity<ThrowResponse> handleFileNotValid(
            FileNotValidException ex,
            HttpServletRequest request) {

        ThrowResponse response = ThrowResponse.builder()
                .catchTime(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

}
