package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import lombok.*;

import java.security.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExceptionResponse {
    private LocalDateTime catchTime;
    private int code;
    private String error;
    private String message;
    private String path;
}
