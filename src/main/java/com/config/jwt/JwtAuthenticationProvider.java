package com.config.jwt;

import com.services.CustomUserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationProvider.class);
    protected final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        if (!BCrypt.checkpw(authentication.getCredentials().toString(), userDetail.getPassword()))
            throw new BadCredentialsException(JwtAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        check(userDetail);
        return authentication;
    }

    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            JwtAuthenticationProvider.this.logger.debug("Failed to authenticate since user account is locked");
            throw new LockedException(JwtAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        }
        if (!user.isEnabled()) {
            JwtAuthenticationProvider.this.logger.debug("Failed to authenticate since user account is disabled");
            throw new DisabledException(JwtAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        if (!user.isAccountNonExpired()) {
            JwtAuthenticationProvider.this.logger.debug("Failed to authenticate since user account has expired");
            throw new AccountExpiredException(JwtAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
