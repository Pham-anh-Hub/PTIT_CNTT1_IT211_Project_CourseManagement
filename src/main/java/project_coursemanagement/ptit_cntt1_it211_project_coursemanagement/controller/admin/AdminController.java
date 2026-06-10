package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.admin;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponseDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // Quản lý Người dùng (CRUD, Tìm kiếm - theo tên đăng nhập/sdt, Phân trang)
    @GetMapping("/users")
    public ResponseEntity<?> getUsersManagement(
            @RequestParam(defaultValue = "", required = false) String username,
            @RequestParam(defaultValue = "", required = false) String phone,
            @PageableDefault(
                page = 0,
                size = 5,
                sort = "id",
                direction = Sort.Direction.ASC
        )Pageable pageable
            ){

        List<UserResponseDTO> userResponses = adminService.getUserByUsernameOrPhone(username, phone, pageable);
        return ResponseEntity.ok(userResponses);
    }
}
