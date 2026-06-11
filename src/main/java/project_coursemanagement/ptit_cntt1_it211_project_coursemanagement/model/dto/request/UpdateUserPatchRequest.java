package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserPatchRequest {

    private String fullName;

    private String phone;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String roleCode;

    private Boolean isActive;
}