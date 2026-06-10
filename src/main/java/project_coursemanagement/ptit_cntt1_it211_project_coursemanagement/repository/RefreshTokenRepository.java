package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
