package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import com.sun.jdi.LongValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Role;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByCode(RoleName code);

    Role findByRoleName(String roleName);
}
