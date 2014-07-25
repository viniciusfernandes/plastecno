<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" type="text/css" href="<c:url value="/css/geral.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/tabela.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/botao.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/mensagem.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/formulario.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui-1.10.3.custom.min.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/relatorio.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/autocomplete.css"/>" />


<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>

<title>Relatório de Vendas do Vendedor</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
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
					}
				}
		);
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/venda/vendedor"/>" ></form>
	
	<form action="<c:url value="/relatorio/venda/vendedor/listagem"/>" >
		<input type="hidden" id="idVendedor" name="idVendedor" value="${vendedor.id}"/>
		
		<fieldset>
			<legend>::: Relatório de Vendas do Vendedor :::</legend>
					<div class="label obrigatorio" style="width: 30%">Data Inícial:</div>
					<div class="input" style="width: 15%">
						<input type="text" id="dataInicial" name="dataInicial" value="${dataInicial}" maxlength="10" class="pesquisavel" />
					</div>
		
					<div class="label obrigatorio" style="width: 10%">Data Final:</div>
					<div class="input" style="width: 35%">
						<input type="text" id="dataFinal" name="dataFinal" value="${dataFinal}" maxlength="100" class="pesquisavel" style="width: 45%"/>
					</div>
					<div class="label obrigatorio" style="width: 30%">Vendedor:</div>
					<div class="input" style="width: 40%">
						<input type="text" id="nomeVendedor" value="${vendedor.nomeCompleto}" class="pesquisavel" style="width: 50%"/>
						<div class="suggestionsBox" id="containerPesquisaVendedor" style="display:none; width: 50%"></div>
					</div>
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar"/>
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Pedido do Vendedor" class="botaoLimpar"/>
		</div>
	</form>
	
	<a id="rodape"></a>
	<c:if test="${relatorioGerado}">
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 15%">Representada</th>
					<th style="width: 10%">Dt. Envio</th>
					<th style="width: 10%">No. Pedido</th>
					<th style="width: 45%">Cliente</th>
					<th style="width: 10%">Valor (R$) </th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.listaRepresentada}" var="representada" varStatus="iteracaoRepresentada"> 
					<!-- bloco de totalizacao de valores vendidos para cada representada -->		
					<c:forEach items="${representada.listaVenda}" var="venda" varStatus="iteracaoVenda">
						<tr>
							<c:if test="${iteracaoVenda.count eq 1}">
								<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}" 
									rowspan="${representada.numeroVendas + 1}">${representada.nome}</td>
							</c:if>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${venda.dataEnvio}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${venda.numeroPedido}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${venda.nomeCliente}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${venda.valorVendaFormatado}</td>
						</tr>
					</c:forEach>
					
					<tr>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}" style="border-right: none;"></td>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}" style="border-left: none;"></td>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}" style="font-weight: bold;">TOTAL (R$)</td>
						<td class="total${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}" style="font-weight: bold;">${representada.valorVendaTotalFormatado}</td>
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
				</tr>
			</tfoot>
		</table>
	</c:if>
</body>
</html>