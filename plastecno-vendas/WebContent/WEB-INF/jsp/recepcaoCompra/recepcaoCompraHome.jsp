<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Recep��o de Compras</title>

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
	
	var checker = new tabelaChecker({idTabela:'tabelaRecepcaoCompras', nomeParametros:'listaIdItem'});
	
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
		if(!checker.hasChecked()){
			gerarListaMensagemAlerta(['Para efetuar um pagamento selecione algum elemento da lista de compras.']);
			return;
		}
		checker.addInputHidden('formPagamento');
		adicionarInputHiddenFormulario('formPagamento', 'dataInicial', $('#dataInicial').val());
		adicionarInputHiddenFormulario('formPagamento', 'dataFinal', $('#dataFinal').val());
		adicionarInputHiddenFormulario('formPagamento', 'idRepresentada', $('#formPesquisa #idRepresentada').val());
		$('#formPagamento').attr('action', '<c:url value="/compra/item/pagamento/inclusao"/>').attr('method', 'post').submit();	
	});
	
});

function removerItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja REMOVER esse item do pedido de compra?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};


function recepcionarItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja RECEPCIONAR esse item do pedido de compra?',
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
			<legend>::: Pedidos de Compra para Recep��o :::</legend>
			<div class="label" style="width: 30%">Data In�cial:</div>
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
			<div class="input" style="width: 50%">
				<select name="idRepresentada" style="width: 30%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="representada" items="${listaRepresentada}">
						<option value="${representada.id}"
							<c:if test="${representada.id eq idRepresentadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>
			<div class="bloco_botoes">
				<input type="submit" value="" class="botaoPesquisar" /> 
				<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Gera��o do Relat�rio de Compras" class="botaoLimpar" />
			</div>
		</fieldset>
	</form>
	
	<form id="formPagamento">
	<fieldset>
		<legend>Pagamento</legend>
		<div class="label">Dt. Venc.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataVencimento" name="pagamento.dataVencimento" value="${pagamento.dataVencimentoFormatada}" style="width: 100%" />
		</div>
		
		<div class="label" style="width: 10%">Dt. Emiss.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataEmissao" name="pagamento.dataEmissao" value="${pagamento.dataEmissaoFormatada}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Dt. Receb.:</div>
		<div class="input" style="width: 40%">
			<input type="text" id="dataRecebimento" name="pagamento.dataRecebimento" value="${pagamento.dataRecebimentoFormatada}" style="width: 30%" />
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
			<input type="button" id="botaoInserirPagamento" title="Inserir Pagamento" value="" class="botaoDinheiro"/>
			<input type="button" id="botaoLimparPagamento" value="" title="Limpar Pagamento" class="botaoLimpar" />
		</div>
	</fieldset>
	</form>
	
	<c:if test="${not empty itemPedido}">
		<jsp:include page="/bloco/bloco_edicao_item.jsp"/>
	</c:if>
	
	<c:if test="${not empty relatorio}">
		<table id='tabelaRecepcaoCompras' class="listrada" >
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 7%">Num. Pedido</th>
					<th style="width: 5%">Dt. Entrega</th>
					<th style="width: 7%">Num. Venda</th>
					<th style="width: 1%">Item</th>
					<th style="width: 5%">Qtde</th>
					<th style="width: 5%">Qtde Recep.</th>
					<th style="width: 38%">Desc. Item</th>
					<th style="width: 10%">Comprador</th>
					<th style="width: 10%">Forneced.</th>
					<th style="width: 13%">A��o</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.listaGrupo}" var="pedido" varStatus="iGrupo">
					<c:forEach items="${pedido.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}">${pedido.id}</td>
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}">${pedido.propriedades['dataEntrega']}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.idPedidoVenda}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.sequencial}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidadeRecepcionada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeProprietario}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeRepresentada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/pedido/pdf"/>" >
										<input type="hidden" name="idPedido" value="${pedido.id}" />
										<input type="hidden" name="tipoPedido" value="${relatorio.propriedades['tipoPedido']}" /> 
										<input type="submit" value="" title="Visualizar Pedido PDF" class="botaoPdf_16 botaoPdf_16_centro"/>
									</form>
									<form action="<c:url value="/compra/item/recepcao"/>" method="post" >
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<input type="button" value="" title="Recepcionar o Item do Pedido" 
										onclick="recepcionarItem(this);" class="botaoAdicionar_16" />
									</form>
									<form action="<c:url value="/compra/item/edicao"/>" method="post">
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<input type="button" value="" title="Editar o Item do Pedido" class="botaoEditar" onclick="submeterForm(this);"/>
									</form>
									<form action="<c:url value="/compra/item/remocao"/>" method="post" >
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<input type="button" value="" title="Remover o Item do Pedido" 
											onclick="removerItem(this);" class="botaoRemover" />
									</form>
									<div class="input" style="width: 1%">
										<input type="checkbox" name="idItemPedido" value="${item.id}" />
									</div>
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