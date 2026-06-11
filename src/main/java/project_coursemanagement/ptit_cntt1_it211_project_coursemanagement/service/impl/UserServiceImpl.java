package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.DataDuplicateException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RegisterUserRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.UpdateUserPatchRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.UpdateUserRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Role;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.RoleRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getUserByUsernameOrPhone(String username, String phone, Pageable pageable) {
        // 1. Gọi đến repository để lấy Page thực thể Entity từ Database như cũ
        Page<Users> usersPage = usersRepository.getUsersByUsernameAndPhone(username, phone, pageable);

        // 2. Sử dụng hàm map để chuyển đổi danh sách Entity sang danh sách DTO sạch
        return usersPage.map(this::mapToResponse).toList();
    }

    @Override
    public UserResponse createNewUser(RegisterUserRequest userRequest) {
        if (usersRepository.existsByUsername(userRequest.getUsername())){
            throw new DataDuplicateException("Tên đăng nhập đã tồn tại");
        }
        if(usersRepository.existsByEmail(userRequest.getEmail())){
            throw new DataDuplicateException("Email đã tồn tại");
        }
        if(usersRepository.existsByPhone(userRequest.getPhone())){
            throw new DataDuplicateException("Số điện thoại đã tồn tại");
        }

        Users newUser = Users.builder().id(null)
                .userCode(null)
                .username(userRequest.getUsername())
                .passwordHash(passwordEncoder.encode(userRequest.getPassword()))
                .fullName(userRequest.getFullName())
                .phone(userRequest.getPhone())
                .email(userRequest.getEmail())
                .role(roleRepository.findByRoleName(userRequest.getRole()))
                .isActive(true)
                .build();
        Users savedUser = usersRepository.save(newUser);
        savedUser.setUserCode(savedUser.getRole().getRoleName().toUpperCase() + String.format("-%03d-%03d",savedUser.getId(), usersRepository.getUsersByRole_RoleName(savedUser.getRole().getRoleName())));
        usersRepository.save(savedUser);

        return mapToResponse(savedUser);
    }

    //


    @Override
    public UserResponse getUsersByIdOrUserCode(String key) {
        Optional<Users> userOptional;

        // 1. Kiểm tra xem chuỗi 'key' truyền vào có phải là số hay không
        if (key.matches("\\d+")) {
            // Nếu là số, ép sang kiểu Long và tìm theo ID
            Long id = Long.parseLong(key);
            userOptional = usersRepository.findById(id);
        } else {
            // Nếu chứa chữ hay ký tụ đặc biệt, tìm kiếm theo userCode tương đối
            userOptional = usersRepository.findByUserCodeContaining(key);
        }

        // 2. Trả về kết quả hoặc ném lỗi nếu không tìm thấy
        Users target = userOptional.orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng với từ khóa: " + key));

        return mapToResponse(target);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Không tìm thấy user với id = " + id));


        if (!user.getEmail().equals(request.getEmail()) && usersRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DataDuplicateException("Email đã tồn tại");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }

        if (request.getRoleName() != null) {
            Role role = roleRepository.findByRoleName(request.getRoleName());
            user.setRole(role);
        }
        Users saved = usersRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    public UserResponse patchUser(Long id, UpdateUserPatchRequest request) {
        Users target = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Không tìm thấy user với id " + id));

        // fullName
        if (request.getFullName() != null) {
            target.setFullName(request.getFullName());
        }

        // phone
        if (request.getPhone() != null) {
            target.setPhone(request.getPhone());
        }

        // email
        if (request.getEmail() != null) {
            if (usersRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new DataDuplicateException("Email đã tồn tại");
            }
            target.setEmail(request.getEmail());
        }
        // role
        if (request.getRoleCode() != null) {
            RoleName roleName;
            try {
                roleName = RoleName.valueOf(request.getRoleCode().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Vai trò không hợp lệ");
            }
            Role role = roleRepository.findByCode(roleName);
            if (role == null) {
                throw new UserNotFoundException("Không tìm thấy role");
            }
            target.setRole(role);
        }

        // status
        if (request.getIsActive() != null) {
            target.setActive(request.getIsActive());
        }
        Users saved = usersRepository.save(target);
        return mapToResponse(saved);
    }

    @Override
    public ThrowResponse deleteUserById(Long id, HttpServletRequest request){
        Users targetDelete = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng có id " + id));
        targetDelete.setActive(false);
        usersRepository.save(targetDelete);
        return ThrowResponse.builder()
                .catchTime(LocalDateTime.now()).code(401).message("Xóa người dùng thành công").path(request.getContextPath()).build();
    }

    private UserResponse mapToResponse(Users target) {
        return UserResponse.builder()
                .userCode(target.getUserCode())
                .username(target.getUsername())
                .fullName(target.getFullName())
                .phone(target.getPhone())
                .email(target.getEmail())
                .role(target.getRole().getCode().toString())
                .isActive(target.isActive())
                .createdAt(target.getCreatedAt())
                .updatedAt(target.getUpdatedAt())
                .build();
    }
}
