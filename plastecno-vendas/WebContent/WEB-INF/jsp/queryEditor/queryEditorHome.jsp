<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/geral.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/tabela.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/botao.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/formulario.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/mensagem.css"/>" />

<title>SQL Editor</title>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />

	<fieldset>
		<legend>::: SQL Editor :::</legend>
		<div class="label">Query:</div>
		<div class="input" style="width: 80%">
			<form action="<c:url value="/administracao/sql/execucao"/>"
				method="post">

				<textarea id="query" name="query" style="width: 100%; height: 150px"
					class="uppercaseBloqueado">${query}</textarea>
				<div class="bloco_botoes">
					<input type="submit" value="" class="botaoPesquisar"
						style="width: 5%" />
				</div>

			</form>
		</div>
		<div class="label">Resultado da Execução:</div>
		<div class="input" style="width: 80%">
			<textarea id="resultado" style="width: 100%; height: 400px"
				class="uppercaseBloqueado">${resultado}</textarea>
		</div>
	</fieldset>
</body>
</html>