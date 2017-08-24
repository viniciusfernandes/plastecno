<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE >
<html>
<head>

<jsp:include page="/bloco/bloco_css.jsp" />


<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/tabela_handler.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/pedido/pedido.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/pedido/bloco_item_pedido.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>

<script type="text/javascript">
$(document).ready(function() {
	scrollTo('${ancora}');

	var tabelaItemHandler = gerarTabelaItemPedido('<c:url value="/orcamento"/>');
	
	$("#botaoInserirItemPedido").click(function() {
		inserirOrcamento();
		inserirItemPedido($('#numeroPedido').val(), '<c:url value="/orcamento/item/inclusao"/>');
	});
	
	$("#botaoInserirOrcamento").click(function() {
		inserirOrcamento();
	});
	
	$("#botaoPesquisaOrcamento").click(function() {
		var idPedido = $('#numeroPedido').val();
		if (isEmpty(idPedido)) {
			gerarListaMensagemAlerta(new Array('O número do pedido é obrigatório para a pesquisa'));
			return;
		} 
		var form = document.getElementById('formVazio');
		form.action = '<c:url value="/orcamento/'+idPedido+'"/>';
		form.submit();
	});
	
	inicializarAutocompleteCliente('<c:url value="/orcamento/cliente"/>', function(cliente){
		$('#idCliente').val(cliente.id);
		$('#cnpj').val(cliente.cnpj);
		$('#cpf').val(cliente.cpf);
		$('#nomeCliente').val(cliente.nomeCompleto);
		$('#ddd').val(cliente.ddd);
		$('#telefone').val(cliente.telefone);
		$('#idVendedor').val(cliente.vendedor.id);
		$('#vendedor').val(cliente.vendedor.nome + ' - '+ cliente.vendedor.email);
	});
	inicializarAutocompleteMaterial('<c:url value="/orcamento/material"/>');
	inserirMascaraCNPJ('cnpj');
	inserirMascaraCPF('cpf');
});

function inserirOrcamento(){
	toUpperCaseInput();
	toLowerCaseInput();
	
	var parametros = $('#formPedido').serialize();
	var request = $.ajax({
		type : "post",
		url : '<c:url value="/orcamento/inclusao"/>',
		data : parametros
	});

	request.done(function(response) {
		var erros = response.erros;
		var contemErro = erros != undefined;
		/*
		 * Ocultando no caso de que o usuario envie um novo request com a area
		 * de mensagem renderizada e deve ser um hide para que o bloco suma
		 * rapidamente apos novo request
		 */
		$('#bloco_mensagem').hide();

		var pedidoJson = response.pedido;
		var contemPedido =pedidoJson != undefined && pedidoJson != null;
		if (!contemErro && contemPedido) {
			/*
			 * Temos que ter esse campo oculto pois o campo Numero do Pedido na
			 * tela sera desabilitado e nao sera enviado no request.
			 */
			$('#idCliente').val(pedidoJson.idCliente);
			$('#idPedido').val(pedidoJson.id);
			$('#numeroPedido').val(pedidoJson.id);
			$('#numeroPedidoPesquisa').val(pedidoJson.id);
			$('#formEnvioOrcamento #idPedido').val(pedidoJson.id);
			
			$('#situacaoPedido').val(pedidoJson.situacaoPedido);
			$('#dataInclusao').val(pedidoJson.dataInclusaoFormatada);
			$('#proprietario').val(
					pedidoJson.proprietario.nome + ' - '
							+ pedidoJson.proprietario.email);
			$('#idVendedor').val(pedidoJson.proprietario.id);
			
			habilitar('#numeroPedido', false);
			gerarListaMensagemSucesso(new Array('O orçamento No. ' + pedidoJson.id + ' foi incluido com sucesso.'));

		} else if(!contemErro && !contemPedido) {
			gerarListaMensagemAlerta(['O usuario pode nao estar logado no sistema']);
		} else if(contemErro) {
			gerarListaMensagemErro(erros);
		}
	});

	request.fail(function(request, status) {
		alert('Falha inclusao do orcamento => Status da requisicao: ' + status);
	});

}
</script>
	
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>


	<form id="formVazio" method="get"></form>

	<form id="formPedido" action="<c:url value="/orcamento/inclusao"/>" method="post">
		<input type="hidden" id="idVendedor" name="pedido.proprietario.id" value="${pedido.proprietario.id}"/>
		<input type="hidden" id="idCliente" name="cliente.id" value="${cliente.id}"/>
		<input type="hidden" id="idPedido"  name="pedido.id" value="${pedido.id}"/>
		<input type="hidden" id="idRepresentada" name="pedido.representada.id" value="${idRepresentadaSelecionada}" />
	
	<fieldset>
		<legend>Orçamento</legend>
		<div class="label">Número:</div>
		<div class="input" style="width: 30%">
			<input type="text" id="numeroPedido" value="${pedido.id}" class="pesquisavel" style="width: 100%"/>
		</div>
		<div class="input" style="width: 2%">
				<input type="button" id="botaoPesquisaOrcamento"
					title="Pesquisar Pedido" value="" class="botaoPesquisarPequeno" />
			</div>
		<div class="label" style="width: 8%">Situação:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="situacaoPedido" name="pedido.situacaoPedido" 
					value="${pedido.situacaoPedido}" class="desabilitado" disabled="disabled" style="width: 75%"/>
			</div>
		<div class="label obrigatorio" >Fornecedor:</div>
		<div class="input" style="width: 30%">
			<select id="representada" id="representada" name="pedido.representada.id" style="width: 100%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="representada" items="${listaRepresentada}">
					<option value="${representada.id}"
						<c:if test="${representada.id eq idRepresentadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
				</c:forEach>
			</select>
		</div>
		<div class="label" style="width: 10%">Vendedor:</div>
		<div class="input" style="width: 40%">
			<input type="text" id="vendedor" name="vendedor.nome" value="${vendedorNome} - ${vendedorEmail}" disabled="disabled"
					class="uppercaseBloqueado desabilitado" style="width: 75%"/>
		</div>
			
		<div class="label obrigatorio">Cliente:</div>
		<div class="input" style="width: 30%">
			<input type="text" id="nomeCliente" name="cliente.nomeFantasia" value="${cliente.nomeFantasia}" class="pesquisavel" style="width: 100%"/>
			<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
		</div>
		<div class="input" style="width: 2%">
			<input type="button" id="botaoEditarCliente"
					title="Editar Cliente" value="" class="botaoEditar" />
		</div>
		
		<div class="label" style="width: 8%">CNPJ:</div>
		<div class="input" style="width: 12%">
			<input type="text" id="cnpj" name="cliente.cnpj" value="${cliente.cnpj}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 5%">CPF:</div>
		<div class="input" style="width: 20%">
			<input type="text" id="cpf" name="cliente.cpf" value="${cliente.cpf}" style="width: 60%"/>
		</div>
		
		<div class="label">Contato:</div>
		<div class="input" style="width: 30%">
			<input type="text" id="contato" name="contato.nome" value="${contato.nome}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Email:</div>
		<div class="input" style="width: 30%">
			<input type="text" id="email" name="contato.email" value="${contato.email}" style="width: 100%"
				class="apenasLowerCase uppercaseBloqueado lowerCase" />
		</div>
		
		<div class="label">DDD:</div>
		<div class="input" style="width: 5%">
			<input type="text" id="ddd" name="contato.ddd" value="${contato.ddd}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 8%">Telefone:</div>
		<div class="input" style="width: 16%">
			<input type="text" id="telefone" name="contato.telefone" value="${contato.telefone}" style="width: 100%"/>
		</div>
		<div class="bloco_botoes">
			<input type="button" id="botaoInserirOrcamento" title="Inserir Orçamento" value="" class="botaoInserir"/>
		</div>
	</fieldset>
	</form>
	
	<jsp:include page="/bloco/bloco_item_pedido.jsp" />
			
</body>
</html>