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
                        <h4>User Information</h4>
                    </div>
                    <div class="card-body">
                        <p><strong>Name:</strong> ${userInfo.name}</p>
                        <p><strong>ID:</strong> ${userInfo.id}</p>
                    </div>
                </div>
            </c:if>
            
            <div class="card mb-4">
                <div class="card-header">
                    <h4>Session Information</h4>
                </div>
                <div class="card-body">
                    <p><strong>Token Expires In:</strong> ${expiresIn} seconds</p>
                    <div class="progress mb-3">
                        <div class="progress-bar" role="progressbar" 
                             style="width: ${(expiresIn / 3600) * 100}%;" 
                             aria-valuenow="${(expiresIn / 3600) * 100}" 
                             aria-valuemin="0" 
                             aria-valuemax="100">
                            ${(expiresIn / 3600) * 100}%
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="text-center mt-4">
                <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-danger">Logout</a>
            </div>
        </div>
    </div>
    
    <script>
        const expiresIn = ${expiresIn};
        if (expiresIn > 30) {
            setTimeout(function() {
                window.location.reload();
            }, (expiresIn - 30) * 1000);
        }
    </script>
</t:layout>
