package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LoginRequestDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.LoginResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.RefreshToken;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.RefreshTokenRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.jwt.JwtProvider;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.AuthService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    @Value("${jwt.expiredAccessToken}")
    private Long expiredAccessToken;

    @Value("${jwt.expiredRefreshToken}")
    private Long expiredRefreshToken;

    @Override
        public LoginResponse login(LoginRequestDTO loginRequest) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            if (authentication.isAuthenticated()){
                Users targetUser = usersRepository.findByUsername(loginRequest.getUsername()).get();
                UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
                String accessToken = jwtProvider.generateAccesToken(user);
                String refreshToken = jwtProvider.generateRefreshToken(user);

                refreshTokenRepository.save(RefreshToken.builder()
                        .id(null)
                        .token(refreshToken)
                        .user(targetUser)
                        .expiredAt(new Date(new Date().getTime() + expiredRefreshToken))
                        .isRevoked(false)
                        .build()
                );
                UserResponse userResponse = UserResponse.builder()
                        .userCode(targetUser.getUserCode())
                        .username(targetUser.getUsername())
                        .fullName(targetUser.getFullName())
                        .phone(targetUser.getPhone())
                        .email(targetUser.getEmail())
                        .role(targetUser.getRole().getCode().toString())
                        .isActive(targetUser.isActive())
                        .createdAt(targetUser.getCreatedAt())
                        .updatedAt(targetUser.getUpdatedAt())
                        .build();
                return LoginResponse.builder().user(userResponse).loginTime(LocalDateTime.now()).accessToken(accessToken).refreshToken(refreshToken).build();
            } else {
                throw new RuntimeException("Username hoặc password chưa đúng, vui lòng kiểm tra lại!!");
            }
        }
}
