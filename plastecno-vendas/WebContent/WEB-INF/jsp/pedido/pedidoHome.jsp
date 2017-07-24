<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/tabela_handler.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/logradouro.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bloco/contato.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/pedido/pedido.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>

<style type="text/css">
.listrada td:last-child form input:first-child {
	margin-left: 8%;
}
</style>

<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');

	inicializarAutomcompleteMaterial('<c:url value="/pedido/material"/>');
	// inicializarAutocompleteDescricaoPeca('<c:url value="/estoque/descricaopeca"/>');
	
	habilitar('#bloco_item_pedido #descricao', false);
	habilitar('#bloco_item_pedido #peso', false);
	habilitar('#bloco_item_pedido #aliquotaIPI', <c:out value="${not empty pedido and pedido.representada.IPIHabilitado}"/>);
	
	var urlInclusaoPedido = '<c:url value="/pedido/inclusao"/>';
	var urlInclusaoItemPedido = '<c:url value="/pedido/item/inclusao"/>';
	$("#botaoInserirPedido").click(function() {
		inserirPedido(false, urlInclusaoPedido, urlInclusaoItemPedido);
	});
	 
	$("#botaoInserirItemPedido").click(function() {
		inserirPedido(true, urlInclusaoPedido, urlInclusaoItemPedido);	
	});

	$("#botaoPesquisaNumeroPedido").click(function() {
		var numeroPedido = $('#numeroPedidoPesquisa').val();
		if (isEmpty(numeroPedido)) {
			gerarListaMensagemAlerta(new Array('O número do pedido é obrigatório para a pesquisa'));
			return;
		} 
		var form = $('#formVazio'); 
		form.attr('action', '<c:url value="/pedido/'+numeroPedido+'?tipoPedido=${tipoPedido}"/>');
		form.submit();
		
	});
	
	$("#botaoCopiarPedido").click(function() {
		var numeroPedido = $('#numeroPedidoPesquisa').val();
		if (isEmpty(numeroPedido)) {
			gerarListaMensagemAlerta(new Array('O número do pedido é obrigatório para copiar o pedido'));
			return;
		} 
		var form = $('#formVazio'); 
		form.attr('method', 'post').attr('action', '<c:url value="/pedido/copia/'+numeroPedido+'?tipoPedido=${tipoPedido}"/>');
		form.submit();
		
	});
	
	$("#botaoLimparNumeroPedido").click(function() {
		 $('#formLimparPedido').submit();
	});
	
	$("#representada").change(function() {
		habilitarIPI('<c:url value="/pedido"/>', $(this).val());	
	});
	
	$("#botaoImpressaoPedido").click(function() {
		var numeroPedido = $('#numeroPedido').val();
		if (isEmpty(numeroPedido)) {
			gerarListaMensagemAlerta(new Array('O pedido não pode ser impresso pois não existe no sistema'));
			return;
		} 

		$('#idPedidoImpressao').val($('#numeroPedido').val());
		var form = $(this).closest('form'); 
		form.submit();
		
	});
	
	$("#botaoPesquisaPedido").click(function() {
		$('#formPesquisa #tipoPedidoPesquisa').val($('#formPedido #tipoPedido').val());
		$('#formPesquisa #idFornecedorPesquisa').val($('#formPedido #representada').val());
		$('#formPesquisa #idClientePesquisa').val($('#formPedido #idCliente').val());
		$('#formPesquisa #idVendedorPesquisa').val($('#formPedido #idVendedor').val());
		var form = $(this).closest('form'); 
		form.submit();
	});
	
	<c:if test="${acessoCompraPermitido}">
	$("#pedidoAssociado").change(function() {
		if(isEmpty($(this).val())){
			return;
		}
		$('<input>').attr('type','hidden').attr('name','idPedido').attr('value',$(this).val()).appendTo('#formVazio');
		$('#formVazio').attr('action', '<c:url value="/pedidoassociado/pdf"/>').submit();
	});
	</c:if>
	
	$('#botaoEditarCliente').click(function(){
		var id = $('#formPedido #idCliente').val();
		var url = null;
		if(!isEmpty(id)){
			url='<c:url value="/cliente/"/>'+id;
		} else {
			url='<c:url value="/cliente"/>';
		}
		$('#formVazio').attr('action', url).submit();
	});
	
	inicializarBlocoItemPedido('<c:url value="/pedido"/>');
	
	inserirMascaraData('dataEntrega');
	inserirMascaraCNPJ('cnpj');
	inserirMascaraCPF('cpf');
	inserirMascaraInscricaoEstadual('inscricaoEstadual');
	inserirMascaraNumerica('numeroPedidoPesquisa', '9999999');
	inserirMascaraMonetaria('precoVenda', 7);
	inserirMascaraMonetaria('fretePedido', 7);
	inserirMascaraNumerica('aliquotaIPI', '99');
	inserirMascaraMonetaria('aliquotaComissao', 5);
	inserirMascaraMonetaria('aliquotaICMS', 5);
	inserirMascaraNumerica('quantidade', '9999999');
	inserirMascaraMonetaria('comprimento', 8);
	inserirMascaraMonetaria('peso', 8);
	inserirMascaraMonetaria('medidaExterna', 8);
	inserirMascaraMonetaria('medidaInterna', 8);
	inserirMascaraNumerica('prazoEntrega', '999');
	inserirMascaraNumerica('validade', '999');

	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
	inicializarAutomcompleteCliente('<c:url value="/pedido/cliente"/>');
	<%--Desabilitando toda a tela de pedidos --%>
	<c:if test="${pedidoDesabilitado}">
		$('input[type=text], select:not(.semprehabilitado), textarea').attr('disabled', true).addClass('desabilitado');
	</c:if>

	<c:if test="${empty pedido.id}">
		$('#formEnvioPedido #botaoEnviarPedido').hide();
	</c:if>

	$('#material').focus(function() {
		if($('#representada').val() == '') {
			gerarListaMensagemAlerta(new Array('Escolha uma representada antes de selecionar o material'));
		}
	});

	// A segunda condicao verifica quando o pedido eh do tipo de compra
	habilitar('#nomeCliente', <c:out value="${empty pedido.id and empty tipoPedido}"/>);
	habilitar('#numeroPedidoPesquisa', <c:out value="${empty pedido.id}"/>);
	habilitar('#representada', <c:out value="${empty pedido.id or not contemItem}"/>);
	habilitar('#bloco_item_pedido #ipi', <c:out value="${not ipiDesabilitado}"/>);
	habilitar('#idRepresentada', <c:out value="${not empty pedido.id and contemItem}"/>);
	
	$('#botaoRefazerPedido').click(function (){
		inicializarModalConfirmacao({
			mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja REFAZER esse pedido?',
			confirmar: function(){
				$('#botaoRefazerPedido').closest('form').submit();	
			}
		});
	});	
	
	$('#botaoCancelarPedido').click(function (){
		inicializarModalConfirmacao({
			mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja CANCELAR esse pedido?',
			confirmar: function(){
				$('#botaoCancelarPedido').closest('form').submit();	
			}
		});
	});
	
	$('#botaoEnviarPedido').click(function (){
		inicializarModalConfirmacao({
			mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja ENVIAR esse pedido?',
			confirmar: function(){
				$('#botaoEnviarPedido').closest('form').submit();	
			}
		});
	});
	
	$('#botaoAceitarOrcamento').click(function (){
		if(isEmpty($('#formEnvioPedido #idPedido').val())){
			return;
		}
		inicializarModalConfirmacao({
			mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja ACEITAR esse orçamento para os pedidos?',
			confirmar: function(){
				var form = $('#botaoAceitarOrcamento').closest('form');
				$(form).attr('action', '<c:url value="pedido/aceiteorcamento"/>').submit();
			}
		});
	});
});
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" action="pedido" method="get">
		<input type="hidden" name="orcamento" value="${empty orcamento ? false : orcamento}" />
		<input type="hidden" name="tipoPedido" value="${tipoPedido}" />
	</form>

	<form id="formPedido" action="<c:url value="/pedido/inclusao"/>" method="post">
		<fieldset>
			<legend>::: Dados do ${orcamento ? 'Orçamento': 'Pedido'} de ${isCompra ? 'Compra': 'Venda'} :::</legend>

			<!-- O campo id do pedido eh hidden pois o input text nao eh enviado na edicao do formulario pois esta "disabled" -->
			<input type="hidden" id="numeroPedido" name="pedido.id" value="${pedido.id}" /> 
			<input type="hidden" id="idCliente" name="pedido.cliente.id" value="${cliente.id}" /> 
			<input type="hidden" id="idVendedor" name="pedido.proprietario.id" value="${proprietario.id}" /> 
			<input type="hidden" id="idRepresentada" name="pedido.representada.id" value="${idRepresentadaSelecionada}" />
			<input type="hidden" id="tipoPedido" name="pedido.tipoPedido" value="${tipoPedido}" />
			<input type="hidden" id="orcamento" name="orcamento" value="${empty orcamento ? false : orcamento}" />
			<input type="hidden" id="situacaoPedido" name="pedido.situacaoPedido" value="${pedido.situacaoPedido}"/>
			
			<c:if test="${not empty pedido.id}">
			<div class="label">Pedido(s) de ${isCompra ? 'Venda:': 'Compra:'}</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" name="idPedidoAssociado"
					style="width: 13%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			</c:if>
			
			<div class="label">${isCompra ? 'Comprador:': 'Vendedor:'}</div>
			<div class="input" style="width: 40%">
				<input type="text" id="proprietario" name="proprietario.nome"
					value="${proprietario.nome} - ${proprietario.email}" disabled="disabled"
					class="uppercaseBloqueado desabilitado" style="width: 95%"/>
			</div>
			<div class="label" style="width: 10%">Situação:</div>
			<div class="input" style="width: 20%">
				<input type="text" name="pedido.situacaoPedido" 
					value="${pedido.situacaoPedido}" class="desabilitado" disabled="disabled" width="95%"/>
			</div>
			
			<div class="label">Número:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="numeroPedidoPesquisa" value="${pedido.id}"
					class="pesquisavel" />
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoPesquisaNumeroPedido"
					title="Pesquisar Pedido" value="" class="botaoPesquisarPequeno" />
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoLimparNumeroPedido"
					title="Limpar Pedido" value="" class="botaoLimparPequeno" />
			</div>
			<div class="input" style="width: 1%">
				<input type="button" id="botaoCopiarPedido"
					title="Copiar Pedido" value="" class="botaoCopiarPequeno" />
			</div>
			
			<div class="label" style="width: 12%">Nr. Pedido Cliente:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="numeroPedidoCliente"
					name="pedido.numeroPedidoCliente"
					value="${pedido.numeroPedidoCliente}" style="width: 100%" />
			</div>
			<div class="label" style="width: 12%">Pagamento:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="formaPagamento" name="pedido.formaPagamento"
					value="${pedido.formaPagamento}" style="width: 100%" />
			</div>
			<div class="label">
				<label>Data Entrega:</label>
			</div>
			<div class="input" style="width: 10%">
				<input type="text" id="dataEntrega" name="pedido.dataEntrega"
					value="${pedido.dataEntregaFormatada}" />
			</div>
			<div class="label" style="width: 17%">Prazo Entrega:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="prazoEntrega" name="pedido.prazoEntrega" value="${pedido.prazoEntrega}"/>
			</div>
			<div class="label" style="width: 12%">Data Envio:</div>
			<div class="input" style="width: 30%">
				<input type="text" id="dataEnvio"
					value="${pedido.dataEnvioFormatada}" readonly="readonly"
					class="desabilitado" style="width: 25%" />
			</div>
			<div class="label obrigatorio">Cliente:</div>
			<div class="input" style="width: 30%">
				<input type="text" id="nomeCliente" value="${cliente.nomeCompleto}" class="pesquisavel" style="width: 100%"/>
				<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoEditarCliente"
					title="Editar Cliente" value="" class="botaoEditar" />
			</div>
			<div class="label" style="width: 10%">Env. Email Ped.:</div>
			<div class="input" style="width: 30%">
				<input type="checkbox" id="clienteNotificadoVenda"
					name="pedido.clienteNotificadoVenda"
					<c:if test="${pedido.clienteNotificadoVenda}">checked</c:if>
					class="checkbox" style="width: 4%"/>
			</div>
			<div class="label">End. Faturam.</div>
			<div class="input" style="width: 80%">
				<input type="text" id="logradouroFaturamento"
					value="${logradouroFaturamento}" disabled="disabled" class="uppercaseBloqueado desabilitado" style="width: 50%"/>
			</div>
			<div class="label">CNPJ:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="cnpj" name="cliente.cnpj"
					value="${cliente.cnpj}" class="desabilitado" disabled="disabled" />
			</div>
			<div class="label" style="width: 5%">CPF:</div>
			<div class="input" style="width: 60%">
				<input type="text" id="cpf" name="cliente.cpf"
					value="${cliente.cpf}" class="desabilitado" disabled="disabled" style="width: 25%"/>
			</div>
			<div class="label">SUFRAMA:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="suframa" name="cliente.inscricaoSUFRAMA"
					value="${cliente.inscricaoSUFRAMA}" 
					class="uppercaseBloqueado desabilitado" disabled="disabled" />
			</div>
			<div class="label" style="width: 5%">Email:</div>
			<div class="input" style="width: 50%">
				<input type="text" id="email" name="cliente.email"
					value="${cliente.email}" style="width: 45%"
					class="uppercaseBloqueado desabilitado" disabled="disabled" />
			</div>
			<div class="label" style="width: 15%">Tipo Entrega:</div>
			<div class="input" style="width: 20%">
				<select id="tipoEntrega" name="pedido.tipoEntrega"
					style="width: 80%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="tipoEntrega" items="${listaTipoEntrega}">
						<option value="${tipoEntrega}"
							<c:if test="${tipoEntrega eq pedido.tipoEntrega}">selected</c:if>>${tipoEntrega.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio" style="width: 20%">${not empty tipoPedido ? 'Fornecedor:': 'Representada:'}</div>
			<div class="input" style="width: 40%">
				<select id="representada" name="pedido.representada.id"
					style="width: 45%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="representada" items="${listaRepresentada}">
						<option value="${representada.id}"
							<c:if test="${representada.id eq idRepresentadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Transportadora:</div>
			<div class="input" style="width: 20%">
				<select id="listaTransportadora" name="pedido.transportadora.id"
					style="width: 80%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="transportadora" items="${listaTransportadora}">
						<option value="${transportadora.id}"
							<c:if test="${transportadora.id eq pedido.transportadora.id}">selected</c:if>>${transportadora.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label" style="width: 20%">Redespacho:</div>
			<div class="input" style="width: 40%">
				<select id="listaRedespacho" name="pedido.transportadoraRedespacho.id" style="width: 45%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="redespacho" items="${listaRedespacho}">
						<option value="${redespacho.id}"
							<c:if test="${redespacho.id eq pedido.transportadoraRedespacho.id}">selected</c:if>>${redespacho.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>

			<div class="label obrigatorio">Finalidade:</div>
			<div class="input" style="width: 20%">
				<select id="finalidadePedido" name="pedido.finalidadePedido" style="width: 80%" >
					<option value=""></option>
					<c:forEach var="tipo" items="${listaTipoFinalidadePedido}">
						<option value="${tipo}" 
							<c:if test="${tipo eq pedido.finalidadePedido}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label" style="width: 20% ">Frete (R$):</div>
			<div class="input" style="width: 40%">
				<input id="fretePedido" name="pedido.valorFrete" value="${pedido.valorFrete}" style="width: 45%" />
			</div>
			<c:if test="${pedido.orcamento}">
				<div class="label">Validade:</div>
				<div class="input" style="width: 80%">
				<input id="validade" name="pedido.validade" value="${pedido.validade}" style="width: 5%" />
			</div>
			</c:if>
			<div class="label">Observação:</div>
			<div class="input areatexto" style="width: 70%">
				<textarea id="obervacao" name="pedido.observacao"
					style="width: 100%">${pedido.observacao}</textarea>
			</div>
			<div class="label">Observação Prod.:</div>
			<div class="input areatexto" style="width: 70%">
				<textarea id="observacaoProducao" name="pedido.observacaoProducao"
					style="width: 100%">${pedido.observacaoProducao}</textarea>
			</div>

		</fieldset>
	</form>
	<div class="bloco_botoes">
		<form id="formPesquisa" action="<c:url value="/pedido/listagem"/>" method="get">
			<input type="hidden" name="tipoPedido"  id="tipoPedidoPesquisa" value="${tipoPedido}"/> 
			<input type="hidden" name="idCliente" id="idClientePesquisa"  value="${cliente.id}"/> 
			<input type="hidden" name="idFornecedor" id="idFornecedorPesquisa" value="${idRepresentadaSelecionada}"/>
			<input type="hidden" name="idVendedor" id="idVendedorPesquisa" value="${proprietario.id}"/>  
			<input type="hidden" name="orcamento" id="orcamento" value="${empty orcamento ? false : orcamento}"/>
			<input type="button" id="botaoPesquisaPedido" value="" title="Pesquisar Dados do Pedido" class="botaoPesquisar" />
		</form>
		<form id="formLimparPedido" action="pedido/limpar" method="get">
			<input type="hidden" name="tipoPedido" value="${tipoPedido}" />
			<input type="hidden" name="orcamento" id="orcamento" value="${empty orcamento ? false : orcamento}"/>
			<input type="submit" value="" title="Limpar Dados do Pedido" class="botaoLimpar" />
		</form>
		<form action="pedido/pdf" method="get">
			<input type="hidden" name="tipoPedido" value="${tipoPedido}" /> 
			<input type="hidden" name="idPedido" id="idPedidoImpressao" value="${pedido.id}" />
			<input type="button" id="botaoImpressaoPedido" value="" title="Imprimir Pedido" class="botaoPDF" />
		</form>

		<c:if test="${acessoRefazerPedidoPermitido}">
			<form action="pedido/refazer" method="post">
				<input type="hidden" name="tipoPedido" value="${tipoPedido}" /> 
				<input type="hidden" name="idPedido" id="idPedido" value="${pedido.id}" />
				<input type="hidden" name="orcamento" id="orcamento" value="${empty orcamento ? false : orcamento}"/>
				<input id="botaoRefazerPedido" type="button" value="" title="Refazer Pedido" class="botaoRefazer" />
			</form>
		</c:if>
		<c:if test="${acessoCancelamentoPedidoPermitido}">
			<form action="pedido/cancelamento" method="post">
				<input type="hidden" name="tipoPedido" value="${tipoPedido}" /> 
				<input type="hidden" name="idPedido" id="idPedidoCancelamento" value="${pedido.id}" />
				<input id="botaoCancelarPedido" type="button" value="" title="Cancelar Pedido" class="botaoCancelar" />
			</form>
		</c:if>
	</div>

	<%-- c:if test="${not empty pedido.id}">
		<jsp:include page="/bloco/bloco_nota_fiscal.jsp" />
	</c:if --%>
	<jsp:include page="/bloco/bloco_contato.jsp" />

	<div class="bloco_botoes">
		<c:if test="${not pedidoDesabilitado and acessoCadastroPedidoPermitido}">
			<a id="botaoInserirPedido" title="Incluir Dados do Pedido" class="botaoInserir"></a>
		</c:if>
	</div>
	<jsp:include page="/bloco/bloco_item_pedido.jsp" />

	<form id="formEnvioPedido" action="<c:url value="/pedido/envio"/>" method="post">
		<input type="hidden" name="tipoPedido" value="${tipoPedido}"/>
		<input type="hidden" name="orcamento" value="${orcamento}"/>
		<div class="bloco_botoes">
			<input type="button" id="botaoEnviarPedido" title="Enviar Email do ${orcamento ? 'Orcamento' : 'Pedido'}" value="" class="botaoEnviarEmail"
				<c:if test="${not acessoEnvioPedidoPermitido and not acessoReenvioPedidoPermitido}"> style='display:none'</c:if> 
			/>
			<c:if test="${orcamento}">
				<input type="button" id="botaoAceitarOrcamento" title="Aceitar do Orçamento" value="" class="botaoAceitar"/>
			</c:if>
			<input type="hidden" id="idPedido" name="idPedido" value="${pedido.id}" />
		</div>
	</form>

	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Pedidos de ${isCompra ? 'Compra': 'Venda'} :::</legend>
		<div id="paginador"></div>
		<div>
			<table id="tabelaItemPedido" class="listrada">
			<thead>
				<tr>
					<th style="width: 10%">Situaç.</th>
					<th style="width: 10%">Ped./N. Cli.</th>
					<th style="width: 5%">Item</th>
					<th style="width: 5%">Qtde.</th>
					<th style="width: 35%">Descrição</th>
					<th style="width: 5%">Venda</th>
					<th style="width: 5%">Preço (R$)</th>
					<th style="width: 5%">Unid. (R$)</th>
					<th style="width: 10%">Total (R$)</th>
					<th style="width: 5%">IPI (%)</th>
					<th style="width: 5%">ICMS (%)</th>
					<th>Ações</th>
				</tr>
			</thead>

			<tbody>
			
			<c:forEach items="${relatorioItemPedido.listaGrupo}" var="grupo" varStatus="iGrupo">
					<c:forEach items="${grupo.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento}">${grupo.id.situacaoPedido.descricao}</td>
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento}">${grupo.id.id}/<br></br>${grupo.id.numeroPedidoCliente}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.sequencial}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" style="text-align: center;">${item.tipoVenda}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoVendaFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoUnidadeFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoItemFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.aliquotaIPIFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.aliquotaICMSFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/pedido/pdf"/>">
										<input type="hidden" name="tipoPedido" value="${grupo.id.tipoPedido}" /> 
										<input type="hidden" name="idPedido" value="${grupo.id.id}" />
										<input type="submit" value="" title="Visualizar Pedido PDF" class="botaoPdf_16 botaoPdf_16_centro" />
									</form>
									<form action="<c:url value="/pedido/${grupo.id.id}"/>" method="get">
										<input type="hidden" name="tipoPedido" value="${grupo.id.tipoPedido}" /> 
										<input type="hidden" name="id" value="${grupo.id.id}" />
										<input type="submit" id="botaoEditarPedido" title="Editar Dados do Pedido" value="" class="botaoEditar" />
									</form>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>

			</tbody>
			
		</table>
			
		</div>
	</fieldset>
	
</body>
</html>
