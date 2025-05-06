<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Error">
    <div class="row">
        <div class="col-md-12 text-center">
            <div class="alert alert-danger" role="alert">
                <h4 class="alert-heading">Oops! Something went wrong.</h4>
                
                <c:choose>
                    <c:when test="${not empty error}">
                        <p><strong>Error:</strong> ${error}</p>
                        <c:if test="${not empty error_description}">
                            <p>${error_description}</p>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <p>An unexpected error occurred. Please try again later.</p>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Home</a>
            </div>
        </div>
    </div>
</t:layout>
