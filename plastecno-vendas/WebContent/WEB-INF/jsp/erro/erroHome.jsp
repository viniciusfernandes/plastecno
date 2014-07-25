<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title></title>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/mensagem.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/botao.css"/>" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript">
$(document).ready(function() {
	scrollTo('topo');
});
</script>
</head>
<body>
<jsp:include page="/bloco/bloco_mensagem.jsp" />

<div class="bloco_botoes">
	<a onclick="javascript:history.back()" title="Voltar para Pagina Anterior" class="botaoVoltar"></a>
</div>
</body>
</html>

