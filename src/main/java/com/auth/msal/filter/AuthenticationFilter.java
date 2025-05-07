package com.auth.msal.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auth.msal.model.AuthenticationResult;
import com.auth.msal.util.SessionManager;

public class AuthenticationFilter implements Filter {

    private static final int EXPIRY_WARNING_THRESHOLD = 60; // 60 seconds (1 minute)

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);

        if (!SessionManager.isAuthenticated(session)) {
            String loginUrl = httpRequest.getContextPath() + "/auth/login";
            httpResponse.sendRedirect(loginUrl);
            return;
        }

        AuthenticationResult authResult = SessionManager.getAuthResult(session);

        if (authResult.isExpired()) {
            boolean refreshed = SessionManager.refreshTokenIfNeeded(session);
            
            if (!refreshed) {
                String loginUrl = httpRequest.getContextPath() + "/auth/login";
                httpResponse.sendRedirect(loginUrl);
                return;
            }
        }
        else if (authResult.getExpiresIn() <= EXPIRY_WARNING_THRESHOLD) {
            SessionManager.refreshTokenIfNeeded(session);
        }

        request.setAttribute("tokenExpiresIn", authResult.getExpiresIn());

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
