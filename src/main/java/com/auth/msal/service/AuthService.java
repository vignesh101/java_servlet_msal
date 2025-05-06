package com.auth.msal.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.auth.msal.config.MsalConfig;
import com.auth.msal.model.AuthenticationResult;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.RefreshTokenParameters;
import com.microsoft.aad.msal4j.SilentParameters;

public class AuthService {

    public static String getAuthorizationUrl(String state, String nonce) {
        Set<String> scopes = new HashSet<>(MsalConfig.getScopes());
        String authorityUrl = MsalConfig.getAuthority();
        String clientId = MsalConfig.getClientId();
        String redirectUri = MsalConfig.getRedirectUri();
        
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(authorityUrl)
                  .append("/oauth2/v2.0/authorize?")
                  .append("client_id=").append(clientId)
                  .append("&response_type=code")
                  .append("&redirect_uri=").append(redirectUri)
                  .append("&response_mode=query")
                  .append("&scope=").append(String.join("%20", scopes))
                  .append("&state=").append(state);
        
        if (nonce != null && !nonce.isEmpty()) {
            urlBuilder.append("&nonce=").append(nonce);
        }
        
        return urlBuilder.toString();
    }
    

    public static AuthenticationResult getTokensByAuthorizationCode(String authCode)
            throws ExecutionException, InterruptedException, URISyntaxException {
        
        AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(
                authCode,
                new URI(MsalConfig.getRedirectUri()))
            .scopes(new HashSet<>(MsalConfig.getScopes()))
            .build();
        
        CompletableFuture<IAuthenticationResult> future = 
                MsalConfig.getMsalClient().acquireToken(parameters);
        IAuthenticationResult result = future.get();
        
        return mapToAuthResult(result);
    }

    public static AuthenticationResult getTokensSilently(String accountId)
            throws ExecutionException, InterruptedException, MalformedURLException {
        
        IAccount account = MsalConfig.getMsalClient().getAccounts().join()
                .stream()
                .filter(a -> a.homeAccountId().equals(accountId))
                .findFirst()
                .orElse(null);
        
        if (account == null) {
            return null;
        }
        
        SilentParameters parameters = SilentParameters.builder(
                new HashSet<>(MsalConfig.getScopes()))
            .account(account)
            .build();
        
        CompletableFuture<IAuthenticationResult> future = 
                MsalConfig.getMsalClient().acquireTokenSilently(parameters);
        IAuthenticationResult result = future.get();
        
        return mapToAuthResult(result);
    }
    

    public static AuthenticationResult refreshTokens(String refreshToken) 
            throws ExecutionException, InterruptedException {

        RefreshTokenParameters parameters = RefreshTokenParameters.builder(
                new HashSet<>(MsalConfig.getScopes()), 
                refreshToken)
            .build();
        
        CompletableFuture<IAuthenticationResult> future = 
                MsalConfig.getMsalClient().acquireToken(parameters);
        IAuthenticationResult result = future.get();
        
        return mapToAuthResult(result);
    }

    private static AuthenticationResult mapToAuthResult(IAuthenticationResult result) {
        if (result == null) {
            return null;
        }

        AuthenticationResult authResult = new AuthenticationResult(
                result.account().username(),
                result.accessToken(),
                "REFRESH_TOKEN_MANAGED_BY_MSAL", // Placeholder since we can't access the refresh token
                result.idToken(),
                result.expiresOnDate().toInstant().getEpochSecond()
        );

        if (result.idToken() != null && !result.idToken().isEmpty()) {
            Map<String, Object> userInfo = new HashMap<>();

            userInfo.put("name", result.account().username());
            userInfo.put("id", result.account().homeAccountId());
            
            authResult.setUserInfo(userInfo);
        }
        
        return authResult;
    }
    

    public static String getLogoutUrl(String customPostLogoutRedirectUri) {
        String redirectUri = (customPostLogoutRedirectUri != null && !customPostLogoutRedirectUri.isEmpty())
                ? customPostLogoutRedirectUri
                : MsalConfig.getPostLogoutRedirectUri();
        
        return MsalConfig.getLogoutEndpoint() + 
               "?post_logout_redirect_uri=" + redirectUri;
    }
}
