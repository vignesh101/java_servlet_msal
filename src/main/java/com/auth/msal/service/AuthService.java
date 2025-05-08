package com.auth.msal.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.auth.msal.config.MsalConfig;
import com.auth.msal.model.AuthenticationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.RefreshTokenParameters;
import com.microsoft.aad.msal4j.SilentParameters;

public class AuthService {

    private static final String GRAPH_API_URL = "https://graph.microsoft.com/oidc/userinfo";
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
            throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        
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
            throws ExecutionException, InterruptedException, MalformedURLException, IOException {
        
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
    

    public static AuthenticationResult refreshTokens(String accountId) 
            throws ExecutionException, InterruptedException, MalformedURLException, IOException {
        
        return getTokensSilently(accountId);
    }
    

    private static AuthenticationResult mapToAuthResult(IAuthenticationResult result) throws IOException {
        if (result == null) {
            return null;
        }

        AuthenticationResult authResult = new AuthenticationResult(
                result.account().username(),
                result.accessToken(),
                result.idToken(),
                result.expiresOnDate().toInstant().getEpochSecond()
        );

        Map<String, Object> userInfo = getUserInfoFromGraph(result.accessToken());

        if (userInfo.isEmpty()) {
            userInfo = new HashMap<>();
            userInfo.put("name", result.account().username());
            userInfo.put("id", result.account().homeAccountId());
        }
        
        authResult.setUserInfo(userInfo);
        
        return authResult;
    }

    private static Map<String, Object> getUserInfoFromGraph(String accessToken) {
        Map<String, Object> userInfo = new HashMap<>();
        
        try {
            URL url = new URL(GRAPH_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonNode jsonNode = objectMapper.readTree(response.toString());

                if (jsonNode.has("displayName")) {
                    userInfo.put("name", jsonNode.get("displayName").asText());
                }
                if (jsonNode.has("id")) {
                    userInfo.put("id", jsonNode.get("id").asText());
                }
                if (jsonNode.has("mail")) {
                    userInfo.put("email", jsonNode.get("mail").asText());
                } else if (jsonNode.has("userPrincipalName")) {
                    userInfo.put("email", jsonNode.get("userPrincipalName").asText());
                }
                if (jsonNode.has("jobTitle")) {
                    userInfo.put("jobTitle", jsonNode.get("jobTitle").asText());
                }
                if (jsonNode.has("department")) {
                    userInfo.put("department", jsonNode.get("department").asText());
                }
                if (jsonNode.has("officeLocation")) {
                    userInfo.put("officeLocation", jsonNode.get("officeLocation").asText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return userInfo;
    }
    

    public static String getLogoutUrl(String customPostLogoutRedirectUri) {

        String redirectUri = (customPostLogoutRedirectUri != null && !customPostLogoutRedirectUri.isEmpty())
                ? customPostLogoutRedirectUri
                : MsalConfig.getPostLogoutRedirectUri();
        
        return MsalConfig.getLogoutEndpoint() + 
               "?post_logout_redirect_uri=" + redirectUri;
    }
}
