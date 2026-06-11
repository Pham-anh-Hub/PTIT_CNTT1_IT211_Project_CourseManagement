package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    @Query("from Users u where u.username like concat('%',:username, '%') and u.phone like concat('%', :phone, '%')")
    Page<Users> getUsersByUsernameAndPhone(@Param("username") String username, @Param("phone") String phone, Pageable pageable);

    @Query("""
        select count(1) from Users u where u.role.roleName like concat('%', :roleName, '%')
        """)
    int getUsersByRole_RoleName(@Param("roleName") String roleRoleName);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByUserCodeContaining(String userCode);

    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhone(String phone);


    @Query("FROM Users u WHERE u.role.roleName = 'Lecturer' " +
            "AND (:email = '' OR u.email LIKE CONCAT('%', :email, '%')) " +
            "AND (:phone = '' OR u.phone LIKE CONCAT('%', :phone, '%'))")
    Optional<Users> findLecturerByEmailAndPhone(@Param("email") String email, @Param("phone") String phone);
}
