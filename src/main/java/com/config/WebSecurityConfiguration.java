package com.config;


import com.config.jwt.JwtAuthenticationProvider;
import com.config.jwt.JwtFilter;
import com.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

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
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PUBLIC_URLS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new JwtAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS);

        http.cors().and().csrf().disable()
                .formLogin().disable()
                .logout().disable();

        http.authorizeRequests()
                .requestMatchers(PRIVATE_URLS).authenticated()
                .and().exceptionHandling().authenticationEntryPoint((req, res, auth) -> {
                    res.sendError(401, "You must have to login");
                });
        http.addFilterBefore(new JwtFilter(userService), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    }
}
