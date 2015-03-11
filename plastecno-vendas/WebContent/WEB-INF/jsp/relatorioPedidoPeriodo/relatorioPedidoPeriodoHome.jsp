<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />


<script type="text/javascript"
	src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>

<title>Relatório de Pedidos por Período</title>
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
	<form id="formVazio" action="<c:url value="/relatorio/pedido"/>" method="get">
		<input type="hidden" name="isCompra" value="${isCompra}"/>
	</form>

	<form action="<c:url value="/relatorio/pedido/listagem"/>" method="get">
		<input type="hidden" name="isCompra" value="${isCompra}"/>
		<input type="hidden" name="isEntrega" value="${isEntrega}"/>
		<fieldset>
			<legend>::: ${titulo} :::</legend>
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
				title="Limpar Dados de Geração do Relatório de Entregas"
				class="botaoLimpar" />
		</div>
	</form>

	<a id="rodape"></a>
	<c:if test="${relatorioGerado}">
		<table class="listrada">
			<caption>${tituloRelatorio}</caption>
			<thead>
				<tr>
					<th style="width: 15%">Data Entrega</th>
					<th style="width: 15%">Num. Pedido</th>
					<th style="width: 15%">Representada</th>
					<th style="width: 35%">Cliente</th>
					<th style="width: 15%">Valor (R$)</th>
					<th>Ações</th>
				</tr>
			</thead>

			<tbody>
				<c:forEach var="pedido" items="${listaPedido}">
					<tr>
						<td>${pedido.dataEntregaFormatada}</td>
						<td>${pedido.id}</td>
						<td>${pedido.representada.nomeFantasia}</td>
						<td>${pedido.cliente.nomeFantasia}-
							${pedido.cliente.razaoSocial}</td>
						<td>${pedido.valorPedidoFormatado}</td>
						<td>
							<div class="coluna_acoes_listagem">
								<form action="<c:url value="/relatorio/pedido/pdf"/>" method="get">
									<input type="hidden" name="idPedido" value="${pedido.id}"/>
									<input type="hidden" name="tipoPedido" value="${pedido.tipoPedido}"/>
									<input type="submit" title="Vizualizar Pedido PDF" value="" class="botaoPDF" />
								</form>
							</div>
						</td>
					</tr>

				</c:forEach>
			</tbody>
		</table>
	</c:if>

</body>
</html>