package com.auth.msal.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public class AuthenticationResult implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String accessToken;
    private String refreshToken;
    private String idToken;
    private long expiresOn;
    private Map<String, Object> userInfo;
    
    public AuthenticationResult() {
    }
    
    public AuthenticationResult(String username, String accessToken, String refreshToken, 
                                String idToken, long expiresOn) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.idToken = idToken;
        this.expiresOn = expiresOn;
    }
    
    public boolean isExpired() {
        return Instant.now().getEpochSecond() > expiresOn;
    }
    
    public long getExpiresIn() {
        return Math.max(0, expiresOn - Instant.now().getEpochSecond());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public long getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(long expiresOn) {
        this.expiresOn = expiresOn;
    }

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map<String, Object> userInfo) {
        this.userInfo = userInfo;
    }
}
