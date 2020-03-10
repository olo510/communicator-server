<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html xmlns:th="https://www.thymeleaf.org">
<head>
<title>Zaloguj</title>
<link href="<c:url value="/resources/bootstrap.css" />" rel="stylesheet">
<link href="<c:url value="/resources/style.css" />" rel="stylesheet">
</head>
<body class="text-center" style="background-image: url('<c:url value="/resources/static/img/rain.jpg"/>');">
		<img src="<c:url value="/resources/static/img/cocplusnapis.png"/>" width="500px"class="mx-auto d-block">
		<form name="f" th:action="@{/login}" method="post" class="form-signin">
			<h1 class="h3 mb-3 font-weight-normal lcd">Log in</h1>
			<div class="alert alert-error lcd">${error}</div>
			<fieldset>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				<input type="text" id="username" name="username" class="login lcd" placeholder="nick"/> 
				<input type="password" id="password" name="password" class="login lcd" placeholder="password"/>
				<button class="btn btn-lg btn-block btn-main lcd" type="submit">Go</button>
			</fieldset>
		</form>
</body>
</html>