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
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>

<title>Relatório de Faturamento</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		
		$('#botaoPesquisar').click(function () {
			$('#formPesquisa').submit();
		});
		
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/faturamento"/>">
	</form>

		<fieldset>
			<legend>::: Relatório de Faturamento :::</legend>
	
			<form id="formPesquisa" action="<c:url value="/relatorio/faturamento/listagem"/>" method="get">
				<div class="label obrigatorio" style="width: 30%">Data Inícial:</div>
				<div class="input" style="width: 15%">
					<input type="text" id="dataInicial" name="dataInicial"
						value="${dataInicial}" maxlength="10" class="pesquisavel" />
				</div>
	
				<div class="label obrigatorio" style="width: 15%">Data Final:</div>
				<div class="input" >
					<input type="text" id="dataFinal" name="dataFinal"
						value="${dataFinal}" maxlength="100" class="pesquisavel" />
				</div>
			</form>
			<div class="label" style="width: 30%">Valor Vendido (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${faturamento.valorVendidoFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 15%">Valor Comprado (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${faturamento.valorVendidoFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 30%">Débito IPI (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${faturamento.valorDebitoIPIFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 15%">Crédito IPI (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${faturamento.valorCreditoIPIFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 30%">Valor IPI (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${faturamento.valorIPIFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 15%">Valor ICMS (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${faturamento.valorICMSFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 30%">Faturamento (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${faturamento.valorFaturadoFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input id="botaoPesquisar" type="button" value="" class="botaoPesquisar" title="Pesquisar Faturamento" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Faturamento" class="botaoLimpar" />
		</div>
</body>
</html>