package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.TokenInvalidException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.LoginRequestDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RefreshTokenRequest;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final StringRedisTemplate redisTemplate;


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
        Users users = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Người dùng " + username + " không tồn tại, vui lòng kiểm tra lại"));

        RefreshToken refreshToken = refreshTokenRepository.findByUser_UsernameAndToken(username, refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new TokenInvalidException("RefreshToken không hợp lệ, vui lòng kiểm tra lại"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // chỉnh sửa lại để xử lý với redis
        // đưa  access token vào redis blacklist
        try{
            // lấy thời gian hết hạn của token, tính thời gian sống còn lại
            long expiredTime = jwtProvider.getExpirationFromToken(accessToken).getTime();
            long restTime = expiredTime - System.currentTimeMillis();

            if (restTime > 0) {
                redisTemplate.opsForValue().set(accessToken, "revoked", restTime, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            // In lỗi chi tiết ra tab Console của IntelliJ để dễ debug nếu có sự cố khác
            throw new TokenInvalidException("Không thể xử lý token với blacklist: " + e.getMessage());
        }
    }


}
