<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Secure Home">
    <div class="row">
        <div class="col-md-12">
            <h2>Welcome to the Secure Area</h2>
            <p>You are successfully authenticated using Microsoft Authentication Library.</p>
            
            <c:if test="${not empty userInfo}">
                <div class="card mb-4">
                    <div class="card-header">
                        <h4>User Profile from Microsoft Graph</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h5>Basic Information</h5>
                                <p><strong>Name:</strong> ${userInfo.name}</p>
                                <p><strong>Email:</strong> ${userInfo.email}</p>
                                <p><strong>ID:</strong> ${userInfo.id}</p>
                            </div>
                            <div class="col-md-6">
                                <h5>Work Information</h5>
                                <p><strong>Job Title:</strong> ${not empty userInfo.jobTitle ? userInfo.jobTitle : 'N/A'}</p>
                                <p><strong>Department:</strong> ${not empty userInfo.department ? userInfo.department : 'N/A'}</p>
                                <p><strong>Office Location:</strong> ${not empty userInfo.officeLocation ? userInfo.officeLocation : 'N/A'}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <div class="card mb-4">
                <div class="card-header">
                    <h4>Session Information</h4>
                </div>
                <div class="card-body">
                    <p><strong>Token Expires In:</strong> ${tokenExpiresIn} seconds</p>
                    <c:choose>
                        <c:when test="${tokenExpiresIn > 300}">
                            <div class="alert alert-success">
                                Your session is active and the token is valid.
                            </div>
                        </c:when>
                        <c:when test="${tokenExpiresIn > 60}">
                            <div class="alert alert-warning">
                                Your session will expire soon. The page will automatically refresh to extend your session.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-danger">
                                Your session is about to expire! The page will automatically refresh to attempt extending your session.
                            </div>
                        </c:otherwise>
                    </c:choose>
                    
                    <div class="progress mb-3">
                        <div class="progress-bar ${tokenExpiresIn > 300 ? 'bg-success' : (tokenExpiresIn > 60 ? 'bg-warning' : 'bg-danger')}" 
                             role="progressbar" 
                             style="width: ${(tokenExpiresIn / 3600) * 100}%;" 
                             aria-valuenow="${(tokenExpiresIn / 3600) * 100}" 
                             aria-valuemin="0" 
                             aria-valuemax="100">
                            ${Math.round((tokenExpiresIn / 3600) * 100)}%
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="card mb-4">
                <div class="card-header">
                    <h4>Token Information</h4>
                </div>
                <div class="card-body">
                    <p>Your access token allows you to access Microsoft Graph API resources on behalf of your account.</p>
                    <p>The data above was retrieved from Microsoft Graph API using your access token.</p>
                    <p>The token has been granted the following permissions:</p>
                    <ul>
                        <li><strong>User.Read</strong> - Read your basic profile information</li>
                        <li><strong>openid</strong> - Sign you in</li>
                        <li><strong>profile</strong> - Access your basic profile</li>
                        <li><strong>email</strong> - Read your email address</li>
                    </ul>
                </div>
            </div>
            
            <div class="text-center mt-4">
                <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-danger">Logout</a>
            </div>
        </div>
    </div>
    
    <script>
        // Calculate refresh time - refresh the page when less than 5 minutes remain
        // or 30 seconds before expiry, whichever comes first
        const expiresIn = ${tokenExpiresIn};
        const refreshTime = Math.min(expiresIn - 30, 300);
        
        if (expiresIn > 30) {
            console.log("Token expires in " + expiresIn + " seconds. Will refresh in " + refreshTime + " seconds");
            setTimeout(function() {
                console.log("Refreshing page to extend session...");
                window.location.reload();
            }, refreshTime * 1000);
        } else if (expiresIn > 0) {
            // If very close to expiry, refresh very soon
            console.log("Token about to expire! Refreshing in 5 seconds...");
            setTimeout(function() {
                window.location.reload();
            }, 5000);
        }
    </script>
</t:layout>
