package sopt.univoice.domain.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    public UserAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static UserAuthentication createUserAuthentication(Long userId, Collection<? extends GrantedAuthority> authorities) {
        return new UserAuthentication(userId, null, authorities);
    }
}

