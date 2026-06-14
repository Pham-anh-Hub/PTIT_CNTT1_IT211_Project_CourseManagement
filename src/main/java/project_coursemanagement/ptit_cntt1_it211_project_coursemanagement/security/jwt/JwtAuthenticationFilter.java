package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.TokenInvalidException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserDetailsServiceCustom;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsServiceCustom userDetailsServiceCustom;
    private final StringRedisTemplate redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromHeader(request);
        try {
            if (token != null && jwtProvider.validateToken(token)) {
                // Kiểm tra token có trong blacklist không
                boolean isBlacklisted = redisTemplate.hasKey(token);
                if (isBlacklisted){
                    throw new TokenInvalidException("Token đã bị vô hiệu hóa, vui lòng đăng nhập hoặc kiểm tra lại");
                }

                String username = jwtProvider.getUsernameFromToken(token);
                UserPrinciple userDetails =
                        (UserPrinciple) userDetailsServiceCustom.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken userAuthenToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(userAuthenToken);
            }
        } catch (Exception e) {
            // In ra lỗi thật sự thay vì nuốt im lặng
            throw new RuntimeException(e.getMessage());
        }
        filterChain.doFilter(request, response);

    }


    private String getTokenFromHeader(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            return header.substring(7);
        }
        return null;
    }
}
