package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_code")
    private String userCode;

    @Column(unique = true)
    private String username;

    @Column(name = "password_hashed")
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    @Column(unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "status")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "gradedBy")
    @Builder.Default
    private List<Submission> gradedSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "lecturer")
    @Builder.Default
    private List<Courses> teachingCourses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<TokenBlacklist> tokenBlacklists = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
