<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/tabela_handler.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/logradouro.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bloco/contato.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/pedido/pedido.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>

<style type="text/css">
.listrada td:last-child form input:first-child {
	margin-left: 8%;
}
</style>

<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');

	inicializarAutomcompleteMaterial('<c:url value="/pedido/material"/>');

	habilitar('#bloco_item_pedido #descricao', false);
	habilitar('#bloco_item_pedido #ipi', false);
	
	$("#botaoLimpar").click(function() {
		$('#formVazio').submit();
	});

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
		form.attr('action', '<c:url value="/pedido/'+numeroPedido+'"/>');
		form.submit();
		
	});

	$("#representada").change(function() {
		habilitarIPI($(this).val());	
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
	
	inicializarBlocoItemPedido('<c:url value="/pedido"/>');
	
	inserirMascaraData('dataEntrega');
	inserirMascaraCNPJ('cnpj');
	inserirMascaraCPF('cpf');
	inserirMascaraInscricaoEstadual('inscricaoEstadual');
	inserirMascaraNumerica('quantidade', '9999999');
	inserirMascaraNumerica('numeroPedidoPesquisa', '9999999');
	inserirMascaraMonetaria('precoVenda', 7);
	inserirMascaraNumerica('ipi', '99');
	
	inserirMascaraMonetaria('comprimento', 8);
	inserirMascaraMonetaria('medidaExterna', 8);
	inserirMascaraMonetaria('medidaInterna', 8);
	

	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
	inicializarAutomcompleteCliente('<c:url value="/pedido/cliente"/>');

	<c:if test="${pedidoDesabilitado}">
		$('input[type=text], select, textarea').attr('disabled', true).addClass('desabilitado');
	</c:if>

	<c:if test="${empty pedido.id}">
		$('#formEnvioPedido #botaoEnviarPedido').hide();
	</c:if>

	$('#material').focus(function() {
		if($('#representada').val() == '') {
			gerarListaMensagemAlerta(new Array('Escolha uma representada antes de selecionar o material'));
		}
	});

	habilitar('#nomeCliente', <c:out value="${empty pedido.id}"/>);
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
});


</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" action="pedido" method="get"></form>

	<form id="formPedido" action="<c:url value="/pedido/inclusao"/>"
		method="post">
		<fieldset>
			<legend>::: Dados do Pedido :::</legend>

			<!-- O campo id do pedido eh hidden pois o input text nao eh enviado na edicao do formulario pois esta "disabled" -->
			<input type="hidden" id="numeroPedido" name="pedido.id"
				value="${pedido.id}" /> <input type="hidden" id="idCliente"
				name="pedido.cliente.id" value="${cliente.id}" /> <input
				type="hidden" id="idVendedor" name="pedido.vendedor.id"
				value="${vendedor.id}" /> <input type="hidden" id="idRepresentada"
				name="pedido.representada.id" value="${representadaSelecionada.id}" />

			<div class="label">Vendedor:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="vendedor" name="vendedor.nome"
					value="${vendedor.nome} - ${vendedor.email}" disabled="disabled"
					class="uppercaseBloqueado desabilitado" />
			</div>
			<c:choose>
				<c:when test="${not pedidoDesabilitado}">
					<div class="label" style="width: 10%">Orçamento:</div>
					<div class="input" style="width: 30%">
						<input type="checkbox" id="orcamento" name="pedido.orcamento"
							<c:if test="${pedido.orcamento}">checked</c:if> class="checkbox" />
					</div>
				</c:when>
				<c:otherwise>
					<div class="label" style="width: 10%">Situação:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="situacaoPedido"
							name="pedido.situacaoPedido"
							value="${pedido.situacaoPedido.descricao}" />
					</div>
				</c:otherwise>
			</c:choose>

			<div class="label">Nr. Pedido:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="numeroPedidoPesquisa" value="${pedido.id}"
					class="pesquisavel" />
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoPesquisaNumeroPedido"
					title="Pesquisar Pedido" value="" class="botaoPesquisarPequeno" />
			</div>
			<div class="label">Nr. Pedido Cliente:</div>
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
			<div class="label" style="width: 17%">Data Inclusão:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="dataInclusao"
					value="${pedido.dataInclusaoFormatada}" readonly="readonly"
					class="desabilitado" />
			</div>
			<div class="label" style="width: 12%">Data Envio:</div>
			<div class="input" style="width: 30%">
				<input type="text" id="dataEnvio"
					value="${pedido.dataEnvioFormatada}" readonly="readonly"
					class="desabilitado" style="width: 30%" />
			</div>
			<div class="label obrigatorio">Cliente:</div>
			<div class="input" style="width: 30%">
				<input type="text" id="nomeCliente" value="${cliente.nomeCompleto}"
					class="pesquisavel" />
				<div class="suggestionsBox" id="containerPesquisaCliente"
					style="display: none; width: 50%"></div>
			</div>
			<div class="label">Receber Email Ped.:</div>
			<div class="input" style="width: 30%">
				<input type="checkbox" id="clienteNotificadoVenda"
					name="pedido.clienteNotificadoVenda"
					<c:if test="${pedido.clienteNotificadoVenda}">checked</c:if>
					class="checkbox" />
			</div>
			<div class="label">CNPJ:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="cnpj" name="cliente.cnpj"
					value="${cliente.cnpj}" class="desabilitado" disabled="disabled" />
			</div>
			<div class="label" style="width: 5%">CPF:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="cpf" name="cliente.cpf"
					value="${cliente.cpf}" class="desabilitado" disabled="disabled" />
			</div>
			<div class="label" style="width: 15%">Email Envio NFe:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="email" name="cliente.email"
					value="${cliente.email}" style="width: 80%"
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
			<div class="label obrigatorio" style="width: 11%">Representada:</div>
			<div class="input" style="width: 50%">
				<select id="representada" name="pedido.representada.id"
					style="width: 80%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="representada" items="${listaRepresentada}">
						<option value="${representada.id}"
							<c:if test="${representada eq representadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
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
			<div class="label" style="width: 11%">Redespacho:</div>
			<div class="input" style="width: 50%">
				<select id="listaRedespacho"
					name="pedido.transportadoraRedespacho.id" style="width: 80%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="redespacho" items="${listaRedespacho}">
						<option value="${redespacho.id}"
							<c:if test="${redespacho.id eq pedido.transportadoraRedespacho.id}">selected</c:if>>${redespacho.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>

			<div class="label obrigatorio">Finalidade:</div>
			<div class="input">
				<input type="radio" name="pedido.finalidadePedido"
					value="${industrializacao}"
					<c:if test="${industrializacao eq pedido.finalidadePedido}">checked</c:if> />
			</div>
			<div class="label label_radio_button">Industrialização</div>
			<div class="input">
				<input type="radio" name="pedido.finalidadePedido"
					value="${consumo}"
					<c:if test="${consumo eq pedido.finalidadePedido}">checked</c:if> />
			</div>
			<div class="label label_radio_button" style="width: 8%">Consumo</div>
			<div class="input">
				<input type="radio" name="pedido.finalidadePedido"
					value="${revenda}"
					<c:if test="${revenda eq pedido.finalidadePedido}">checked</c:if> />
			</div>
			<div class="label label_radio_button" style="width: 50%">Revenda</div>

			<div class="label">Observação:</div>
			<div class="input areatexto" style="width: 70%">
				<textarea id="obervacao" name="pedido.observacao"
					style="width: 100%">${pedido.observacao}</textarea>
			</div>

		</fieldset>
	</form>
	<div class="bloco_botoes">
		<form id="formPesquisa" action="pedido/listagem" method="get">
			<input type="hidden" name="idCliente" id="idClientePesquisa" value="${cliente.id}" /> 
			<input type="submit" value="" title="Pesquisar Dados do Pedido" class="botaoPesquisar" />
		</form>
		<form action="pedido" method="get">
			<input type="submit" value="" title="Limpar Dados do Pedido"
				class="botaoLimpar" />
		</form>
		<form action="pedido/pdf" method="get">
			<input type="hidden" name="idPedido" id="idPedidoImpressao" value="${pedido.id}" /> 
			<input type="button" id="botaoImpressaoPedido" value="" title="Imprimir Pedido" class="botaoPDF" />
		</form>

		<c:if test="${acessoRefazerPedidoPermitido}">
			<form action="pedido/refazer" method="post">
				<input type="hidden" name="idPedido" id="idPedido" value="${pedido.id}" /> 
				<input id="botaoRefazerPedido" type="button" value="" title="Refazer Pedido" class="botaoRefazer" />
			</form>
		</c:if>
		<c:if test="${acessoCancelamentoPedidoPermitido}">
			<form action="pedido/cancelamento" method="post">
				<input type="hidden" name="idPedido" id="idPedidoCancelamento"
					value="${pedido.id}" /> <input id="botaoCancelarPedido"
					type="button" value="" title="Cancelar Pedido"
					class="botaoCancelar" />
			</form>
		</c:if>
	</div>

	<jsp:include page="/bloco/bloco_contato.jsp" />

	<div class="bloco_botoes">
		<c:if test="${not pedidoDesabilitado and acessoVendaPermitida}">
			<a id="botaoInserirPedido" title="Incluir Dados do Pedido"
				class="botaoInserir"></a>
		</c:if>
	</div>
	<jsp:include page="/bloco/bloco_item_pedido.jsp" />

	<form id="formEnvioPedido" action="<c:url value="/pedido/envio"/>"
		method="post">
		<div class="bloco_botoes">

			<input type="submit" id="botaoEnviarPedido"
				title="Enviar Dados do Pedido" value=""
				onclick="javascript: return confirm('Voce deseja mesmo enviar o PEDIDO?');"
				class="botaoEnviarEmail"
				<c:if test="${not acessoEnvioPedidoPermitido and not acessoReenvioPedidoPermitido}"> style='display:none'</c:if> />
			<input type="hidden" id="idPedido" name="idPedido"
				value="${pedido.id}" />

		</div>
	</form>

	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Pedidos :::</legend>
		<div id="paginador"></div>
		<div>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 10%">Situação</th>
						<th style="width: 9%">Nr. Pedido</th>
						<th style="width: 11%">Nr. Pedido Cliente</th>
						<th style="width: 33%">Cliente / Vendedor</th>
						<th style="width: 10%">Data Incl.</th>
						<th style="width: 12%; text-align: center">CNPJ/CPF</th>
						<th style="width: 8%">Valor (R$)</th>
						<th style="width: 7%">Ações</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="pedido" items="${listaPedido}">
						<tr>
							<td style="text-align: center;">${pedido.situacaoPedido}</td>
							<td>${pedido.id}</td>
							<td>${pedido.numeroPedidoCliente}</td>
							<td>${pedido.cliente.nomeFantasia}/
								${pedido.vendedor.nomeCompleto}</td>
							<td style="text-align: center;">${pedido.dataInclusaoFormatada}</td>
							<td>${pedido.cliente.documento}</td>
							<td style="text-align: right;">${pedido.valorPedido}</td>
							<td>
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/pedido/pdf"/>">
										<input type="hidden" name="idPedido" value="${pedido.id}" /> <input
											type="submit" value="" title="Visualizar Pedido PDF"
											class="botaoPdf_16 botaoPdf_16_centro" />
									</form>
									<form action="<c:url value="/pedido/${pedido.id}"/>"
										method="get">
										<input type="submit" id="botaoEditarPedido"
											title="Editar Dados do Pedido" value="" class="botaoEditar" />
										<input type="hidden" name="id" value="${pedido.id}" />
									</form>
								</div>

							</td>
						</tr>

					</c:forEach>
				</tbody>

			</table>
		</div>
	</fieldset>
	
</body>
</html>
