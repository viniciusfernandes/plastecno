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
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>

<title>Relatório de Vendas do Vendedor</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		
		$('#botaoPesquisar').click(function () {
			var parametros = serializarBloco('bloco_pesquisa');
			var action = '<c:url value="/relatorio/venda/vendedor/listagem"/>?'+parametros;
			$('#formVazio').attr('action', action).attr('method', 'post').submit();
		});
		
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
		
		autocompletar(
				{
					url: '<c:url value="/relatorio/venda/vendedor/nome"/>',
					campoPesquisavel: 'nomeVendedor', 
					parametro: 'nome', 
					containerResultados: 'containerPesquisaVendedor',
					selecionarItem: function (itemSelecionado){
						$('#idVendedor').val(itemSelecionado.id);
						$('#botaoPesquisar').click();
					}
				}
		);
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/venda/vendedor"/>">
	</form>

		<fieldset id="bloco_pesquisa">
			<legend>::: ${titulo} :::</legend>
			
			<input type="hidden" id="idVendedor" name="idVendedor" value="${vendedor.id}" />
			
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
			<div class="label obrigatorio" style="width: 30%">Vendedor:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="nomeVendedor" value="${vendedor.nomeCompleto}"
					class="pesquisavel <c:if test="${not acessoPesquisaVendaVendedorPermitido}">desabilidado</c:if>"
					<c:if test="${not acessoPesquisaVendaVendedorPermitido}">disabled="disabled"</c:if> style="width: 50%" />
				
				<div class="suggestionsBox" id="containerPesquisaVendedor"
					style="display: none; width: 50%"></div>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input id="botaoPesquisar" type="button" class="botaoPesquisar" /> 
			<input id="botaoLimpar" type="button" title="Limpar Dados de Geração do Relatório de Pedido do Vendedor" class="botaoLimpar" />
		</div>

	<a id="rodape"></a>
	<c:if test="${relatorioGerado}">
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 15%">Representada</th>
					<th style="width: 10%">Dt. Envio</th>
					<th style="width: 10%">No. Pedido</th>
					<th style="width: 40%">Cliente</th>
					<th style="width: 10%">Valor (R$)</th>
					<th style="width: 5%">PDF</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.listaRepresentada}" var="representada"
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
								
							<form action="<c:url value="${orcamento ? '/orcamento/pdf': '/pedido/pdf'}"/>">
								<input type="hidden" name="idPedido" value="${venda.numeroPedido}" />
								<div class="input" style="width: 40%">
									<input type="submit" value="" title="Visualizar Pedido/Orçamto PDF" class="botaoPDFPequeno" style="border: none;" />
								</div>
							</form>
							<form action="<c:url value="${orcamento ? '/orcamento/' : '/pedido/'}${venda.numeroPedido}"/>">
								<div class="input" style="width: 40%">
									<input type="submit" value="" title="Editar pedido/Orçamto" class="botaoEditar" style="border: none;" />
								</div>
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
			<tfoot>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td>TOTAL GERAL (R$):</td>
					<td>${relatorio.totalVendidoFormatado}</td>
					<td></td>
				</tr>
			</tfoot>
		</table>
	</c:if>
</body>
</html>