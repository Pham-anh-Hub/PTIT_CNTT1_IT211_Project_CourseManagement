package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service;

import org.springframework.http.ResponseEntity;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LoginRequestDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RefreshTokenRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.LoginResponse;

public interface AuthService {

    public LoginResponse login(LoginRequestDTO loginRequest);


    public String refreshToken (RefreshTokenRequest refreshTokenRequest);
}
