package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ThrowResponse {
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime catchTime;
    private int code;
    private String error;
    private String message;
    private String path;
}
