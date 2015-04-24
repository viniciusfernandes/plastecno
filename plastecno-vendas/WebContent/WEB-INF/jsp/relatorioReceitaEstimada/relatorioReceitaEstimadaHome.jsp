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

<title>Relatório de Receita</title>
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
	<form id="formVazio" action="<c:url value="/relatorio/receita"/>">
	</form>

		<fieldset>
			<legend>::: Relatório de Receita :::</legend>
	
			<form id="formPesquisa" action="<c:url value="/relatorio/receita/listagem"/>" method="get">
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
			<div class="label" style="width: 30%">Receita (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorReceitaFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 15%">Líquido (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorLiquidoFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 30%">Valor Vendido (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorVendidoFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 15%">Valor Comprado (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorCompradoFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 30%">Débito ICMS (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorDebitoICMSFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 15%">Crédito ICMS (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorCreditoICMSFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 30%">Débito IPI (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorDebitoIPIFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 15%">Crédito IPI (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorCreditoIPIFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			
			<div class="label" style="width: 30%">Valor IPI (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorIPIFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 15%">Valor ICMS (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorICMSFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			<div class="label" style="width: 30%">Comissão (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" value="${receita.valorComissionadoFormatado}" maxlength="10" class="desabilitado" disabled="disabled"/>
			</div>
			
		</fieldset>
		<div class="bloco_botoes">
			<input id="botaoPesquisar" type="button" value="" class="botaoPesquisar" title="Pesquisar Receita" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Receita" class="botaoLimpar" />
		</div>
</body>
</html>