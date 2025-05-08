package com.auth.msal.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auth.msal.model.AuthenticationResult;
import com.auth.msal.util.SessionManager;


@WebServlet("/secure/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            AuthenticationResult authResult = SessionManager.getAuthResult(session);
            
            if (authResult != null) {
                request.setAttribute("userInfo", authResult.getUserInfo());
                request.setAttribute("expiresIn", authResult.getExpiresIn());
            }
        }
        
        request.getRequestDispatcher("/secure/home.jsp").forward(request, response);
    }
}
