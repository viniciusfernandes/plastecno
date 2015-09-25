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


<title>Relat�rio das Comiss�es dos Vendedores</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
		
		$('#botaoPesquisar').click(function () {
			var parametros = serializarBloco('bloco_pesquisa');
			var action = '<c:url value="/relatorio/comissao/vendedor/listagem"/>?'+parametros;
			$('#formVazio').attr('action', action).attr('method', 'post').submit();
		});
		
		autocompletar({
			url : '<c:url value="/vendedor/listagem/nome"/>',
			campoPesquisavel : 'nome',
			parametro : 'nome',
			containerResultados : 'containerPesquisaVendedor',
			selecionarItem: function(itemLista) {
				$('#idVendedor').val(itemLista.id);
				$('#botaoPesquisar').click();
			}
		});
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/comissao/vendedor"/>">
	</form>

		<fieldset id="bloco_pesquisa">
			<input type="hidden" id="idVendedor" name="vendedor.id" value="${vendedor.id}" />
		
			<legend>::: Relat�rio das Comiss�es do Vendedor :::</legend>
			<div class="label obrigatorio" style="width: 30%">Data In�cial:</div>
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
			<div class="label" style="width: 30%">Vendedor:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="nome" name="vendedor.nome" value="${vendedor.nome}"
					class="pesquisavel <c:if test="${not acessoPesquisaComissaoPermitido}">desabilitado</c:if>" 
					<c:if test="${not acessoPesquisaComissaoPermitido}">disabled="disabled"</c:if> style="width: 50%" />
				<div class="suggestionsBox" id="containerPesquisaVendedor" style="display: none; width: 50%"></div>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input id="botaoPesquisar" type="button" class="botaoPesquisar" title="Pesquisar Dados da Comiss�o do Vendedor" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados da Comiss�o do Vendedor" class="botaoLimpar" />
		</div>

	<a id="rodape"></a>
		<c:choose>
		<c:when  test="${not empty relatorio and not isRelatorioVendedores}">
		<table id="tabelaItemPedido" class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">Pedido</th>
					<th style="width: 2%">Item</th>
					<th style="width: 5%">Qtde.</th>
					<th style="width: 50%">Descri��o</th>
					<th style="width: 20%">Venda (R$)</th>
					<th style="width: 10%">Valor Comiss. (R$)</th>
					<th style="width: 5%">A��es</th>
				</tr>
			</thead>

			<tbody>
			
			<c:forEach items="${relatorio.listaGrupo}" var="pedido" varStatus="iGrupo">
					<c:forEach items="${pedido.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}">${pedido.id}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.sequencial}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoItemFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.valorComissionadoFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
							<div class="coluna_acoes_listagem">
								<form action="<c:url value="/relatorio/comissao/pedido/pdf"/>" method="get">
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
					<td colspan="3"></td>
					<td colspan="2" style="text-align: right;">TOTAL COMISSIONADO:</td>
					<td colspan="4"><div id="valorPedido"
							style="text-align: left;">R$ ${relatorio.valorTotal}</div></td>
				</tr>
			</tfoot>
		</table>
		</c:when>
		<c:when test="${not empty relatorio and isRelatorioVendedores}">
		<table id="tabelaItemPedido" class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 50%">Vendedor</th>
					<th style="width: 10%">Qtde.</th>
					<th style="width: 25%">Total (R$)</th>
					<th style="width: 15%">Valor Comiss. (R$)</th>
				</tr>
			</thead>

			<tbody>
			
				<c:forEach items="${relatorio.listaElemento}" var="comissao" varStatus="iGrupo">
						<tr>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${comissao.nomeVendedor}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${comissao.quantidadeVendida}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${comissao.valorVendidoFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${comissao.valorComissaoFormatado}</td>
						</tr>
					</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"></td>
					<td style="text-align: right;">TOTAL COMISSIONADO:</td>
					<td ><div id="valorPedido" style="text-align: left;">R$ ${relatorio.valorTotal}</div></td>
				</tr>
			</tfoot>
		</table>
		</c:when>
		</c:choose>
</body>
</html>