package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle;

import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserPrinciple implements UserDetails {
    private Users user;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getUserCode(){
        return user.getUserCode();
    }
    public Long getUserId(){
        return user.getId();
    }

    public void setUsername(String student01) {
    }
}
