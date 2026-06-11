package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    public void sendOtpEmail(String toEmail, String otp){
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailSender, "Hệ thống Quản lý khóa học và chấm điểm đồ án");
            helper.setTo(toEmail);
            helper.setSubject("MÃ OTP ĐẶT LẠI MẬT KHẨU");
            String content = "Chào " + toEmail + " ,\n\n" +
                    "Mã OTP để xác minh đặt lại mật khẩu của bạn là: " + otp + "\n" +
                    "Mã này có hiệu lực trong vòng 15 phút.\n" +
                    "Vì lý do bảo mật, tuyệt đối không chia sẻ mã này cho bất kỳ ai.\n\n" +
                    "Trân trọng,\n" +
                    "Ban quản trị hệ thống.";

            helper.setText(content);

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            // Ném lỗi hệ thống nếu quá trình dựng cấu trúc Mail thất bại
            throw new RuntimeException("Lỗi xảy ra trong quá trình dựng cấu trúc gửi Email: " + e.getMessage());
        }
    }
}
