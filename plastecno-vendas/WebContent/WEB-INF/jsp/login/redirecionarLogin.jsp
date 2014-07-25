<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Redirecionamento para a pagina de Login</title>
</head>
<script type="text/javascript">
/*
 * Esse script foi implementado para o redirecionamento para a tela de login
 * caso o usuario tenha a sessao expirada, pois, ao clicarmos em qualquer link do menu,
 * o conteudo da tela de login era inserida no iframe da tela principal, ja que todos 
 * os itens do menu, ao serem clicados, redirecionam o conteudo da resposta do servidor
 * para o iframe, sendo assim, tivemos que criar uma instrucao para que a tela de menu aponte
 * para a URL da tela de login.  
 */
function recarregarMenu() {
	window.top.location.href='<c:url value="/login"/>';
	
}
recarregarMenu();
</script>
<body>

</body>
</html>