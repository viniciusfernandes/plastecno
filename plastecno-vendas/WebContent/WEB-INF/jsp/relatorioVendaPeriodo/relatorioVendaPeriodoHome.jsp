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

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>


<title>Relatório de Vendas por Período</title>
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
	<form id="formVazio" action="<c:url value="/relatorio/venda/periodo"/>" ></form>
	
	<form action="<c:url value="/relatorio/venda/periodo/listagem"/>" method="get">
		<fieldset>
			<legend>::: Relatório de Vendas por Período :::</legend>
					<div class="label obrigatorio" style="width: 30%">Data Inícial:</div>
					<div class="input" style="width: 15%">
						<input type="text" id="dataInicial" name="dataInicial" value="${dataInicial}" maxlength="10" class="pesquisavel" />
					</div>
		
					<div class="label obrigatorio" style="width: 10%">Data Final:</div>
					<div class="input" style="width: 15%">
						<input type="text" id="dataFinal" name="dataFinal" value="${dataFinal}" maxlength="100" class="pesquisavel"/>
					</div>
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar"/>
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Vendas" class="botaoLimpar"/>
		</div>
	</form>
	
	<a id="rodape"></a>
	<c:if test="${relatorioGerado}">
	<table class="listrada">
		<caption>${relatorio.titulo}</caption>
		<thead>
			<tr>
				<th style="width: 40%">Vendedor</th>
				<th style="width: 30%">Representada</th>
				<th>Valor Venda (R$)</th>
			</tr>			
		</thead>
		<tbody>
			<c:forEach items="${relatorio.listaVendedor}" var="vendedor" varStatus="iteracaoVendedor"> 
				<c:forEach items="${vendedor.listaRepresentada}" var="representada" varStatus="iteracaoRepresentada">
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
					<td class="total${iteracaoVendedor.index % 2 == 0 ? 1 : 2}" style="font-weight: bold;">TOTAL (R$)</td>
					<td class="total${iteracaoVendedor.index % 2 == 0 ? 1 : 2}" style="font-weight: bold;">${vendedor.totalVendidoFormatado}</td>
				</tr>
			</c:forEach>
			
		</tbody>
		
	</table>
	
	<table class="listrada">
		<caption>Total de Vendas por Representada</caption>
		<thead>
			<tr>
				<th>Representada</th>
				<th>Total (R$)</th>
			</tr>
		</thead>
		<c:forEach items="${relatorio.listaVendaRepresentada}" var="venda">
			<tr>
				<td>${venda.nomeRepresentada}</td>
				<td>${venda.valorVendaFormatado}</td>
			</tr>
		</c:forEach>
		
		<tfoot>
			<tr>
				<th>TOTAL GERAL (R$)</th>
				<th>${relatorio.valorTotalVendido}</th>
			</tr>
		</tfoot>
	</table>
	</c:if>
</body>
</html>