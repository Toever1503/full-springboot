package com.config;


import com.config.jwt.JwtAuthenticationProvider;
import com.config.jwt.JwtFilter;
import com.config.socket.SocketFilter;
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
    //List of public urls
    private final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/users/signup"),
            new AntPathRequestMatcher("/users/login"),
            new AntPathRequestMatcher("/users/forget-password"),
            new AntPathRequestMatcher("/users/set-password/**"),
            new AntPathRequestMatcher("/addresses/provinces"),
            new AntPathRequestMatcher("/addresses/getAllDistrict/**"),
            new AntPathRequestMatcher("/addresses/getAllWards/**"),
            new AntPathRequestMatcher("/swagger-resources/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/v2/api-docs"),
            new AntPathRequestMatcher("/webjars/**"),
            new AntPathRequestMatcher("/transaction/result"),
            new AntPathRequestMatcher("/categories/all-parent-categories"),
            new AntPathRequestMatcher("/products/get-all"),
            new AntPathRequestMatcher("/products/get-by-id/**"),
            new AntPathRequestMatcher("/categories/slug/**")
    );

    private RequestMatcher PRIVATE_URLS = new NegatedRequestMatcher(PUBLIC_URLS);
    @Autowired
    @Lazy
    private IUserService userService;

    //Gain access for public urls
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PUBLIC_URLS);
    }
    //Authentication manager bean config
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManager authenticationManager = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .parentAuthenticationManager(authentication -> {
                    throw new RuntimeException("Bad Credential");
                }).build();
        httpSecurity
                .authenticationProvider(new JwtAuthenticationProvider())
                .authenticationManager(authenticationManager);
        return authenticationManager;
    }

    //Filter chain bean config
    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().disable();
        http.cors().and().csrf().disable()
                .formLogin().disable()
                .logout().disable();

        http.authorizeRequests()
                .requestMatchers(PRIVATE_URLS).authenticated();
        http.addFilterBefore(new JwtFilter(this.userService), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new SocketFilter(this.userService),JwtFilter.class);
        return http.build();
    }

}
