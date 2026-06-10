package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    @Query("from Users u where u.username like concat('%',:username, '%') or u.phone like concat('%', :phone, '%')")
    Page<Users> getUsersByUsernameOrPhone(@Param("username") String username, @Param("phone") String phone, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Users> findByUsername(String username);
}
