<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />


<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>

<title>Relatório de Faturamento</title>
<script type="text/javascript">
$(document).ready(function() {
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
});

</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/faturamento"/>" method="get">
	</form>

	<form action="<c:url value="/relatorio/faturamento/pdf"/>" method="get">
		<fieldset>
			<legend>::: Relatório de Faturamento :::</legend>
			<div class="label obrigatorio" style="width: 30%">Data Inícial:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataInicial" name="dataInicial"
					value="${dataInicial}" maxlength="10" class="pesquisavel" />
			</div>

			<div class="label obrigatorio" style="width: 10%">Data Final:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataFinal" name="dataFinal"
					value="${dataFinal}" maxlength="100" class="pesquisavel" />
			</div>

		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPDF" /> 
		</div>
	</form>
</body>
</html>