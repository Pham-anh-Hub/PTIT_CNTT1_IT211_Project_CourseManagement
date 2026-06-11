package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.ForgotPasswordRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.ResetPasswordRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.VerifyOtpRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ResetPasswordResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.VerifyOtpResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.PasswordResetService;

@RestController
@RequestMapping("/api/v1/auth/forgot-password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // Bước 1: Nhận Email -> Xác thực tài khoản -> Gửi mã OTP 6 số qua Gmail
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.verifyEmail_sendOTP(request.getEmail());
        return ResponseEntity.ok("Mã OTP xác thực đã được gửi tới hộp thư Email của bạn. Vui lòng kiểm tra!");
    }

    // Bước 2: Nhận Email + OTP -> Kiểm tra so khớp -> Trả về mã JWT Reset Token tạm thời (VerifyOtpResponse)
    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse response = passwordResetService.verifyOTP(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(response);
    }

    // Bước 3: Nhận Reset Token + Mật khẩu mới -> Cập nhật mật khẩu mới -> Blacklist Token
    @PostMapping("/reset")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = passwordResetService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}