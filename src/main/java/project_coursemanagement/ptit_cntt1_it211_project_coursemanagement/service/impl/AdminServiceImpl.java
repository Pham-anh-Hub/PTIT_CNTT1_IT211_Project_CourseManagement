package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponseDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.AdminService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UsersRepository usersRepository;

    @Override
    public List<UserResponseDTO> getUserByUsernameOrPhone(String username, String phone, Pageable pageable) {
        // 1. Gọi đến repository để lấy Page thực thể Entity từ Database như cũ
        Page<Users> usersPage = usersRepository.getUsersByUsernameOrPhone(username, phone, pageable);

        // 2. Sử dụng hàm map để chuyển đổi danh sách Entity sang danh sách DTO sạch
        return usersPage.map(user -> UserResponseDTO.builder()
                .id(user.getId())
                .userCode(user.getUserCode())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .active(user.isActive())
                // Lấy thông tin phẳng từ bảng Role, ngắt liên kết hai chiều gây lặp
                .roleCode(user.getRole() != null ? user.getRole().getCode().toString() : null)
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .build()
        ).toList();
    }
}
