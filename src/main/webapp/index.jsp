<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Welcome">
    <div class="jumbotron">
        <h1 class="display-4">MSAL4J Authentication Demo</h1>
        <p class="lead">This application demonstrates Microsoft Authentication using MSAL4J with servlets.</p>
        <hr class="my-4">
        <p>Features include login, logout, session management, token expiry handling, and refresh token usage.</p>
        
        <c:choose>
            <c:when test="${not empty sessionScope.auth_result}">
                <a href="${pageContext.request.contextPath}/secure/home" class="btn btn-primary btn-lg">Go to Home</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-primary btn-lg">Login</a>
            </c:otherwise>
        </c:choose>
    </div>
</t:layout>
