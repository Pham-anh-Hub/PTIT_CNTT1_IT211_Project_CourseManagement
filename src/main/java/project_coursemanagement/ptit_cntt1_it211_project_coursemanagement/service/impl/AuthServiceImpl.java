package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.TokenInvalidException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LoginRequestDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RefreshTokenRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.LoginResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.UserResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.RefreshToken;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.TokenBlacklist;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.RefreshTokenRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.jwt.JwtProvider;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.AuthService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
//    private final Token


    @Value("${jwt.expiredAccessToken}")
    private Long expiredAccessToken;

    @Value("${jwt.expiredRefreshToken}")
    private Long expiredRefreshToken;

    @Override
    public LoginResponse login(LoginRequestDTO loginRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        if (authentication.isAuthenticated()) {
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


    // Client gửi RefreshToken đến API /api/auth/refresh để xin cấp lại một AccessToken mới
    // mà người dùng không cần phải gõ lại Password.
    @Override
    public String refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // kiểm tra validate Token
        boolean validToken = jwtProvider.validateToken(refreshTokenRequest.getRefreshToken());
        if (validToken) {
//            lấy ra username
            String username = jwtProvider.getUsernameFromToken(refreshTokenRequest.getRefreshToken());
//            lấy ra user theo username
            Users user = usersRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Người dùng " + username + " không tồn tại, vui lòng kiểm tra lại"));
//            kiểm tra refreshToken có hợp lệ trong DB hay chưa
            RefreshToken token = refreshTokenRepository.getRefreshTokenByTokenAndUser(refreshTokenRequest.getRefreshToken(), user).orElse(null);

            if (token != null && !token.isRevoked()) {
//            Khởi tạo userPrinciple với user trên và quyền của nó
//            rồi khởi tạo accessToken mới và trả về
                Collection<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode())
                );
                UserPrinciple userPrinciple = new UserPrinciple(user, authorities);
                return jwtProvider.generateAccesToken(userPrinciple);
            } else {
                throw new TokenInvalidException("RefreshToken không hợp lệ, vui lòng kiểm tra lại");
            }
        }
        throw new TokenInvalidException("RefreshToken không hợp lệ, vui lòng kiểm tra lại");
    }

    // logout
    public void logout(HttpServletRequest request, RefreshTokenRequest refreshTokenRequest) {
        // lấy ra token access từ header
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")){
            throw new TokenInvalidException("Token không hợp lệ, vui lòng kiểm tra lại");
        }
        // kiểm tra token != null và phải bắt đầu bằng "Bearer "
        // - hợp lệ --> lấy ra token bằng subString(7) cắt bỏ chuỗi "Bearer "
        String accessToken = header.substring(7);
        String username = jwtProvider.getUsernameFromToken(accessToken);
        Users users = usersRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Người dùng " + username + " không tồn tại, vui lòng kiểm tra lại"));
        RefreshToken refreshToken = refreshTokenRepository.findByUser_UsernameAndToken(username, refreshTokenRequest.getRefreshToken());
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // set lại refreshToken bằng 1 chuỗi bất kỳ -  k hợp lệ  rồi lưu lại cái refreshToken đó vào db
        TokenBlacklist tokenBlacklist = TokenBlacklist.builder().id(null).token(accessToken).revokedAt(LocalDateTime.now()).user(users).build();
        // sau đó đưa cái accessToken revoked đó lưu vào 1 đối tượng blacklist   rồi lưuu db
        blackLi
        // vả trả về blacklistToken đó
    }


}
