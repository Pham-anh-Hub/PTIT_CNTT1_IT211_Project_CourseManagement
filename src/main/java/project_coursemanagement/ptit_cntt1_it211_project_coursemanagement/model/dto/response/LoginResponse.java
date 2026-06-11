package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {
    // Trả về mã HTTP 200 OK cùng đối tượng JSON chứa AccessToken (hạn ngắn) và RefreshToken (hạn dài).
    private UserResponse user;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime loginTime;
    private String accessToken;
    private String refreshToken;

}
