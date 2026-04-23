<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error | Library Management System</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa;
               display: flex; align-items: center; justify-content: center;
               min-height: 100vh; margin: 0; }
        .box { background: #fff; border-radius: 8px; padding: 48px 40px;
               box-shadow: 0 4px 24px rgba(0,0,0,0.1); text-align: center; max-width: 480px; }
        h1   { font-size: 4rem; color: #c62828; margin-bottom: 10px; }
        h2   { color: #212121; margin-bottom: 12px; }
        p    { color: #757575; font-size: 0.95rem; }
        a    { display: inline-block; margin-top: 24px; padding: 10px 28px;
               background: #1a237e; color: #fff; border-radius: 6px;
               text-decoration: none; font-size: 0.9rem; }
        a:hover { background: #283593; }
    </style>
</head>
<body>
    <div class="box">
        <h1>${pageContext.response.status}</h1>
        <h2>Something went wrong!</h2>
        <p>
            <c:choose>
                <c:when test="${not empty pageContext.errorData.throwable}">
                    <c:out value="${pageContext.errorData.throwable.message}"/>
                </c:when>
                <c:otherwise>
                    An unexpected error occurred. Please try again.
                </c:otherwise>
            </c:choose>
        </p>
        <a href="/">← Back to Home</a>
    </div>
</body>
</html>
