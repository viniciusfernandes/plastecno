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

</head>
<body>
	<form id="formPedido" action="<c:url value="/pagamento/inclusao"/>" method="post">
		<input type="hidden" id="idPedido"  name="pedido.id" value="${pedido.id}"/>
		<input type="hidden" id="idRepresentada" name="pedido.representada.id" value="${idRepresentadaSelecionada}" />
	<fieldset>
		<legend>Pagamento</legend>
		<div class="label">Dt. Venc.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataVencimento" value="${pagamento.dataVencimentoFormatada}" disabled="disabled" style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Tipo:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="tipoPagamento" value="${pagamento.tipoPagamento}" style="width: 100%"/>
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
			<input type="text" id="valorParcela" name="pagamento.valorParcela" value="${pagamento.valorParcela}" style="width: 75%"/>
		</div>
			
		<div class="label obrigatorio">NF:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="numeroNF" name="pagamento.numeroNF" value="${pagamento.numeroNF}" style="width: 100%"/>
		</div>
		<div class="label" style="width: 9%">Val. NF:</div>
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
		
		<div class="label" style="width: 9%">Fornec.:</div>
		<div class="input" style="width: 60%">
			<input type="text" id="fornecedor" name="pagamento.idFornecedor" value="${pagamento.idFornecedor}" style="width: 22%"/>
		</div>
		<div class="label">Item:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="sequencial" value="${pagamento.sequencialItem}" style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Qtde:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="quantidade" value="${pagamento.quantidadeItem}" style="width: 100%"/>
		</div>
		<div class="label" style="width: 10%">Descrição:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="descricao" name="pagamento.descricao" value="${pagamento.descricao}" style="width: 75%"/>
		</div>
		<div class="label">Cred. ICMS:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataEnvio" value="${pedido.dataEnvioFormatada}"  style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Mod. Frete:</div>
		<div class="input" style="width: 13%">
			<select id="frete" id="frete" name="pagamento.modalidadeFrete" style="width: 100%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="frete" items="${listaModalidadeFrete}">
					<option value="${frete.codigo}"
						<c:if test="${frete.codigo eq freteSelecionado}">selected</c:if>>${frete.descricao}</option>
				</c:forEach>
			</select>
		</div>
		<div class="bloco_botoes">
			<input type="button" id="botaoInserirPagamento" title="Inserir Pagamento" value="" class="botaoInserir"/>
			<input type="button" id="botaoLimparPagamento" value="" title="Limpar Pagamento" class="botaoLimpar" />
			<input  type="button"id="botaoCancelarPagamento" value="" title="Cancelar Pagamento" class="botaoCancelar" />
		</div>
	</fieldset>
	</form>
</body>
</html>