package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VerifyOtpResponse {
    private String resetToken;
    private String message;
}