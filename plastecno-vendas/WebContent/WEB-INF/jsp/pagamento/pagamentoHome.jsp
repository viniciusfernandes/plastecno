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
		
		adicionarInputHiddenFormulario('formPagamento', 'dataInicial', $('#dataInicial').val());
		adicionarInputHiddenFormulario('formPagamento', 'dataFinal', $('#dataFinal').val());
		$('#formPagamento').attr('action', '<c:out value="pagamento/inclusao"/>').attr('method', 'post').submit();	
	});
	
	$('#botaoLimparPeriodo').click(function(){
		$(this).closest('form').attr('action', '<c:out value="pagamento"/>').attr('method', 'get').submit();	
	});
	
	$('#botaoPesquisarFornecedor').click(function(){
		var idForn = $('#idFornecedor').val();
		if(isEmpty(idForn)){
			return;
		}
		adicionarInputHiddenFormulario('formVazio', 'idFornecedor', idForn);
		adicionarInputHiddenFormulario('formVazio', 'dataInicial', $('#dataInicial').val());
		adicionarInputHiddenFormulario('formVazio', 'dataFinal', $('#dataFinal').val());
		$('#formVazio').attr('action', '<c:out value="pagamento/fornecedor/"/>'+idForn).attr('method', 'get').submit();	
	});
	
	$('#botaoPesquisarPedido').click(function(){
		var idPedido = $('#pedido').val();
		if(isEmpty(idPedido)){
			return;
		}
		adicionarInputHiddenFormulario('formVazio', 'idPedido', idPedido);
		adicionarInputHiddenFormulario('formVazio', 'dataInicial', $('#dataInicial').val());
		adicionarInputHiddenFormulario('formVazio', 'dataFinal', $('#dataFinal').val());
		$('#formVazio').attr('action', '<c:out value="pagamento/pedido/"/>'+idPedido).attr('method', 'get').submit();	
	});
	
	$('#botaoPesquisarNF').click(function(){
		var numeroNF = $('#numeroNF').val();
		if(isEmpty(numeroNF)){
			return;
		}
		adicionarInputHiddenFormulario('formVazio', 'numeroNF', numeroNF);
		adicionarInputHiddenFormulario('formVazio', 'dataInicial', $('#dataInicial').val());
		adicionarInputHiddenFormulario('formVazio', 'dataFinal', $('#dataFinal').val());
		$('#formVazio').attr('action', '<c:out value="pagamento/nf/"/>'+numeroNF).attr('method', 'get').submit();	
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
	
	<jsp:include page="/bloco/bloco_edicao_pagamento.jsp"/>
	
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
					<th style="width: 7%">Ped.</th>
					<th style="width: 33%">Desc.</th>
					<th style="width: 5%">Parc.</th>
					<th style="width: 8%">Forn.</th>
					<th style="width: 5%">ICMS(R$)</th>
					<th style="width: 7%">Ação</th>
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
						<td>${pagamento.idPedido}</td>
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
								<form action="<c:url value="/pagamento/remocao/"/>${pagamento.id}" method="post">
									<input type="hidden" name="dataInicial" value="${dataInicial}"/>
									<input type="hidden" name="dataFinal" value="${dataFinal}"/>
									<input type="submit" value="" title="Remover Pagamento" class="botaoRemover"/>
								</form>
							</div>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</body>
</html>