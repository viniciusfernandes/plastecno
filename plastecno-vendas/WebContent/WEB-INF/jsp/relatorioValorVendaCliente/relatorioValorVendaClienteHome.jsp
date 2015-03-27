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

<title>Relatório de Vendas para o Cliente</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
		
		autocompletar({
			url : '<c:url value="/cliente/listagem/nome"/>',
			campoPesquisavel : 'nomeFantasia',
			parametro : 'nomeFantasia',
			containerResultados : 'containerPesquisaCliente',
			selecionarItem: function(itemLista) {
				$('#idCliente').val(itemLista.id);
			}
		});
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/venda/cliente"/>">
	</form>

	<form action="<c:url value="/relatorio/venda/cliente/listagem"/>">
		<input type="hidden" id="idCliente" name="cliente.id" value="${cliente.id}" />

		<fieldset>
			<legend>::: Relatório de Vendas para Cliente :::</legend>
			<div class="label" style="width: 30%">Pesquisar Orçamentos:</div>
			<div class="input" style="width: 60%">
				<input type="checkbox" name="orcamento"
					<c:if test="${orcamento}">checked</c:if> class="checkbox" />
			</div>
			<div class="label obrigatorio" style="width: 30%">Data Inícial:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataInicial" name="dataInicial"
					value="${dataInicial}" maxlength="10" class="pesquisavel" />
			</div>

			<div class="label obrigatorio" style="width: 10%">Data Final:</div>
			<div class="input" style="width: 35%">
				<input type="text" id="dataFinal" name="dataFinal"
					value="${dataFinal}" maxlength="100" class="pesquisavel"
					style="width: 45%" />
			</div>
			<div class="label" style="width: 30%">Cliente:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="nomeFantasia" name="cliente.nomeFantasia" value="${cliente.nomeFantasia}"
					class="pesquisavel <c:if test="${not acessoPesquisaVendaClientePermitido}">desabilidado</c:if>"
					<c:if test="${not acessoPesquisaVendaClientePermitido}">disabled="disabled"</c:if> style="width: 50%" />
				<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Pedido do Cliente"
				class="botaoLimpar" />
		</div>
	</form>

	<a id="rodape"></a>
	<c:if test="${relatorioGerado}">
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 40%">Cliente</th>
					<th style="width: 15%">Qtde.</th>
					<th style="width: 35%">Valor (R$)</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.listaGrupo}" var="grupo" varStatus="iteracaoTotalizacao">
						<tr>
							<td class="fundo${iteracaoTotalizacao.index % 2 == 0 ? 1 : 2}">${grupo.firstElement.nomeCliente}</td>
							<td class="fundo${iteracaoTotalizacao.index % 2 == 0 ? 1 : 2}">${grupo.firstElement.quantidade}</td>
							<td class="fundo${iteracaoTotalizacao.index % 2 == 0 ? 1 : 2}">${grupo.firstElement.valorTotalFormatado}</td>
						</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td></td>
					<td>TOTAL GERAL (R$):</td>
					<td>${relatorio.valorTotal}</td>
				</tr>
			</tfoot>
		</table>
	</c:if>
</body>
</html>