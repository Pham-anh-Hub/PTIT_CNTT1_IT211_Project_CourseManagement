package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserDetailsServiceCustom;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsServiceCustom userDetailsServiceCustom;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Chặn

        // 1.Lấy Token từ header/cookies
        String token = getTokenFromHeader(request);
        if(token != null && jwtProvider.validateToken(token)){
            String username = jwtProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsServiceCustom.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken userAuthenToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(userAuthenToken);
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
