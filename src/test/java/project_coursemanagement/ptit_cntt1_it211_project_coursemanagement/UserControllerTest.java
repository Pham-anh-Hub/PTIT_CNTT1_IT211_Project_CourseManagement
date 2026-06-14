package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.admin.UserController;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RegisterUserRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.RoleRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.jwt.JwtAuthenticationFilter;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.jwt.JwtProvider;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService adminService;

    // Nếu ApplicationContext bị fail vì initRoles trong main app
    @MockitoBean
    private RoleRepository roleRepository;

    private UserResponse mockUserResponse() {
        return UserResponse.builder()
                .userCode("LECTURER-001-001")
                .username("lecturer01")
                .fullName("Nguyễn Văn A")
                .phone("0987654321")
                .email("lecturer01@gmail.com")
                .role("LECTURER")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private RegisterUserRequest mockRegisterRequest() {
        return RegisterUserRequest.builder()
                .username("student01")
                .password("123456")
                .fullName("Nguyễn Văn B")
                .phone("0987654321")
                .email("student01@gmail.com")
                .role("STUDENT")
                .build();
    }

    @Test
    @DisplayName("TC-01: Lấy danh sách người dùng thành công")
    void getUsersManagement_success_returns200() throws Exception {
        List<UserResponse> responses = List.of(mockUserResponse());

        when(adminService.getUserByUsernameOrPhone(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(responses);

        mockMvc.perform(get("/api/v1/admin/users")
                        .param("username", "lecturer")
                        .param("phone", "098")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("lecturer01"))
                .andExpect(jsonPath("$[0].fullName").value("Nguyễn Văn A"));
    }

    @Test
    @DisplayName("TC-02: Tạo người dùng thành công trả về 201")
    void createNewUser_success_returns201() throws Exception {
        RegisterUserRequest request = mockRegisterRequest();
        UserResponse response = mockUserResponse();

        when(adminService.createNewUser(any(RegisterUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("lecturer01"))
                .andExpect(jsonPath("$.email").value("lecturer01@gmail.com"));
    }

    @Test
    @DisplayName("TC-03: Tạo người dùng thiếu username trả về 400")
    void createNewUser_invalid_returns400() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("")
                .password("123456")
                .fullName("Nguyễn Văn B")
                .phone("0987654321")
                .email("student01@gmail.com")
                .role("STUDENT")
                .build();

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-04: Tìm người dùng theo id thành công")
    void getUserById_success_returns200() throws Exception {
        UserResponse response = mockUserResponse();

        when(adminService.getUsersByIdOrUserCode("1"))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("lecturer01"))
                .andExpect(jsonPath("$.role").value("LECTURER"));
    }

    @Test
    @DisplayName("TC-05: Cập nhật người dùng thành công")
    void updateUser_success_returns200() throws Exception {
        UserResponse response = mockUserResponse();

        when(adminService.updateUser(eq(1L), any()))
                .thenReturn(response);

        // TODO: thay JSON này theo đúng field của UpdateUserRequest thực tế của bạn
        String requestJson = """
                {
                  "fullName": "Nguyễn Văn A",
                  "phone": "0987654321",
                  "email": "lecturer01@gmail.com",
                  "isActive": true,
                  "roleName": "LECTURER"
                }
                """;

        mockMvc.perform(put("/api/v1/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Nguyễn Văn A"));
    }

    @Test
    @DisplayName("TC-06: Patch người dùng thành công")
    void patchUser_success_returns200() throws Exception {
        UserResponse response = mockUserResponse();

        when(adminService.patchUser(eq(1L), any()))
                .thenReturn(response);

        // TODO: thay JSON này theo đúng field của UpdateUserPatchRequest thực tế của bạn
        String requestJson = """
                {
                  "fullName": "Nguyễn Văn C",
                  "email": "lecturer01@gmail.com",
                  "roleCode": "LECTURER",
                  "isActive": true
                }
                """;

        mockMvc.perform(patch("/api/v1/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("lecturer01"));
    }

    @Test
    @DisplayName("TC-07: Xóa người dùng thành công")
    void deleteUser_success_returns204() throws Exception {
        ThrowResponse response = ThrowResponse.builder()
                .message("Xóa người dùng thành công")
                .build();

        when(adminService.deleteUserById(eq(1L), any(HttpServletRequest.class)))
                .thenReturn(response);

        mockMvc.perform(delete("/api/v1/admin/users/1"))
                .andExpect(status().isNoContent());
        // 204 thì nên không assert body
    }
}