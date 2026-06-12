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
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsServiceCustom userDetailsServiceCustom;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println(">>> REQUEST URI: " + request.getRequestURI());
        System.out.println(">>> AUTH HEADER: " + request.getHeader("Authorization"));

        String token = getTokenFromHeader(request);
        System.out.println(">>> TOKEN extracted: " + token);

        try {
            if (token != null && jwtProvider.validateToken(token)) {
                String username = jwtProvider.getUsernameFromToken(token);
                System.out.println(">>> USERNAME: " + username);

                UserPrinciple userDetails =
                        (UserPrinciple) userDetailsServiceCustom.loadUserByUsername(username);
                System.out.println(">>> AUTHORITIES: " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken userAuthenToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(userAuthenToken);
                System.out.println(">>> AUTH SET SUCCESSFULLY");
            } else {
                System.out.println(">>> TOKEN NULL OR INVALID");
            }
        } catch (Exception e) {
            // In ra lỗi thật sự thay vì nuốt im lặng
            System.out.println(">>> FILTER ERROR: " + e.getClass().getSimpleName()
                    + " - " + e.getMessage());
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
