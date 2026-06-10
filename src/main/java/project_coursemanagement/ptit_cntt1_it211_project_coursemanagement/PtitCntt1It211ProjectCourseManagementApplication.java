package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Role;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.RoleRepository;

@SpringBootApplication
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

}
