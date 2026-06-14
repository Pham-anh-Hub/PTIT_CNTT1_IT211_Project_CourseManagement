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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentResponseDTO {
        private Long id;
        private String userCode;
        private String username;
        private String fullName;
        private String phone;
        private String email;
        private boolean active;
        private String roleCode; // Chỉ trả về chuỗi tên mã (Vd: "STUDENT", "ADMIN")
        private String roleName; // Tên hiển thị (Vd: "Student", "Admin")
    }
}
