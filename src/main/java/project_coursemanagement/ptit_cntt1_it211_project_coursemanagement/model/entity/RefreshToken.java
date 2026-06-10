package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Tại sao lại dùng số nguyên mà k phải chuỗi (UUID) - tốc độ truy xuất dữ liệu với dạng số nguyên nhanh hơn chuỗi
    private String token;
    // Mỗi quan hệ 1 user có thể có nhiều token
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    // Thời gian hết hạn, để chi cần gọi lấy ra chứ không phải mất công giải mã thời gian được mã hóa trong token nữa
    @Column(name = "expired_at")
    private Date expiredAt;
    private boolean isRevoked = false;
}
