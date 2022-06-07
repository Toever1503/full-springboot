package com.config.jwt;

import com.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private final IUserService userService;

    public JwtFilter(IUserService userService) {
        this.userService = userService;
    }


    //Filter jw token
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtFilter is checking");
        //If request method is options, do filter
        if (req.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(req, res);
        }
        //if not, checking token and do filter afterward
        else {
            String token = null;
            token = req.getHeader("Authorization");
            System.out.println("token: " + token);
            if (token == null)
                // token not valid
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized! Please login to use this feature!");
            else {
                this.userService.tokenFilter(token.substring(7), req);
                filterChain.doFilter(req, res);
            }
        }

    }
}
