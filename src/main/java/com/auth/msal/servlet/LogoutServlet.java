package com.auth.msal.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auth.msal.config.MsalConfig;
import com.auth.msal.service.AuthService;
import com.auth.msal.util.SessionManager;


@WebServlet("/auth/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        if (session != null) {
            SessionManager.clearAuthResult(session);
            session.invalidate();
        }

        String redirectUri = request.getParameter("post_logout_redirect_uri");

        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = MsalConfig.getPostLogoutRedirectUri();
        }

        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString());

        String logoutUrl = AuthService.getLogoutUrl(encodedRedirectUri);
        response.sendRedirect(logoutUrl);
    }
}
