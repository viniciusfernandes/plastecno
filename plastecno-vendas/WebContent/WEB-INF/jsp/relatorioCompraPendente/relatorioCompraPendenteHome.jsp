<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Relatório de Compras Pendentes</title>
<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>

<script type="text/javascript">

$(document).ready(function() {
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>


	<form action="<c:url value="/pedido/compra/recepcao"/>" method="get">
		<fieldset>
			<legend>::: Pedidos de Compra em Pendência :::</legend>
			<div class="label" style="width: 30%">Data Inícial:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataInicial" name="dataInicial"
					value="${dataInicial}" maxlength="10" class="pesquisavel" />
			</div>

			<div class="label" style="width: 10%">Data Final:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataFinal" name="dataFinal"
					value="${dataFinal}" maxlength="100" class="pesquisavel" />
			</div>
			<div class="label" style="width: 30%">Representada:</div>
			<div class="input" style="width: 40%">
				<select name="idRepresentada" style="width: 70%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="representada" items="${listaRepresentada}">
						<option value="${representada.id}"
							<c:if test="${representada eq representadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar" /> <input
				id="botaoLimpar" type="button" value=""
				title="Limpar Dados de Geração do Relatório de Vendas"
				class="botaoLimpar" />
		</div>
	</form>
	
	<a id="rodape"></a>
	<c:if test="${true}">
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">Num. Pedido</th>
					<th style="width: 60%">Desc. Item</th>
					<th style="width: 10%">Comprador</th>
					<th style="width: 10%">Represent.</th>
					<th style="width: 5%">Valor (R$)</th>
					<th style="width: 5%">Ação</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.lista}" var="representada"
					varStatus="iteracaoRepresentada">
					<!-- bloco de totalizacao de valores vendidos para cada representada -->
					<c:forEach items="${representada.listaVenda}" var="venda"
						varStatus="iteracaoVenda">
						<tr>
							<c:if test="${iteracaoVenda.count eq 1}">
								<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}"
									rowspan="${representada.numeroVendas + 1}">${representada.nome}</td>
							</c:if>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${venda.dataEnvio}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${venda.numeroPedido}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${venda.nomeCliente}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${venda.valorVendaFormatado}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">
								<form action="<c:url value="/pedido/pdf"/>">
									<input type="hidden" name="idPedido"
										value="${venda.numeroPedido}" /> <input type="submit" value=""
										title="Visualizar Pedido PDF" class="botaoPDF"
										style="border: none;" />
								</form>
							</td>

						</tr>
					</c:forEach>

					<tr>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}"
							style="border-right: none;"></td>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}"
							style="border-left: none;"></td>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}"
							style="font-weight: bold;">TOTAL (R$)</td>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}"
							style="font-weight: bold;">${representada.valorVendaTotalFormatado}</td>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}"
							style="border-left: none;"></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

</body>
</html>