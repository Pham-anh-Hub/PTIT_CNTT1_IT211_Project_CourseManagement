package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterUserResponse {
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private RoleName role;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
