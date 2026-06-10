package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.DataDuplicateException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RegisterUserDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.RegistedUserResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Role;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.RoleRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.PublicService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegistedUserResponse createNewStudentAccounts(RegisterUserDTO registerUserDTO) {
        if(usersRepository.existsByEmail(registerUserDTO.getEmail())){
            throw new DataDuplicateException("Email đã tồn tại");
        }
        if (usersRepository.existsByUsername(registerUserDTO.getUsername())){
            throw new DataDuplicateException("Tên đăng nhập đã tồn tại");
        }

        // Tạo tài khoản mới
        Users newStudent = Users.builder()
                .id(null)
                .userCode(null).username(registerUserDTO.getUsername())
                .passwordHash(passwordEncoder.encode(registerUserDTO.getPassword()))
                .fullName(registerUserDTO.getFullName())
                .phone(registerUserDTO.getPhone())
                .email(registerUserDTO.getEmail())
                .role(roleRepository.findByCode(RoleName.STUDENT))
                .isActive(true)
                .build();

        Users userSaved = usersRepository.save(newStudent);
        userSaved.setUserCode("STU" + String.format("%04d", userSaved.getId()));

        usersRepository.save(userSaved);

        return new RegistedUserResponse(userSaved.getUsername(), userSaved.getFullName(), userSaved.getPhone(), userSaved.getEmail(), userSaved.getRole().getCode(), LocalDateTime.now());
    }

}
