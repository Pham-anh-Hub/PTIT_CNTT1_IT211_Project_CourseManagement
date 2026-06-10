package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponseDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;

import java.util.List;

public interface AdminService {
    List<UserResponseDTO> getUserByUsernameOrPhone(String username, String phone, Pageable pageable);
}
