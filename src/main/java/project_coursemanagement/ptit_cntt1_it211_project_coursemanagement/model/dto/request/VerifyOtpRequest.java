package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VerifyOtpRequest {
    private String email;
    private String otp;
}
