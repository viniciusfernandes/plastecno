<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE >
<html>
<head>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');
	$('#botaoInserirPagamento').click(function(){
		if(isEmpty($('#idPagamento').val())){
			return;
		}
		adicionarInputHiddenFormulario('formPagamento', 'dataInicial', $('#dataInicial').val());
		adicionarInputHiddenFormulario('formPagamento', 'dataFinal', $('#dataFinal').val());
		$('#formPagamento').attr('action', '<c:out value="pagamento/inclusao"/>').attr('method', 'post').submit();	
	});
	
	$('#botaoRemoverPagamento').click(function(){
		var idPag = $('#idPagamento').val();
		if(isEmpty(idPag)){
			return;
		}
		adicionarInputHiddenFormulario('formVazio', 'idPagamento', idPag);
		adicionarInputHiddenFormulario('formVazio', 'dataInicial', $('#dataInicial').val());
		adicionarInputHiddenFormulario('formVazio', 'dataFinal', $('#dataFinal').val());
		$('#formVazio').attr('action', '<c:out value="pagamento/remocao"/>').attr('method', 'post').submit();	
	});
	
	$('#botaoLimparPeriodo').click(function(){
		$(this).closest('form').attr('action', '<c:out value="pagamento"/>').attr('method', 'get').submit();	
	});
	
	$('#botaoLimparPagamento').click(function(){
		$('#formPagamento input:text').val('');
		$('#formPagamento input:hidden').val('');
		$('#formPagamento select').val('');	
	});
	
	inserirMascaraData('dataVencimento');
	inserirMascaraData('dataEmissao');
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
	
	inserirMascaraData('dataRecebimento');
	inserirMascaraMonetaria('valorNF', 7);
	inserirMascaraMonetaria('valor', 7);
	inserirMascaraMonetaria('valorCreditoICMS', 7);
	inserirMascaraNumerica('quantidade', '99999');
	inserirMascaraNumerica('parcela', '999');
	inserirMascaraNumerica('pedido', '9999999');
	inserirMascaraNumerica('totalParcelas', '99999');
	inserirMascaraNumerica('numeroNF', '9999999');
	inserirMascaraNumerica('sequencial', '99');
	
	autocompletar({
		url : '<c:url value="/pagamento/fornecedor/listagem"/>',
		campoPesquisavel : 'fornecedor',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaFornecedor',
		selecionarItem : function(itemLista) {
			$('#idFornecedor').val(itemLista.id);
		}
	});
	
});

</script>

</head>
<body>

	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
	
	<fieldset id="bloco_pesquisa">
			<legend>::: Pagamentos do Período :::</legend>
			<form action="<c:url value="/pagamento/periodo/listagem"/>" method="get">
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
					<input type="submit" id="botaoPesquisarPagamentoPeriodo" title="Pesquisar Pagamentos por Período" value="" class="botaoPesquisarPequeno" style="width: 100%"/>
				</div>
				<div class="input" style="width: 10%">
					<input type="button" id="botaoLimparPeriodo" value="" title="Limpar Pagamentos do Período" class="botaoLimparPequeno" />
				</div>
			</form>
		</fieldset>
		
	<form id="formVazio"></form>
	
	<form id="formPagamento" action="<c:url value="/pagamento/inclusao"/>" method="post">
		<input type="hidden" id="idFornecedor" name="pagamento.idFornecedor" value="${pagamento.idFornecedor}"/>
		<input type="hidden" id="idPagamento" name="pagamento.id" value="${pagamento.id}"/>
		
	<fieldset>
		<legend>Pagamento</legend>
		<div class="label">Dt. Venc.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataVencimento" name="pagamento.dataVencimento" value="${pagamento.dataVencimentoFormatada}" style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Tipo:</div>
		<div class="input" style="width: 13%">
			<select id="tipoPagamento" name="pagamento.tipoPagamento" style="width: 100%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="tipoPagamento" items="${listaTipoPagamento}">
					<option value="${tipoPagamento}"
						<c:if test="${tipoPagamento eq pagamento.tipoPagamento}">selected</c:if>>${tipoPagamento}</option>
				</c:forEach>
			</select>
		</div>
		<div class="label" style="width: 10%">Situação:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="situacao" value="${pagamento.situacaoPagamento}" class="desabilitado" disabled="disabled" style="width: 75%"/>
		</div>
		<div class="label obrigatorio" >Parc.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="parcela" name="pagamento.parcela" value="${pagamento.parcela}" style="width: 100%"/>
		</div>
		<div class="label" style="width: 9%">Tot. Parc.:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="totalParcelas" name="pagamento.totalParcelas" value="${pagamento.totalParcelas}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Val. Parc:</div>
		<div class="input" style="width: 40%">
			<input type="text" id="valor" name="pagamento.valor" value="${pagamento.valor}" style="width: 75%"/>
		</div>
			
		<div class="label obrigatorio">NF:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="numeroNF" name="pagamento.numeroNF" value="${pagamento.numeroNF}" style="width: 100%"/>
		</div>
		<div class="input" style="width: 2%">
			<input type="button" id="botaoPesquisaNF" title="Pesquisar Pagamentos da NF" value="" class="botaoPesquisarPequeno" />
		</div>
		<div class="label" style="width: 7%">Val. NF:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="valorNF" name="pagamento.valorNF" value="${pagamento.valorNF}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Dt. Emiss.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataEmissao" name="pagamento.dataEmissao" value="${pagamento.dataEmissaoFormatada}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 8%">Dt. Receb.:</div>
		<div class="input" style="width: 11%">
			<input type="text" id="dataRecebimento" name="pagamento.dataRecebimento" value="${pagamento.dataRecebimentoFormatada}" style="width: 100%" />
		</div>
		
		<div class="label">Pedido:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="pedido" name="pagamento.idPedido" value="${pagamento.idPedido}" style="width: 100%"/>
		</div>
		<div class="input" style="width: 2%">
			<input type="button" id="botaoPesquisaPedido" title="Pesquisar Pagamentos do Pedido" value="" class="botaoPesquisarPequeno" />
		</div>
		<div class="label" style="width: 7%">Fornec.:</div>
		<div class="input" style="width: 60%">
			<input type="text" id="fornecedor" name="pagamento.nomeFornecedor" value="${pagamento.nomeFornecedor}" style="width: 22%"/>
			<div class="suggestionsBox" id="containerPesquisaFornecedor" style="display: none; width: 30%"></div>
		</div>
		<div class="label">Item:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="sequencial" name="pagamento.sequencialItem" value="${pagamento.sequencialItem}" style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Qtde:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="quantidade" name="pagamento.quantidadeItem" value="${pagamento.quantidadeItem}" style="width: 100%"/>
		</div>
		<div class="label" style="width: 10%">Descrição:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="descricao" name="pagamento.descricao" value="${pagamento.descricao}" style="width: 75%"/>
		</div>
		<div class="label">Cred. ICMS:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="valorCreditoICMS" name="pagamento.valorCreditoICMS" value="${pagamento.valorCreditoICMS}"  style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Mod. Frete:</div>
		<div class="input" style="width: 13%">
			<select id="frete" name="pagamento.modalidadeFrete" style="width: 100%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="frete" items="${listaModalidadeFrete}">
					<option value="${frete.codigo}"
						<c:if test="${frete.codigo eq pagamento.modalidadeFrete}">selected</c:if>>${frete.descricao}</option>
				</c:forEach>
			</select>
		</div>
		<div class="bloco_botoes">
			<input type="button" id="botaoInserirPagamento" title="Inserir Pagamento" value="" class="botaoInserir"/>
			<input type="button" id="botaoLimparPagamento" value="" title="Limpar Pagamento" class="botaoLimpar" />
			<input  type="button"id="botaoRemoverPagamento" value="" title="Remover Pagamento" class="botaoCancelar" />
		</div>
	</fieldset>
	</form>
	
	<c:if test="${not empty listaPagamento}">
	<a id="rodape"></a>
		<table class="listrada">
			<caption>${titulo}</caption>
			<thead>
				<tr>
					<th style="width: 3%">Sit.</th>
					<th style="width: 7%">Venc.</th>
					<th style="width: 7%">Val.(R$)</th>
					<th style="width: 7%">NF</th>
					<th style="width: 7%">Val. NF(R$)</th>
					<th style="width: 33%">Desc.</th>
					<th style="width: 5%">Parc.</th>
					<th style="width: 8%">Forn.</th>
					<th style="width: 5%">ICMS(R$)</th>
					<th style="width: 5%">Ação</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${listaPagamento}" var="pagamento" >
					<tr>
						<c:choose>
							<c:when test="${pagamento.liquidado}">
								<td style="text-align: center"><div class="botaoVerificacaoEfetuadaGrande" title="Liquidado"></div></td>
							</c:when>
							<c:when test="${not pagamento.liquidado and pagamento.vencido}">
								<td style="text-align: center"><div class="botaoVerificacaoFalhaGrande" title="Vencido"></div></td>
							</c:when>
							<c:otherwise>
								<td style="text-align: center"><div class="botaoVerificacaoAguardadaGrande" title="Aguardando"></div></td>
							</c:otherwise>
						</c:choose>
						<td>${pagamento.dataVencimentoFormatada}</td>
						<td>${pagamento.valor}</td>
						<td>${pagamento.numeroNF}</td>
						<td>${pagamento.valorNF}</td>
						<td>${pagamento.descricao}</td>
						<td>${pagamento.parcelaFormatada}</td>
						<td>${pagamento.nomeFornecedor}</td>
						<td>${pagamento.valorCreditoICMS}</td>
						<td>
							<div class="coluna_acoes_listagem">
								<form action="<c:url value="/pagamento/"/>${pagamento.id}" >
									<input type="hidden" name="dataInicial" value="${dataInicial}"/>
									<input type="hidden" name="dataFinal" value="${dataFinal}"/>
									<input type="submit" value="" title="Editar Pagamento" class="botaoEditar"/>
								</form>
								
								<c:choose>
									<c:when test="${not pagamento.liquidado}">
										<form action="<c:url value="/pagamento/liquidacao/"/>${pagamento.id}" method="post" >
											<input type="hidden" name="dataInicial" value="${dataInicial}"/>
											<input type="hidden" name="dataFinal" value="${dataFinal}"/>
											<input type="submit" value="" title="Liquidar Pagamento" class="botaoVerificacaoEfetuadaPequeno" />
										</form>
									</c:when>
									<c:otherwise>
										<form action="<c:url value="/pagamento/retonoliquidacao/"/>${pagamento.id}" method="post" >
											<input type="hidden" name="dataInicial" value="${dataInicial}"/>
											<input type="hidden" name="dataFinal" value="${dataFinal}"/>
											<input type="submit" value="" title="Retornar Liquidação Pagamento" class="botaoVerificacaoFalhaPequeno" />
										</form>
									</c:otherwise>
								</c:choose>
							</div>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</body>
</html>