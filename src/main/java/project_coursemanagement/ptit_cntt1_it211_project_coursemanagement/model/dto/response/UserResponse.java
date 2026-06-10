package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private String username;
    private String email;
    private String phone;
}
