package com.auth.msal.util;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;

import com.auth.msal.model.AuthenticationResult;
import com.auth.msal.service.AuthService;

public class SessionManager {
    
    private static final String AUTH_SESSION_KEY = "auth_result";
    private static final String STATE_SESSION_KEY = "auth_state";
    private static final String NONCE_SESSION_KEY = "auth_nonce";
    

    public static String generateAndStoreState(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute(STATE_SESSION_KEY, state);
        return state;
    }
    

    public static String generateAndStoreNonce(HttpSession session) {
        String nonce = UUID.randomUUID().toString();
        session.setAttribute(NONCE_SESSION_KEY, nonce);
        return nonce;
    }
    

    public static boolean validateState(HttpSession session, String state) {
        String storedState = (String) session.getAttribute(STATE_SESSION_KEY);
        return storedState != null && storedState.equals(state);
    }

    public static void storeAuthResult(HttpSession session, AuthenticationResult result) {
        session.setAttribute(AUTH_SESSION_KEY, result);

        if (result != null) {
            long tokenExpiresIn = result.getExpiresIn();
            int sessionMaxAge = Math.max(60, (int) tokenExpiresIn - 60);
            session.setMaxInactiveInterval(sessionMaxAge);
        }
    }

    public static AuthenticationResult getAuthResult(HttpSession session) {
        return (AuthenticationResult) session.getAttribute(AUTH_SESSION_KEY);
    }

    public static boolean isAuthenticated(HttpSession session) {
        AuthenticationResult result = getAuthResult(session);
        return result != null;
    }

    public static boolean isTokenExpired(HttpSession session) {
        AuthenticationResult result = getAuthResult(session);
        return result == null || result.isExpired();
    }

    public static long getTokenExpiresIn(HttpSession session) {
        AuthenticationResult result = getAuthResult(session);
        if (result != null) {
            return result.getExpiresIn();
        }
        return 0;
    }

    public static boolean refreshTokenIfNeeded(HttpSession session) {
        AuthenticationResult result = getAuthResult(session);
        
        if (result == null) {
            return false;
        }

        if (result.getExpiresIn() < 300) {
            try {
                AuthenticationResult newResult = AuthService.refreshTokens(result.getRefreshToken());
                if (newResult != null) {
                    storeAuthResult(session, newResult);
                    return true;
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return !result.isExpired();
        }

        return true;
    }

    public static void clearAuthResult(HttpSession session) {
        session.removeAttribute(AUTH_SESSION_KEY);
        session.removeAttribute(STATE_SESSION_KEY);
        session.removeAttribute(NONCE_SESSION_KEY);
    }
}
