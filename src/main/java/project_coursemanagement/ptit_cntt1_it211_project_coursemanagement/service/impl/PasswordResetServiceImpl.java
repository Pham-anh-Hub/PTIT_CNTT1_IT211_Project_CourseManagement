package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.TokenInvalidException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.ResetPasswordRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ResetPasswordResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.VerifyOtpResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.PasswordResetToken;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.ResetPasswordTokenRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.jwt.JwtProvider;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.PasswordResetService;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;


    @Override
    public void verifyEmail_sendOTP(String toUserEmail) {
        // nhận vào request chứa email người nhận
        // kiểm tra email xem người dùng có tồn tại trong hệ thống hay không
        Users receiver = usersRepository.findByEmail(toUserEmail).orElseThrow(() -> new UserNotFoundException("Người dùng không tồn tại, vui lòng kiểm tra lại"));
        // (tối ưu) sẽ kiểm tra trong DB xem có mã OTP mới nhất mà người dùng này đã xin mà chưa sử dụng cũng chưa hết hạn k
        resetPasswordTokenRepository.findTopByUserEmailAndUsedFalseAndExpiresAtAfter(toUserEmail, LocalDateTime.now())
                .ifPresent(oldToken -> {
                    oldToken.setUsed(true);
                    resetPasswordTokenRepository.save(oldToken);
                });
        // 3. Sử dụng OtpService để tạo mã 6 số ngẫu nhiên
        String rawOtp = otpService.generateOtp();

        // 4. Mã hóa OTP trước khi lưu vào DB để đảm bảo an toàn tuyệt đối
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(receiver);
        resetToken.setOtpHashed(passwordEncoder.encode(rawOtp));
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // Hiệu lực 15 phút
        resetToken.setUsed(false);

        resetPasswordTokenRepository.save(resetToken);

        // 5. Gửi mã OTP gốc (chưa mã hóa) về hộp thư của người dùng
        emailService.sendOtpEmail(toUserEmail, rawOtp);
    }

    // Xác thực OTP trả về JWT - resetToken tạm thời
    @Override
    public VerifyOtpResponse verifyOTP(String email, String otp) {
        // 1. Lấy mã OTP hợp lệ duy nhất của người dùng này từ DB lên
        PasswordResetToken resetToken = resetPasswordTokenRepository
                .findTopByUserEmailAndUsedFalseAndExpiresAtAfter(email, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn sử dụng!"));

        // 2. So khớp xem 6 số người dùng nhập có trùng với trong DB không
        if (!passwordEncoder.matches(otp, resetToken.getOtpHashed())) {
            throw new RuntimeException("Mã OTP bạn nhập không chính xác!");
        }
        // người dùng theo email
        Users users = usersRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng " + email + ", vui lòng kiểm tra lại"));

        // 3. Đúng mã -> Đánh dấu vô hiệu hóa OTP này lập tức để không bị dùng lại
        resetToken.setUsed(true);
        resetPasswordTokenRepository.save(resetToken);

        // 4. Sinh JWT Reset Token (Lấy thẳng đối tượng Users từ liên kết JPA)
        Users user = resetToken.getUser();
        String resetTokenJwt = jwtProvider.generateResetToken(user.getUsername());

        // 5. Trả về đối tượng Response chứa Token cho Frontend
        return VerifyOtpResponse.builder()
                .resetToken(resetTokenJwt)
                .message("Xác thực OTP thành công, vui lòng tiến hành đặt lại mật khẩu mới.")
                .build();
    }


    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // 1. Kiểm tra xem 2 ô mật khẩu mới nhập có trùng nhau không
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không trùng khớp!");
        }

        // 2. Kiểm tra xem chiếc chìa khóa gửi lên có đúng định dạng ResetToken đặc biệt không
        if (!jwtProvider.isResetToken(resetPasswordRequest.getResetToken())) {
            throw new RuntimeException("Mã Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn!");
        }

        // 3. Trích xuất username an toàn từ ResetToken để tìm ra thực thể Người dùng
        String username = jwtProvider.getUsernameFromResetToken(resetPasswordRequest.getResetToken()); // Hàm bóc tách subject bạn đã viết
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản người dùng không còn tồn tại trên hệ thống!"));

        // 4. Ràng buộc bảo mật: Không cho phép đặt mật khẩu mới trùng y hệt mật khẩu hiện tại
        if (passwordEncoder.matches(resetPasswordRequest.getNewPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu đang sử dụng!");
        }

        // 5. Tiến hành mã hóa mật khẩu mới và cập nhật vào Database
        user.setPasswordHash(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        usersRepository.save(user);

        // 6. Đưa cái resetToken này vào danh sách đen (Blacklist) để nó biến thành vô hiệu lực ngay lập tức
        // Điều này ngăn chặn hacker thu thập lại token cũ để đổi mật khẩu lần 2
        // (Sử dụng chính thực thể TokenBlacklist bạn đã hoàn thiện ở tính năng Logout)

        // chỉnh sửa lại để xử lý với redis
        // đưa  access token vào redis blacklist
        try{
            String resetToken = resetPasswordRequest.getResetToken();
            // lấy thời gian hết hạn của token, tính thời gian sống còn lại
            long expiredTime = jwtProvider.getExpirationFromToken(resetToken).getTime();
            Long restTime = System.currentTimeMillis() - expiredTime;

            if (restTime > 0){
                // accessToken chưa hết hạn --> đưa vào redis
                redisTemplate.opsForValue().set(resetToken, "reset_used", restTime, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            throw new TokenInvalidException("Không thể xử lý token với blacklist");
        }

        return ResetPasswordResponse.builder().email(user.getEmail()).username(user.getUsername()).message("Cập nhật mật khẩu mới thành công").resetTime(LocalDateTime.now()).build();
    }
}
