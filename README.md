# MSAL4J Authentication Servlet Application

This project demonstrates how to implement Microsoft Authentication Library (MSAL) for Java in a servlet-based web application. It provides a complete solution for authentication and authorization using Azure Active Directory.

## Features

- Authentication with Microsoft Azure AD using MSAL4J
- Session management and token storage
- Token expiry detection and automatic refresh
- Secure area with authorization filter
- JSP-based user interface
- Complete logout functionality

## Project Structure

- **Java Package Structure**:
  - `com.auth.msal.config`: Configuration classes
  - `com.auth.msal.filter`: Servlet filters for authentication
  - `com.auth.msal.model`: Data models
  - `com.auth.msal.service`: Authentication services
  - `com.auth.msal.servlet`: Servlet controllers
  - `com.auth.msal.util`: Utility classes

- **Web Resources**:
  - JSP pages for UI
  - Layout tag files
  - Error handling pages

## Configuration

Before running the application, you need to register it in the Azure Portal and update the `msal.properties` file with your application configuration:

1. Register an application in the Azure Portal
2. Get the Client ID and create a Client Secret
3. Configure the Redirect URI
4. Update the `msal.properties` file with your values

## How to Run

1. Configure your MSAL properties in `src/main/resources/msal.properties`
2. Build the project using Maven:
   ```
   mvn clean package
   ```
3. Deploy the WAR file to your servlet container (Tomcat, Jetty, etc.) or run it using the embedded Jetty:
   ```
   mvn jetty:run
   ```
4. Access the application at: http://localhost:8080/msal4j-auth/

## Technical Implementation Details

- **Session Management**: Sessions are maintained on the server-side, not in the JSP
- **Token Refresh**: Automatic refresh of access tokens using refresh tokens
- **Security**: CSRF protection with state parameter and nonce for replay protection
- **JDK Compatibility**: Built for JDK 1.8

## Requirements

- Java 8 or higher
- Maven 3.6 or higher
- A servlet container (Tomcat, Jetty, etc.)
- An Azure Active Directory tenant

## License

This project is licensed under the MIT License - see the LICENSE file for details.
