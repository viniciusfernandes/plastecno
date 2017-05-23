<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />
<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>

<jsp:include page="/bloco/bloco_modal_js.jsp" />


<title>Relatório das Duplicatas</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
		inserirMascaraData('dataVencimento');
		inserirMascaraMonetaria('valor', 7);
	});

function alterarDuplicata(botao, metodo, acao, tipo){
	inicializarModalConfirmacao({
			mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja '+tipo+' esse item?',
			confirmar: function(){
				$(botao).closest('form').attr('action', acao).attr('method', metodo).submit();
			}
		});
};
</script>
</head>
<body>
	<div id="modal"></div>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/duplicata"/>">
	</form>

		<fieldset id="bloco_pesquisa">
			<legend>::: Relatório das Duplicatas :::</legend>
			<form action="<c:url value="/relatorio/duplicata/listagem"/>" method="get">
				<div class="label" style="width: 30%">Data Inícial:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="dataInicial" name="dataInicial"
						value="${dataInicial}" maxlength="10" class="pesquisavel" />
				</div>
				<div class="label" style="width: 10%">Data Final:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="dataFinal" name="dataFinal"
						value="${dataFinal}" maxlength="100" class="pesquisavel"
						style="width: 100%" />
				</div>
				<div class="input" style="width: 2%">
					<input type="submit" id="botaoPesquisarPeriodo" title="Pesquisar Duplicatas por Período" value="" class="botaoPesquisarPequeno" style="width: 100%"/>
				</div>
				<div class="input" style="width: 10%">
					<input type="button" id="botaoLimpar" value="" title="Limpar Dados das Duplicatas no Período" class="botaoLimparPequeno" />
				</div>
			</form>
			<form action="<c:url value="/relatorio/duplicata/listagem/pedido"/>">
				<div class="label" style="width: 30%">Pedido:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="idPedido" name="idPedido"
						value="${idPedido}" maxlength="10" class="pesquisavel" style="width: 100%"/>
				</div>
				<div class="input" style="width: 50%">
					<input type="submit" id="botaoPesquisaPedido" title="Pesquisar Duplicatas do Pedido" value="" class="botaoPesquisarPequeno" style="width: 5%"/>
				</div>
			</form>
			
			<form action="<c:url value="/relatorio/duplicata/listagem/nfe"/>">
				<div class="label" style="width: 30%">NFe:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="numeroNFe" name="numeroNFe"
						value="${numeroNFe}" maxlength="10" class="pesquisavel" style="width: 100%"/>
				</div>
				<div class="input" style="width: 50%">
					<input type="submit" id="botaoPesquisaNFe" title="Pesquisar Duplicatas da NFe" value="" class="botaoPesquisarPequeno" style="width: 5%"/>
				</div>
			</form>
		</fieldset>

	<c:if test="${not empty idDuplicata}">
	<fieldset id="bloco_edicao_duplicata">
		<legend>::: Relatório das Duplicatas :::</legend>
		<form action="<c:url value="/duplicata/alteracaodata"/>" method="post">
			<input type="hidden" name="idDuplicata" value="${idDuplicata}"/>
			<input type="hidden" name="dataInicial" value="${dataInicial}"/>
			<input type="hidden" name="dataFinal" value="${dataFinal}"/>
			<div class="label">Dt Venc.:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataVencimento" name="dataVencimento" value="${dataVencimento}" />
			</div>
			<div class="label">Vl. (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" id="valor" name="valor" value="${valor}"/>
			</div>
			<div class="bloco_botoes">
				<input type="submit" value="" class="botaoInserir" title="Alterar Data da Duplicata"/>
			</div>
		</form>
	</fieldset>
	</c:if>
	
	<a id="rodape"></a>
		<c:if test="${not empty relatorio}">
		<table id="tabelaItemPedido" class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">Dt. Venc.</th>
					<th style="width: 54%">Cliente</th>
					<th style="width: 8%">NFe</th>
					<th style="width: 10%">Vl. (R$)</th>
					<th style="width: 10%">Situação</th>
					<th style="width: 8%">Ações</th>
				</tr>
			</thead>
			
			<tbody>
			
			<c:forEach items="${relatorio.listaGrupo}" var="grupo" varStatus="iGrupo">
					<c:forEach items="${grupo.listaElemento}" var="elemento" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento}">${grupo.propriedades['dataVencimentoFormatada']}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.nomeCliente}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.numeroNFe}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.valor}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.tipoSituacaoDuplicata.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form>
										<input type="hidden" name="dataInicial" value="${dataInicial}"/>
										<input type="hidden" name="dataFinal" value="${dataFinal}"/>
										
										<input type="button" title="Editar Duplicata" value="" class="botaoEditar" 
											onclick="alterarDuplicata(this, 'get', '<c:url value="/duplicata/${elemento.id}"/>','ALTERAR')"/>
										<input type="button" title="Liquidar Duplicata" value="" class="botaoVerificarPequeno" 
											onclick="alterarDuplicata(this, 'post', '<c:url value="/duplicata/liquidacao/${elemento.id}"/>','LIQUIDAR')"/>
										<input type="button" title="Remover Duplicata" value="" class="botaoRemover" 
											onclick="alterarDuplicata(this, 'post', '<c:url value="/duplicata/remocao/${elemento.id}"/>','REMOVER')"/>
									</form>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>

			</tbody>
		</table>
		</c:if>
</body>
</html>