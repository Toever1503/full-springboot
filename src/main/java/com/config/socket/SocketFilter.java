package com.config.socket;

import com.dtos.ResponseDto;
import com.services.IUserService;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SocketFilter extends OncePerRequestFilter {

    private final IUserService userService;

    public SocketFilter(IUserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        if (req.getServletPath().equals("/socket")) {
            String token = req.getParameter("Authorization");
            if (token.length() == 0) {
                // token null
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().println(new JSONObject(new ResponseDto("Unauthorized! Please login to use this feature!", "ERROR", null)));
            } else {
                this.userService.tokenFilter(token.substring(7), req, res);
                Long id = SecurityUtils.getCurrentUserId();
                if (SocketHandler.userSessions.containsKey(id))
                    return;
            }
        }
        filterChain.doFilter(req, res);
    }
}
