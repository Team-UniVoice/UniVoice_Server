package sopt.univoice.domain.auth;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sopt.univoice.infra.common.exception.UnauthorizedException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;

@Component
public class PrincipalHandler {

    private static final String ANONYMOUS_USER = "anonymousUser";

    public Long getUserIdFromPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        isPrincipalNull(principal);
        System.out.println("Principal: " + principal);
        return Long.valueOf(principal.toString());
    }

    public void isPrincipalNull(final Object principal) {
        if (principal.toString().equals(ANONYMOUS_USER)) {
            System.out.println("Principal is anonymous");
            throw new UnauthorizedException(ErrorMessage.JWT_UNAUTHORIZED_EXCEPTION);
        }
    }
}