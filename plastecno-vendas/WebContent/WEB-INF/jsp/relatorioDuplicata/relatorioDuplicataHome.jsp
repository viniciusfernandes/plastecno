<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />
<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>

<title>Relatório das Duplicatas</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
	
		autocompletar({
			url : '<c:url value="/duplicata/cliente/listagem/nome"/>',
			campoPesquisavel : 'nomeCliente',
			parametro : 'nomeCliente',
			containerResultados : 'containerPesquisaCliente',
			selecionarItem: function(itemLista) {
				var form = document.getElementById('formVazio');
				adicionarInputHiddenFormulario('formVazio', 'nomeCliente', document.getElementById('nomeCliente').value);
				form.action = '<c:url value="/relatorio/duplicata/listagem/cliente/"/>'+itemLista.id;
				form.method = 'get';
				form.submit();
			}
		});

	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
	
	});

function alterarDuplicata(botao, metodo, acao){
	$(botao).closest('form').attr('action', acao).attr('method', metodo).submit();
};


function removerDuplicata(botao, metodo, acao){
	inicializarModalConfirmacao({
			mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja REMOVER esse item?',
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
						value="${dataInicial}" class="pesquisavel" />
				</div>
				<div class="label" style="width: 10%">Data Final:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="dataFinal" name="dataFinal"
						value="${dataFinal}" class="pesquisavel"
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
				<div class="input" style="width: 2%">
					<input type="submit" id="botaoPesquisaPedido" title="Pesquisar Duplicatas do Pedido" value="" class="botaoPesquisarPequeno" />
				</div>
			</form>
			<div class="label" style="width: 8%">Cliente:</div>
			<div class="input" style="width: 45%">
				<input type="text" id="nomeCliente" value="${nomeCliente}" class="pesquisavel"  style="width: 50%"/>
				<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
			</div>
			
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

	<c:if test="${not empty duplicata}">
	<fieldset id="bloco_edicao_duplicata">
		<legend>::: Relatório das Duplicatas :::</legend>
		<form action="<c:url value="/duplicata/alteracao"/>" method="post">
			<input type="hidden" name="duplicata.id" value="${duplicata.id}"/>
			<input type="hidden" name="dataInicial" value="${dataInicial}"/>
			<input type="hidden" name="dataFinal" value="${dataFinal}"/>
			<div class="label">Dt Venc.:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataVencimento" name="duplicata.dataVencimento" value="${duplicata.dataVencimentoFormatada}" />
			</div>
			<div class="label" style="width: 10%">Vl. (R$):</div>
			<div class="input" style="width: 55%">
				<input type="text" id="valor" name="duplicata.valor" value="${duplicata.valor}" style="width: 25%"/>
			</div>
			<div class="label">Banco:</div>
			<div class="input" style="width: 15%">
				<select name="duplicata.codigoBanco">
					<option></option>
					<c:forEach items="${listaBanco}" var="banco">
						<option value="${banco.codigo}" <c:if test="${banco.codigo eq duplicata.codigoBanco}">selected</c:if> >${banco.nome}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label" style="width: 10%">Situação:</div>
			<div class="input" style="width: 55%">
				<select name="duplicata.tipoSituacaoDuplicata" style="width: 25%">
					<option></option>
					<c:forEach items="${listaTipoSituacao}" var="tipo">
						<option value="${tipo}" <c:if test="${tipo eq duplicata.tipoSituacaoDuplicata}">selected</c:if> >${tipo.descricao}</option>
					</c:forEach>
				</select>
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
					<th style="width: 42%">Cliente</th>
					<th style="width: 8%">NFe</th>
					<th style="width: 12%">Banco</th>
					<th style="width: 10%">Vl. (R$)</th>
					<th style="width: 5%">Parc.</th>
					<th style="width: 10%">Situação</th>
					<th style="width: 8%">Ações</th>
				</tr>
			</thead>
			
			<tbody>
			
			<c:forEach items="${relatorio.listaGrupo}" var="grupo" varStatus="iGrupo">
					<c:forEach items="${grupo.listaElemento}" var="elemento" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento + 2}">${grupo.propriedades['dataVencimentoFormatada']}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.nomeCliente}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.numeroNFe}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.nomeBanco}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.valor}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.parcelaFormatada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.tipoSituacaoDuplicata.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form>
										<input type="hidden" name="dataInicial" value="${dataInicial}"/>
										<input type="hidden" name="dataFinal" value="${dataFinal}"/>
										<div class="input" style="width: 33%">
											<input type="button" title="Editar Duplicata" value="" class="botaoEditar" 
												onclick="alterarDuplicata(this, 'get', '<c:url value="/duplicata/${elemento.id}"/>')"/>
										</div>
										<c:choose>
											<c:when test="${not elemento.liquidado}">
												<div class="input" style="width: 33%">
													<input type="button" title="Liquidação Duplicata" value="" class="botaoVerificacaoEfetuadaPequeno" 
														onclick="alterarDuplicata(this, 'post', '<c:url value="/duplicata/liquidacao/${elemento.id}"/>')"/>
												</div>
											</c:when>
											<c:otherwise>
												<div class="input" style="width: 33%">
													<input type="button" title="Cancelar Liquidação Duplicata" value="" class="botaoVerificacaoFalhaPequeno" 
														onclick="alterarDuplicata(this, 'post', '<c:url value="/duplicata/liquidacao/cancelamento/${elemento.id}"/>')"/>
												</div>
											</c:otherwise>
										</c:choose>
										<div class="input" style="width: 33%">
											<input type="button" title="Remover Duplicata" value="" class="botaoRemover" 
												onclick="removerDuplicata(this, 'post', '<c:url value="/duplicata/remocao/${elemento.id}"/>')"/>
										</div>
									</form>
								</div>
							</td>
						</tr>
					</c:forEach>
						<tr>
							<td colspan="7" style="text-align: center; border-bottom: 0px; font-size: 10px" class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">TOTAL R$ ${grupo.propriedades['totDia']}</td>
						</tr>
						<tr>
							<td colspan="7" style="text-align: center; border-top: 0px; font-size: 10px" class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">A RECEBER R$ ${grupo.propriedades['totReceberDia']}</td>
						</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: right;">QTDE.:</td>
					<td colspan="5"><div id="valorPedido"
							style="text-align: left;">${relatorio.propriedades['qtde']}</div></td>
				</tr>
				<tr>
					<td colspan="3" style="text-align: right;">TOTAL:</td>
					<td colspan="5"><div id="valorPedidoIPI"
							style="text-align: left;">R$ ${relatorio.propriedades['tot']}</div></td>
				</tr>
				<tr>
					<td colspan="3" style="text-align: right;">A RECEBER:</td>
					<td colspan="5"><div id="valorTotalSemFrete"
							style="text-align: left;">R$ ${relatorio.propriedades['totReceber']}</div></td>
				</tr>
			</tfoot>
		</table>
		</c:if>
</body>
</html>