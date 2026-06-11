package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.RefreshToken;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> getRefreshTokenByTokenAndUser(String token, Users user);

    Optional<RefreshToken> findByUser_UsernameAndToken(String userUsername, String token);

}
