package com.auth.msal.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auth.msal.model.AuthenticationResult;
import com.auth.msal.service.AuthService;
import com.auth.msal.util.SessionManager;


@WebServlet("/auth/callback")
public class AuthCallbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String error = request.getParameter("error");
        String errorDescription = request.getParameter("error_description");

        if (error != null && !error.isEmpty()) {
            request.setAttribute("error", error);
            request.setAttribute("error_description", errorDescription);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        if (!SessionManager.validateState(session, state)) {
            request.setAttribute("error", "Invalid state parameter");
            request.setAttribute("error_description", "The state parameter does not match. Possible CSRF attack.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            AuthenticationResult authResult = AuthService.getTokensByAuthorizationCode(code);
            
            if (authResult != null) {
                SessionManager.storeAuthResult(session, authResult);
                response.sendRedirect(request.getContextPath() + "/secure/home");
            } else {
                request.setAttribute("error", "Authentication failed");
                request.setAttribute("error_description", "Failed to get tokens from authorization code.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } catch (ExecutionException | InterruptedException e) {
            request.setAttribute("error", "Authentication failed");
            request.setAttribute("error_description", e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
