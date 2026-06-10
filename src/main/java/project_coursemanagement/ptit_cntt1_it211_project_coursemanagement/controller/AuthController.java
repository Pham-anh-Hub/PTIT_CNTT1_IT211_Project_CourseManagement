package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LoginRequestDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest){
        // Gọi đến AuthService
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
