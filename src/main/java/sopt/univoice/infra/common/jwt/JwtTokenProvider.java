package sopt.univoice.infra.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;



import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String USER_ID = "userId";
    private static final String ROLES = "roles";

    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L * 14;

    @Value("${jwt.secret}")
    private String JWT_SECRET;


    //Authentication 객체로 AccessToken 발행
    public String issueAccessToken(final Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRATION_TIME);
    }


    public String generateToken(Authentication authentication, Long tokenExpirationTime) {
        final Date now = new Date();

        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenExpirationTime));      // 만료 시간

        // Claim에는 token 생성시간과 만료시간, 그리고 사용자 인증 정보가 들어간다.
        claims.put(USER_ID, authentication.getPrincipal());
        claims.put(ROLES, authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList()));

        //헤더 타입을 설정해주는 Header Param, Claim 을 이용한 정보를 대상으로 암호화하여 Jwt 토큰을 만들어 반환.
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // Header
                .setClaims(claims) // Claim
                .signWith(getSigningKey()) // Signature
                .compact();
    }

    private SecretKey getSigningKey() {
        String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes()); //SecretKey 통해 서명 생성
        return Keys.hmacShaKeyFor(encodedKey.getBytes());   //일반적으로 HMAC (Hash-based Message Authentication Code) 알고리즘 사용
    }

    //Token에서 Claim을 추출하는 과정에서 에러가 발생하면 Catch한 후 Validation을 진행
    public JwtValidationType validateToken(String token) {
        try {
            final Claims claims = getBody(token);
            System.out.println("Token is valid(Provider): " + claims);
            return JwtValidationType.VALID_JWT;
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT Token: " + ex.getMessage());
            return JwtValidationType.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT Token: " + ex.getMessage());
            return JwtValidationType.EXPIRED_JWT_TOKEN;
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT Token: " + ex.getMessage());
            return JwtValidationType.UNSUPPORTED_JWT_TOKEN;
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT Token is empty: " + ex.getMessage());
            return JwtValidationType.EMPTY_JWT;
        }
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Token의 Claim에서 USER_ID(userId) 값을 추출한다.
    public Long getUserFromJwt(String token) {
        Claims claims = getBody(token);
        return Long.valueOf(claims.get(USER_ID).toString());
    }


    public Collection<? extends GrantedAuthority> getAuthoritiesFromJwt(String token) {
        Claims claims = getBody(token);
        List<String> roles = claims.get(ROLES, List.class);
        if (roles == null) {
            System.out.println("역할 정보가 없어 빈 리스트 " );

            return new ArrayList<>(); // 역할 정보가 없을 경우 빈 리스트 반환
        }

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


}
