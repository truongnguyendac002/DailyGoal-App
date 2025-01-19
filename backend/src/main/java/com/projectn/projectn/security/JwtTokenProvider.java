package com.projectn.projectn.security;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.model.User;
import com.projectn.projectn.payload.response.LoginResponse;
import com.projectn.projectn.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.access-jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    @Value("${app.refresh-jwt-expiration-milliseconds}")
    private long jwtRefreshExpirationDate;

    @Autowired
    private UserService userService;

    public LoginResponse generateToken(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user = userService.getUserByEmail(username);
        if (user.isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_FOUND, new Object[] {"User email"} , "User not found");
        }
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
        String accessToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();

        Date refreshExpireDate = new Date(currentDate.getTime() + jwtRefreshExpirationDate);
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(refreshExpireDate)
                .signWith(key())
                .compact();

        return LoginResponse.builder()
                .accessToken(accessToken)
                .expiresIn((int) jwtExpirationDate)
                .refreshToken(refreshToken)
                .refreshExpiresIn((int) jwtRefreshExpirationDate)
                .build();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new AppException(Constant.UNAUTHORIZED,null, "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new AppException(Constant.UNAUTHORIZED,null, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new AppException(Constant.UNAUTHORIZED,null, "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new AppException(Constant.UNAUTHORIZED, null, "JWT claims string is empty.");
        }
        catch (Exception ex) {
            throw new AppException(Constant.UNAUTHORIZED, null, "JWT token is invalid: " + ex.getMessage());
        }
    }
    public String generateAccessToken(String username) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }
    public String generateRefreshToken(String username) {
        Date currentDate = new Date();
        Date refreshExpireDate = new Date(currentDate.getTime() + jwtRefreshExpirationDate);


        return  Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(refreshExpireDate)
                .signWith(key()) // Use a method to retrieve your secret key
                .compact();
    }

}
