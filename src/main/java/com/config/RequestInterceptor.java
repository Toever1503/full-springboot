package com.config;

import com.config.jwt.JwtProvider;
import com.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class RequestInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RequestInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("AfterGoOutFilter.postHandle");

        String token = request.getHeader("Authorization");
        if (token != null) {
            if (this.jwtProvider.canTokenBeRefreshed(token)) {
                Cookie cookieToken = new Cookie("user_logged", this.jwtProvider.generateToken(SecurityUtils.getCurrentUsername(), 0));
                cookieToken.setMaxAge(JwtProvider.JWT_TOKEN_VALIDITY.intValue());
                response.addCookie(cookieToken);
            }
        }
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
