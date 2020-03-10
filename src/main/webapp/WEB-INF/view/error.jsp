<html xmlns:th="https://www.thymeleaf.org">
<head>
<title>Błąd</title>
</head>
<body th:include="layout :: body" th:with="content=~{::content}">
	<div>Coś poszło nie tak</div>
</body>
</html>