package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RegisterUserRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.UpdateUserPatchRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.UpdateUserRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class UserController {
    private final UserService adminService;

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

        List<UserResponse> userResponses = adminService.getUserByUsernameOrPhone(username, phone, pageable);
        return ResponseEntity.ok(userResponses);
    }

    // CRUD người dùng
    // C
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createNewUser(@Valid @RequestBody RegisterUserRequest userRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createNewUser(userRequest));
    }

    // R
    @GetMapping("/users/{searchKey}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String searchKey){
        return ResponseEntity.ok(adminService.getUsersByIdOrUserCode(searchKey));
    }


    // U
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateInfoUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateUser(id, request));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id, @Valid @RequestBody UpdateUserPatchRequest request) {
        return ResponseEntity.ok(adminService.patchUser(id, request));
    }

    // D
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ThrowResponse> deleteUser(
            @PathVariable Long id,
            HttpServletRequest request
    ){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(adminService.deleteUserById(id, request));
    }
}


