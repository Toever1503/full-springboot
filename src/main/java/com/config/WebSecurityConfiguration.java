package com.config;


import com.config.jwt.JwtAuthenticationProvider;
import com.config.jwt.JwtFilter;
import com.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(

            new AntPathRequestMatcher("/users/signup"),
            new AntPathRequestMatcher("/users/login"),
            new AntPathRequestMatcher("/users/forget-password"),
            new AntPathRequestMatcher("/users/change-password/**"),
            new AntPathRequestMatcher("/addresses/provinces"),
            new AntPathRequestMatcher("/addresses/getAllDistrict/**"),
            new AntPathRequestMatcher("/addresses/getAllWards/**"),
            new AntPathRequestMatcher("/swagger-resources/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/v2/api-docs"),
            new AntPathRequestMatcher("/webjars/**")
    );
    private RequestMatcher PRIVATE_URLS = new NegatedRequestMatcher(PUBLIC_URLS);
    @Autowired
    @Lazy
    private IUserService userService;


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PUBLIC_URLS);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManager authenticationManager = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .parentAuthenticationManager(authentication -> {
                    throw new RuntimeException("No authentication manager available");
                }).build();
        httpSecurity
                .authenticationProvider(new JwtAuthenticationProvider())
                .authenticationManager(authenticationManager);
        return authenticationManager;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().disable();
        http.cors().and().csrf().disable()
                .formLogin().disable()
                .logout().disable();

        http.authorizeRequests()
                .requestMatchers(PRIVATE_URLS).authenticated()
                .and().exceptionHandling().authenticationEntryPoint((req, res, auth) -> {
                    res.sendError(401, "You must have to login");
                });
        http.addFilterBefore(new JwtFilter(userService), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
