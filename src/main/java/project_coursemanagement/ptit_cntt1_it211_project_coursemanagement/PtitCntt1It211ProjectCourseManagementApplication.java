package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Role;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.RoleRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl.EmailService;

@SpringBootApplication
@RequiredArgsConstructor
public class PtitCntt1It211ProjectCourseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(PtitCntt1It211ProjectCourseManagementApplication.class, args);
    }

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role(null, RoleName.ADMIN, "Admin", null));
                roleRepository.save(new Role(null, RoleName.LECTURER, "Lecturer", null));
                roleRepository.save(new Role(null, RoleName.STUDENT, "Student", null));
            }
        };
    }

    private final EmailService emailService;

//    @Bean
//    public CommandLineRunner runner() throws Exception {
//        return args -> { // 2. Bắt buộc phải có cú pháp Lambda "args -> {}" vì CommandLineRunner là một Functional Interface
//            System.out.println("====== [START TEST EMAIL] ======");
//            try {
//                // Thay địa chỉ email của bạn hoặc một email test bất kỳ vào đây để nhận thư thực tế
//                String testEmailTo = "bichle06032004@gmail.com";
//                String testOtp = "123456";
//
//                emailService.sendOtpEmail(testEmailTo, testOtp);
//
//                System.out.println("====== [TEST EMAIL SUCCESS] ====== Thư đã gửi thành công!");
//            } catch (Exception e) {
//                System.err.println("====== [TEST EMAIL FAILED] ====== Lỗi kết nối gửi mail:");
//                e.printStackTrace();
//            }
//        };
//    }

}
