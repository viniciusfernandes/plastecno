<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Recepção de Compras</title>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>
<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');
	
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
	
	habilitar('#bloco_item_pedido input:radio', false);
	habilitar('#bloco_item_pedido #formaMaterial', false);
	habilitar('#bloco_item_pedido #descricao', false);
	habilitar('#bloco_item_pedido #material', false);
	habilitar('#bloco_item_pedido #medidaExterna', false);
	habilitar('#bloco_item_pedido #medidaInterna', false);
	habilitar('#bloco_item_pedido #comprimento', false);
	habilitar('#bloco_item_pedido #preco', false);
	habilitar('#bloco_item_pedido #aliquotaIPI', false);
	habilitar('#bloco_item_pedido #aliquotaICMS', false);
	
	$('#botaoLimpar').click(function () {
		$('#formVazio').submit();
	});
	
	$('#botaoInserirItemPedido').click(function () {
		if(isEmpty($('#bloco_item_pedido #idItemPedido').val())){
			return;
		}
		var parametros = 'idItemPedido='+$('#bloco_item_pedido #idItemPedido').val()+'&quantidadeRecepcionada='+$('#bloco_item_pedido #quantidade').val();
		parametros += '&ncm='+$('#bloco_item_pedido #ncm').val()+'&' + $('#formPesquisa').serialize();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/compra/item/recepcaoparcial"/>?'+parametros);
		$(form).submit();
	});
	
	$('#botaoInserirNF').click(function () {
		if(isEmpty($('#bloco_item_pedido #idItemPedido').val())){
			return;
		}
		
		var parametros = '?dataInicial='+$('#formPesquisa #dataInicial').val(); 
		parametros += '&dataFinal='+$('#formPesquisa #dataFinal').val();
		parametros += '&idRepresentada='+$('#formPesquisa #idRepresentada').val();
		
		var form =  $(this).closest('form');
		$(form).attr('method', 'get');
		$(form).attr('action', '<c:url value="/compra/recepcao/inclusaodadosnf"/>'+parametros);
		$(form).submit();
	});
	
	$('#botaoInserirPagamento').click(function (){
		adicionarInputHiddenFormulario('formPagamento', 'dataInicial', $('#dataInicial').val());
		adicionarInputHiddenFormulario('formPagamento', 'dataFinal', $('#dataFinal').val());
		adicionarInputHiddenFormulario('formPagamento', 'idRepresentada', $('#formPesquisa #idRepresentada').val());
		$('#formPagamento').attr('action', '<c:url value="/compra/item/pagamento/inclusao"/>').attr('method', 'post').submit();	
	});
	
	$('#botaoLimparPagamento').click(function(){
		$('#formPagamento input:text').val('');
		$('#formPagamento input:hidden').val('');
		$('#formPagamento select').val('');	
	});
	
	var listaIdSelecionado=<c:out value="${not empty listaIdItemSelecionado ? listaIdItemSelecionado : \'new Array()\'}"/>;
	tabelaLinhaSelecionavelExt({
		idForm:'formPagamento',
		idTabela:'tabelaPedidoCompra',
		idBotaoLimpar:'botaoLimparPagamento',
		listaIdSelecionado: listaIdSelecionado
	});
	
	$('#botaoPesquisaNumeroPedido').click(function(){
		var lNumero = $('#listaNumeroPedido').val();
		if(isEmpty(lNumero)){
			return;
		}
		lNumero = lNumero.replace(/\D+/g, ';').split(';');
		var f = document.getElementById('formVazio');
		for (var i = 0; i < lNumero.length; i++) {
			adicionarInputHiddenFormulario('formVazio', 'listaNumeroPedido['+i+']', lNumero[i]);
		}
		f.action = '<c:url value="/compra/recepcao/listagem/pedido"/>';
		f.method='get';
		f.submit();
	});
	
	inserirMascaraData('dataEmissao');
	inserirMascaraData('dataRecebimento');

	inserirMascaraMonetaria('valorNF', 11);
	inserirMascaraNumerica('numeroNF', '9999999');
});

function removerItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja REMOVER esse item do pedido de compra?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};


function recepcionarItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja RECEPCIONAR esse item do pedido de compra?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};

</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" >
	</form>


	<form id="formPesquisa" action="<c:url value="/compra/recepcao/listagem"/>" method="get">
		<fieldset>
			<legend>::: Pedidos de Compra para Recepção :::</legend>
			<div class="label" style="width: 30%">Data Inícial:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataInicial" name="dataInicial"
					value="${dataInicial}" maxlength="10" class="pesquisavel" />
			</div>

			<div class="label" style="width: 10%">Data Final:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataFinal" name="dataFinal"
					value="${dataFinal}" maxlength="100" class="pesquisavel" />
			</div>
			<div class="label" style="width: 30%">Fornecedor:</div>
			<div class="input" style="width: 60%">
				<select name="idRepresentada" style="width: 25%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="representada" items="${listaRepresentada}">
						<option value="${representada.id}"
							<c:if test="${representada.id eq idRepresentadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label" style="width: 30%">Pedido(s):</div>
			<div class="input" style="width: 26%">
				<input type="text" id="listaNumeroPedido" value="${listaNumeroPedido}" style="width: 100%"/>
			</div>
			<div class="input" style="width: 4%">
				<input type="button" id="botaoPesquisaNumeroPedido" title="Pesquisar Pedido(s)" value="" class="botaoPesquisarPequeno"/>
			</div>
			<div class="bloco_botoes">
				<input type="submit" value="" class="botaoPesquisar" /> 
				<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Compras" class="botaoLimpar" />
			</div>
		</fieldset>
	</form>
	
	<c:if test="${acessoGeracaoPagamentoPermitida}">
	<form id="formPagamento">
	<fieldset>
		<legend>::: Pagamento :::</legend>
		<div class="label" >Dt. Emiss.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataEmissao" name="pagamento.dataEmissao" value="${dataEmissaoFormatada}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Dt. Receb.:</div>
		<div class="input" style="width: 60%">
			<input type="text" id="dataRecebimento" name="pagamento.dataRecebimento" value="${dataRecebimentoFormatada}" style="width: 17%" />
		</div>
		
		<div class="label obrigatorio">NF:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="numeroNF" name="pagamento.numeroNF" value="${pagamento.numeroNF}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Val. NF:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="valorNF" name="pagamento.valorNF" value="${pagamento.valorNF}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Mod. Frete:</div>
		<div class="input" style="width: 40%">
			<select id="frete" name="pagamento.modalidadeFrete" style="width: 40%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="frete" items="${listaModalidadeFrete}">
					<option value="${frete.codigo}"
						<c:if test="${frete.codigo eq pagamento.modalidadeFrete}">selected</c:if>>${frete.descricao}</option>
				</c:forEach>
			</select>
		</div>
		<div class="bloco_botoes">
			<input type="button" id="botaoInserirPagamento" title="Inserir Pagamento" value="" class="botaoDinheiro"/>
			<input type="button" id="botaoLimparPagamento" value="" title="Limpar Pagamento" class="botaoLimpar" />
		</div>
	</fieldset>
	</form>
	</c:if>
	
	<c:if test="${not empty itemPedido}">
		<jsp:include page="/bloco/bloco_edicao_item.jsp"/>
	</c:if>
	
	<jsp:include page="/bloco/bloco_listagem_pedido_compra.jsp"></jsp:include>

</body>
</html>