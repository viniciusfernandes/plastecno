<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>


<title>Relatório de Valores Totais por Período</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/pedido/periodo"/>">
		<input type="hidden" name="isCompra" value="${isCompra}"/>
	</form>

	<form action="<c:url value="/relatorio/pedido/periodo/listagem"/>" method="get">
		<input type="hidden" name="isCompra" value="${isCompra}"/>
		<fieldset>
			<legend>::: Relatório de Valores de ${isCompra ? 'Compras' : 'Vendas'} por Período :::</legend>
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
			<input type="submit" value="" class="botaoPesquisar" /> <input
				id="botaoLimpar" type="button" value=""
				title="Limpar Dados de Geração do Relatório de ${isCompra ? 'Compras' : 'Vendas'} por Período"
				class="botaoLimpar" />
		</div>
	</form>

	<a id="rodape"></a>
	<c:if test="${not empty relatorio}">
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 40%">${isCompra ? 'Comprador' : 'Vendedor'}</th>
					<th style="width: 30%">${isCompra ? 'Fornecedor' : 'Representada'}</th>
					<th style="width: 15%">Valor (R$)</th>
					<th style="width: 15%">Valor IPI (R$)</th>
				</tr>
			</thead>
			<tbody>
			
			<c:forEach items="${relatorio.listaGrupo}" var="grupo" varStatus="iGrupo">
					<c:forEach items="${grupo.listaElemento}" var="elemento" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento + 1}">${elemento.nomeProprietario}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.nomeFantasiaRepresentada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.valorTotalFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.valorTotalIPIFormatado}</td>
						</tr>
					</c:forEach>
					<tr>
						<td class="total${iGrupo.index % 2 == 0 ? 1 : 2}" style="font-weight: bold;">TOTAL (R$)</td>
						<td class="total${iGrupo.index % 2 == 0 ? 1 : 2}" style="font-weight: bold;">${grupo.propriedades['valorTotalFormatado']}</td>
						<td class="total${iGrupo.index % 2 == 0 ? 1 : 2}" style="font-weight: bold;">${grupo.propriedades['valorTotalIPIFormatado']}</td>
					</tr>
			</c:forEach>
			
			</tbody>

		</table>

		<table class="listrada">
			<caption>Total de ${isCompra ? 'Compras por Fornecedor' : 'Vendas por Representada'}</caption>
			<thead>
				<tr>
					<th>${isCompra ? 'Fornecedor' : 'Representada'}</th>
					<th>Total (R$)</th>
					<th>Total IPI (R$)</th>
				</tr>
			</thead>
			<c:forEach items="${relatorio.listaElemento}" var="elemento">
				<tr>
					<td>${elemento.nomeFantasiaRepresentada}</td>
					<td>${elemento.valorTotalFormatado}</td>
					<td>${elemento.valorTotalIPIFormatado}</td>
				</tr>
			</c:forEach>

			<tfoot>
				<tr>
					<th>TOTAL GERAL (R$)</th>
					<th>${relatorio.totalGeralFormatado}</th>
					<th>${relatorio.totalGeralIPIFormatado}</th>
				</tr>
				<tr>
					<th>VALOR IPI (R$)</th>
					<th>${relatorio.valorIPIFormatado}</th>
					<th></th>
				</tr>
			</tfoot>
		</table>
	</c:if>
</body>
</html>