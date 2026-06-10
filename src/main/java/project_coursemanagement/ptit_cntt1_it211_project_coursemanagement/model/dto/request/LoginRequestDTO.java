package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank(message = "Không để trống tên đăng nhập")
    private String username;
    @NotBlank(message = "Không để trống mật khẩu")
    private String password;
}
