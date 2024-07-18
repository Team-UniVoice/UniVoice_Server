package sopt.univoice.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sopt.univoice.domain.auth.UserAuthentication;
import sopt.univoice.infra.common.exception.UnauthorizedException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;
import sopt.univoice.infra.common.jwt.JwtTokenProvider;
import sopt.univoice.infra.common.jwt.JwtValidationType;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final Set<String> WHITE_LIST_PATHS = new HashSet<>();

    static {
        WHITE_LIST_PATHS.add("/api/v1/auth/signin");
        WHITE_LIST_PATHS.add("/api/v1/universityData/university");
        WHITE_LIST_PATHS.add("/api/v1/universityData/department");
        WHITE_LIST_PATHS.add("/api/v1/auth/check-email");
        WHITE_LIST_PATHS.add("/api/v1/auth/signup");
        WHITE_LIST_PATHS.add("/api/v1/auth/accecpt");
        WHITE_LIST_PATHS.add("/api/v1/profile");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 화이트리스트 경로인 경우 필터를 통과시킴
        System.out.println("Request URI(filter): " + request.getRequestURI());
        if (WHITE_LIST_PATHS.contains(request.getRequestURI())) {
            System.out.println("Whitelist path, skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = getJwtFromRequest(request);
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token) == JwtValidationType.VALID_JWT) {
                Long userId = jwtTokenProvider.getUserFromJwt(token);
                Collection authorities = jwtTokenProvider.getAuthoritiesFromJwt(token);
                System.out.println("Authenticated User ID(filter): " + userId);
                System.out.println("Authorities(filter): " + authorities);

                UserAuthentication authentication = new UserAuthentication(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("User authenticated successfully with roles: (filter)" + authorities);
            } else {
                System.out.println("Token is not valid or not present");
            }
        } catch (Exception exception) {
            System.out.println("Exception during token validation: " + exception.getMessage());
            throw new UnauthorizedException(ErrorMessage.JWT_UNAUTHORIZED_EXCEPTION);
        }
        System.out.println("ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ");
        filterChain.doFilter(request, response);
        System.out.println("ㅇㅇㅇㅇ");
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring("Bearer ".length());
            System.out.println("Extracted JWT Token(filter): " + token);
            return token;
        }
        System.out.println("No JWT Token found in request");
        return null;
    }
}
