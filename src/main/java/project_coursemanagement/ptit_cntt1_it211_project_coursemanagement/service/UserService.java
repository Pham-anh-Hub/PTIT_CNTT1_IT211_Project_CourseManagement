package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RegisterUserRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.UpdateUserPatchRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.UpdateUserRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ThrowResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getUserByUsernameOrPhone(String username, String phone, Pageable pageable);

    UserResponse createNewUser(RegisterUserRequest userRequest);

    UserResponse getUsersByIdOrUserCode(String userCode);

    UserResponse updateUser(Long id, UpdateUserRequest request);
    UserResponse patchUser(Long id, UpdateUserPatchRequest request);

    ThrowResponse deleteUserById(Long id, HttpServletRequest request);
}
