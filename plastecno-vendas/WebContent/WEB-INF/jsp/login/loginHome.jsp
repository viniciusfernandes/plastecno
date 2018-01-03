<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html >
<html >
<head>


<jsp:include page="/bloco/bloco_header.jsp" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/mensagem.css"/>" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<style type="text/css">
nav {
	height: 1%;
}
</style>
</head>
<body>
	<div id="content">
		<header class="logo">
			<div id="header_content">
			</div>
		</header>
		
		<form action="<c:url value="/login/entrar"/>" method="post">
			<div class="bloco_autenticacao flutuante_esquerda">
				<input type="submit" value="" class="botaoLogin" title="Entrar no sistema"/>
				<input type="password" id="sigla" name="senha" style="width: 8%"/>
				<label>Senha:</label>
				<input type="text" id="sigla" name="email" style="width: 25%"/>
				<label>Email:</label>
			</div>
		</form>
		
		<div class="enfeite flutuante_esquerda"></div>
		<div class="enfeite flutuante_esquerda"></div>
		<div class="enfeite flutuante_esquerda"></div>
		
		<nav class="flutuante_esquerda">
			<ul>
											
			</ul>
		</nav>
		<jsp:include page="/bloco/bloco_mensagem.jsp" />
	</div>
	
</body>
</html>
