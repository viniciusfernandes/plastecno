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
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>


<title>Relatório das Duplicatas</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
		
		$('#botaoPesquisar').click(function () {
			var parametros = serializarBloco('bloco_pesquisa');
			var action = '<c:url value="/relatorio/duplicata/listagem"/>?'+parametros;
			$('#formVazio').attr('action', action).attr('method', 'post').submit();
		});
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/duplicata"/>">
	</form>

		<fieldset id="bloco_pesquisa">
		
			<legend>::: Relatório das Duplicatas :::</legend>
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
		</fieldset>
		<div class="bloco_botoes">
			<input id="botaoPesquisar" type="button" class="botaoPesquisar" title="Pesquisar Dados das Duplicatas no Período" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados das Duplicatas no Período" class="botaoLimpar" />
		</div>

	<a id="rodape"></a>
		<c:if test="${not empty relatorio}">
		<table id="tabelaItemPedido" class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">NFe</th>
					<th style="width: 15%">Data Venc.</th>
					<th style="width: 10%">Vl. (R$)</th>
					<th style="width: 15%">Situação</th>
					<th style="width: 5%">Ações</th>
				</tr>
			</thead>
			
			<tbody>
			
			<c:forEach items="${relatorio.listaGrupo}" var="grupo" varStatus="iGrupo">
					<c:forEach items="${pedido.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento}">${grupo.id}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.dataVencimentoFormatada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.valor}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.tipoSituacaoDuplicata}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/pedido/pdf"/>">
										<input type="hidden" name="idPedido" value="${pedido.id}"/>
										<input type="submit" title="Vizualizar Pedido PDF" value="" class="botaoPDF" />
									</form>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>

			</tbody>
			<tfoot>
				<tr>
					<td></td>
					<td colspan="1"></td>
					<td  style="text-align: right;">TOTAL COMISSIONADO:</td>
					<td colspan="2"><div id="valorPedido"
							style="text-align: left;">R$ ${relatorio.valorTotal}</div></td>
				</tr>
			</tfoot>
		</table>
		</c:if>
</body>
</html>