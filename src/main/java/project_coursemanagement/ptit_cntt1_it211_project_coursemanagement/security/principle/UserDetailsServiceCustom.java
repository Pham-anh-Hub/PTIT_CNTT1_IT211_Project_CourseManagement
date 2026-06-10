package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceCustom implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username not found"));
        if (user == null || !user.isActive()) {
            throw new UsernameNotFoundException("User not found or disabled");
        }
        String roleCode = String.valueOf(user.getRole().getCode()); // Đảm bảo lấy trường map với cột 'code' trong DB
        String authorityName = roleCode.startsWith("ROLE_") ? roleCode : "ROLE_" + roleCode;

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(authorityName)
        );
        return UserPrinciple.builder().user(user).authorities(authorities).build();
    }
}
