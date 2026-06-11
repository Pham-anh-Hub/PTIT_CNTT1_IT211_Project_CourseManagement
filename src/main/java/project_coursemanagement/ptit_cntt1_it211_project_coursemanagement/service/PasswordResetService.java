package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;

import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.ResetPasswordRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ResetPasswordResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.VerifyOtpResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.PasswordResetToken;

public interface PasswordResetService {
    // verify email && sendOTP
    void verifyEmail_sendOTP(String toUserEmail);

    // verify OTP
    VerifyOtpResponse verifyOTP(String email, String otp);


    // reset Password
    ResetPasswordResponse resetPassword(ResetPasswordRequest passwordRequest);

}
