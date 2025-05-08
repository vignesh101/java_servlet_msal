package com.auth.msal.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auth.msal.service.AuthService;
import com.auth.msal.util.SessionManager;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);

        if (SessionManager.isAuthenticated(session) && 
            !SessionManager.isTokenExpired(session)) {
            
            response.sendRedirect(request.getContextPath() + "/secure/home");
            return;
        }

        if (SessionManager.isAuthenticated(session) && 
            SessionManager.isTokenExpired(session)) {
            
            boolean refreshed = SessionManager.refreshTokenIfNeeded(session);
            if (refreshed) {
                response.sendRedirect(request.getContextPath() + "/secure/home");
                return;
            }
        }

        String state = SessionManager.generateAndStoreState(session);
        String nonce = SessionManager.generateAndStoreNonce(session);

        String authUrl = AuthService.getAuthorizationUrl(state, nonce);
        response.sendRedirect(authUrl);
    }
}
