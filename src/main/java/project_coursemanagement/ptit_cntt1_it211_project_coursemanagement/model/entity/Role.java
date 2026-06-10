package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity;


import jakarta.persistence.*;
import lombok.*;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.RoleName;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleName code;
    private String roleName;

    @OneToMany(mappedBy = "role")
    @Builder.Default
    private List<Users> users = new ArrayList<>();
}
