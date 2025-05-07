package com.auth.msal.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IClientSecret;

public class MsalConfig {
    private static final String PROPERTIES_FILE = "/msal.properties";
    private static Properties properties;
    private static ConfidentialClientApplication msalClient;

    static {
        properties = new Properties();
        try (InputStream inputStream = MsalConfig.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new IOException("Unable to load " + PROPERTIES_FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MSAL configuration", e);
        }
    }

    public static String getClientId() {
        return properties.getProperty("aad.clientId");
    }

    public static String getClientSecret() {
        return properties.getProperty("aad.clientSecret");
    }

    public static String getAuthority() {
        return properties.getProperty("aad.authority");
    }

    public static String getRedirectUri() {
        return properties.getProperty("aad.redirectUri");
    }

    public static String getLogoutEndpoint() {
        return properties.getProperty("aad.logoutEndpoint");
    }
    
    public static String getPostLogoutRedirectUri() {
        return properties.getProperty("aad.postLogoutRedirectUri");
    }

    public static List<String> getScopes() {
        String scopesString = properties.getProperty("aad.scopes");
        return scopesString != null 
            ? Arrays.asList(scopesString.split(",")) 
            : Collections.emptyList();
    }

    public static synchronized ConfidentialClientApplication getMsalClient() {
        if (msalClient == null) {
            try {
                IClientSecret clientSecret = ClientCredentialFactory.createFromSecret(getClientSecret());
                msalClient = ConfidentialClientApplication.builder(
                        getClientId(),
                        clientSecret)
                    .authority(getAuthority())
                    .build();
            } catch (Exception e) {
                throw new RuntimeException("Error initializing MSAL client", e);
            }
        }
        return msalClient;
    }
}
