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

//Authentication with JW Token
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationProvider.class);
    protected final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //Get user of authentication request
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        //Check password of request with user's password (Decrypted)
        if (!BCrypt.checkpw(authentication.getCredentials().toString(), userDetail.getPassword()))
            throw new BadCredentialsException(JwtAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        //Check user's account status
        check(userDetail);
        //Return detail and token
        return authentication;
    }

    public void check(UserDetails user) {
        //Check if account is locked
        if (!user.isAccountNonLocked()) {
            JwtAuthenticationProvider.this.logger.debug("Failed to authenticate since user account is locked");
            throw new LockedException(JwtAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        }
        //Check if account is disabled
        if (!user.isEnabled()) {
            JwtAuthenticationProvider.this.logger.debug("Failed to authenticate since user account is disabled");
            throw new DisabledException(JwtAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        //Check if account is expired
        if (!user.isAccountNonExpired()) {
            JwtAuthenticationProvider.this.logger.debug("Failed to authenticate since user account has expired");
            throw new AccountExpiredException(JwtAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //Check if username and password authenticate can support the class
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
