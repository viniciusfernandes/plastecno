<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Recepção de Compras</title>
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
			<caption>XXXXXXXXXXx xxxxxxxxxxxxxxxxxxx${relatorio.titulo}</caption>
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
				<c:forEach items="${relatorio.listaVendedor}" var="vendedor"
					varStatus="iteracaoVendedor">
					<c:forEach items="${vendedor.listaRepresentada}" var="representada"
						varStatus="iteracaoRepresentada">
						<tr>
							<c:if test="${iteracaoRepresentada.count eq 1}">
								<td class="fundo${iteracaoVendedor.index % 2 == 0 ? 1 : 2}"
									rowspan="${vendedor.numeroVendas + 1}">${vendedor.nomeVendedor}</td>
							</c:if>
							<td class="fundo${iteracaoVendedor.index % 2 == 0 ? 1 : 2}">${representada.nome}</td>
							<td class="fundo${iteracaoVendedor.index % 2 == 0 ? 1 : 2}">${representada.valorVendaFormatado}</td>
						</tr>
					</c:forEach>

					<tr>
						<td class="total${iteracaoVendedor.index % 2 == 0 ? 1 : 2}"
							style="font-weight: bold;">TOTAL (R$)</td>
						<td class="total${iteracaoVendedor.index % 2 == 0 ? 1 : 2}"
							style="font-weight: bold;">${vendedor.totalVendidoFormatado}</td>
					</tr>
				</c:forEach>

			</tbody>

		</table>
	</c:if>

</body>
</html>