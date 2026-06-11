package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ForgotPasswordRequest {
    private String email;
}
