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

<title>Relat�rio de Acompanhamento de Entregas</title>
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
	<form id="formVazio" action="<c:url value="/relatorio/entrega"/>"
		method="get"></form>

	<form action="<c:url value="/relatorio/entrega/listagem"/>"
		method="get">
		<fieldset>
			<legend>::: Dados do Relat�rio de Entregas :::</legend>
			<div class="label obrigatorio" style="width: 30%">Data In�cial:</div>
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
				title="Limpar Dados de Gera��o do Relat�rio de Entregas"
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
					<th>A��es</th>
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
								<form action="<c:url value="/pedido/${pedido.id}"/>"
									method="get">
									<input type="submit" title="Vizualizar Dados do Pedido"
										value="" class="botaoEditar" />
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