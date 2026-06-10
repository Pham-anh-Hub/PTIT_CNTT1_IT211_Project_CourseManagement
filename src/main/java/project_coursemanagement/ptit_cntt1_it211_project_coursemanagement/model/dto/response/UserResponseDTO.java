package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
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