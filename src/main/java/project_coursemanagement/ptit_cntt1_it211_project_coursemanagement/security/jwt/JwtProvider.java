package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.ValidTokenException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RefreshTokenRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiredAccessToken}")
    private Long expiredAccessToken;

    @Value("${jwt.expiredRefreshToken}")
    private Long expiredRefreshToken;

    @Value("${jwt.expiredResetToken}")
    private Long expiredResetToken;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // sinh Access Token
    public String generateAccesToken(UserPrinciple userPrinciple){
        return Jwts.builder()
                .setSubject(userPrinciple.getUserCode())
                .claim("username", userPrinciple.getUsername())
                .claim("roles", userPrinciple.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiredAccessToken))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserPrinciple userPrinciple){
        return Jwts.builder()
                .setSubject(userPrinciple.getUserCode())
                .claim("username", userPrinciple.getUsername())
                .claim("roles", userPrinciple.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiredRefreshToken))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // validate Token
    public boolean validateToken(String token){
        SecretKey secret = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try{
            return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody() != null;
        }catch (ExpiredJwtException ex){
            throw new ValidTokenException("Token đã hết hạn");
        }catch (SignatureException ex){
            throw new ValidTokenException("Token không hợp lệ");
        }catch (MalformedJwtException ex){
            throw new ValidTokenException("Token không đúng định dạng");
        }
    }

    public String generateResetToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("purpose", "RESET_PASSWORD")   // ← điểm phân biệt
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiredResetToken))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Kiểm tra token có đúng purpose RESET_PASSWORD không
    public boolean isResetToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return "RESET_PASSWORD".equals(claims.get("purpose", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    // Lấy userCode từ bất kỳ loại token nào
    public String getUsernameFromResetToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Lấy thời gian hết hạn (dùng khi blacklist)
    public Date getExpirationFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    //
    public String getUsernameFromToken(String token){
        SecretKey secret = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody().get("username", String.class);
    }

}
