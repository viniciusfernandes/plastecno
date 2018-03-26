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
<script type="text/javascript" src="<c:url value="/js/tabela_handler.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/pedido/pedido.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/pedido/bloco_item_pedido.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js?${versaoCache}"/>"></script>

<script type="text/javascript">
$(document).ready(function() {
	scrollTo('${ancora}');

	var tabelaItemHandler = gerarTabelaItemPedido('<c:url value="/orcamento"/>');
	
	$("#botaoInserirItemPedido").click(function() {
		inserirOrcamento({
			urlInclusao:'<c:url value="/orcamento/inclusao"/>', 
			inserirItem:inserirItemPedido, 
			urlInclusaoItem:'<c:url value="/orcamento/item/inclusao"/>'
			});
	});
	
	$("#botaoInserirOrcamento").click(function() {
		inserirOrcamento({urlInclusao:'<c:url value="/orcamento/inclusao"/>'});
	});
	
	$("#botaoAnexarArquivo").click(function() {
		$('#botaoAnexarOculto').click();
	});
	
	$("#botaoPesquisaOrcamento").click(function() {
		var idPedido = $('#numeroPedido').val();
		if (isEmpty(idPedido)) {
			return;
		} 
		var form = document.getElementById('formVazio');
		form.action = '<c:url value="/orcamento/"/>'+idPedido;
		form.submit();
	});
	
	$("#botaoListarOrcamento").click(function() {
		var form = document.getElementById('formVazio');
		adicionarInputHiddenFormulario('formVazio', 'idCliente', $('#idCliente').val());
		adicionarInputHiddenFormulario('formVazio', 'idVendedor', $('#idVendedor').val());
		adicionarInputHiddenFormulario('formVazio', 'idRepresentada', $('#idRepresentada').val());

		form.action = '<c:url value="/orcamento/listagem"/>';
		form.submit();
	});
	
	$("#botaoLimparOrcamento").click(function() {
		var form = document.getElementById('formVazio');
		form.action = '<c:url value="/orcamento"/>';
		form.submit();
	});
	
	
	$('#botaoEnviarOrcamento').click(function (){
		var enviarOrcamento = function(){
			var idPedido = $('#numeroPedido').val();
			if (isEmpty(idPedido)) {
				return;
			} 
			
			var tot = 0;
			var files  = document.getElementById("botaoAnexarOculto").files;
			for(var i =0; i< files.length;i++){
				tot += files[i].size;
			}
			var mb = 1000000;
			tot = tot/mb;
			
			var max = 20; 
			if(tot > max){
				var mens = new Array();
				mens[0]='O tamanho dos arquivos anexados é '+tot+' Mb, mas não pode ultrapassar '+max+' Mb.';
				for(var i =0; i< files.length;i++){
					mens[i+1] = files[i].name+' '+files[i].size/mb+' Mb';
				}
				gerarListaMensagemAlerta(mens);
				return;
			}
			
			var url = '<c:url value="orcamento/temporario/id"/>';
			var request = $.ajax({
				type : "post",
				url : url,
				data : {'idOrcamento':idPedido}
			});
			
			request.done(function(response) {
				return;
			});
			
			request.always(function(response) {
				var upload = $.ajax({
						      url: '<c:url value="/orcamento/envio/anexo"/>',
						      type: 'post',
						      data: new FormData(document.getElementById('formAnexo')),
						      processData: false,
						      contentType: false
						    });
				upload.done(function (response){
					if(response.sucesso != undefined && response.sucesso != null){
						limparTela();
						gerarListaMensagemSucesso(response.sucesso);
					}else {
						gerarListaMensagemErro(response.erros);
					}
				});
				
				upload.fail(function(request, status, erro){
					gerarListaMensagemSucesso(["Falha no envio do orçamento No. "+idPedido+"."]);
				});
			});
			

			request.fail(function(request, status) {
				alert('Falha envio do orcamento => Status da requisicao: ' + status);
			});
		};
		
		inserirOrcamento({
			urlInclusao:'<c:url value="/orcamento/inclusao"/>', 
			enviar: enviarOrcamento
		});
	});
	
	$('#botaoAceitarOrcamento').click(function (){
		var idPedido = $('#numeroPedido').val();
		if (isEmpty(idPedido)) {
			return;
		} 
		var form = document.getElementById('formVazio');
		form.action = '<c:url value="/orcamento/aceite/"/>'+idPedido;
		form.method = 'post';
		form.submit();
	});

	$('#botaoAceitarListaItemOrcamento').click(function (){
		var form = document.getElementById('formPesquisa');
		form.action = '<c:url value="/orcamento/aceite/listaitem"/>';
		form.method = 'post';
		form.submit();
	});
	
	$("#botaoPDFOrcamento").click(function() {
		var idPedido = $('#numeroPedido').val();
		if (isEmpty(idPedido)) {
			return;
		} 
		var form = document.getElementById('formVazio');
		adicionarInputHiddenFormulario('formVazio', 'idPedido', idPedido);
		form.action = '<c:url value="/orcamento/pdf"/>';
		form.method = 'get';
		form.submit();
	});
	
	$("#botaoCancelarOrcamento").click(function() {
		var idPedido = $('#numeroPedido').val();
		if (isEmpty(idPedido)) {
			return;
		} 
		var form = document.getElementById('formVazio');
		form.action = '<c:url value="/orcamento/cancelamento/"/>'+idPedido ;
		form.method = 'post';
		form.submit();
	});
	
	$("#botaoCopiarOrcamento").click(function() {
		var idPedido = $('#numeroPedido').val();
		if (isEmpty(idPedido)) {
			return;
		} 
		var form = document.getElementById('formVazio');
		form.action = '<c:url value="/orcamento/copia/"/>'+idPedido ;
		form.method = 'post';
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
		
		$('#idClienteListagem').val(cliente.id);
		$('#idVendedorListagem').val(cliente.vendedor.id);
	});
	
	inicializarAutocompleteContatoCliente('<c:url value="/orcamento/contatocliente"/>', 'contato', function(contato){
		$('#contato').val(contato.nome);
		$('#email').val(contato.email);
		$('#ddd').val(contato.ddd);
		$('#telefone').val(contato.telefone);
	});
	
	inicializarAutocompleteMaterial('<c:url value="/orcamento/material"/>');
	
	autocompletar({
		url : '<c:url value="/orcamento/transportadora/listagem"/>',
		campoPesquisavel : 'transportadora',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaTransportadora',
		selecionarItem : function(itemLista) {
			$('#idTransportadora').val(itemLista.id);
		}
	});
	
	inserirMascaraCNPJ('cnpj');
	inserirMascaraCPF('cpf');
	inserirMascaraNumerica('validade', '999');
	inserirMascaraNumerica('ddd', '999');
	inserirMascaraNumerica('telefone', '999999999');
	inserirMascaraMonetaria('frete', 7);

	<jsp:include page="/bloco/bloco_paginador.jsp" />

});
</script>
	
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
		<%--Esse form foi criado apenas para a paginacao --%>
		<form id="formPesquisa" action="<c:url value="/orcamento/listagem"/>" method="get">
			<input type="hidden" id="idClienteListagem" name="idCliente" value="${cliente.id}"/> 
			<input type="hidden" id="idRepresentadaListagem" name="idRepresentada" value="${idRepresentadaSelecionada}"/>
			<input type="hidden" id="idVendedorListagem" name="idVendedor" value="${pedido.proprietario.id}"/> 
		</form>

	<form id="formVazio" method="get">
		<input type="hidden" name="pedido.tipoPedido" value="${pedido.tipoPedido}"/>
	</form>
	
	<form id="formPedido" action="<c:url value="/orcamento/inclusao"/>" method="post">
		<%-- Os campos ocultos estao presentes pois no formulario eles estao como campode apenas leitura e precisam ser enviados no submit --%>
		<input type="hidden" id="idVendedor" name="pedido.proprietario.id" value="${pedido.proprietario.id}"/>
		<input type="hidden" id="idCliente" name="cliente.id" value="${cliente.id}"/>
		<input type="hidden" id="idPedido"  name="pedido.id" value="${pedido.id}"/>
		<input type="hidden" id="idTransportadora"  name="pedido.transportadora.id" value="${pedido.transportadora.id}"/>
		<input type="hidden" id="idSituacaoPedido"  name="pedido.situacaoPedido" value="${pedido.situacaoPedido}"/>
		<input type="hidden" id="idRepresentada"  name="pedido.representada.id" value="${pedido.representada.id}"/>
		<input type="hidden" name="pedido.tipoPedido" value="${pedido.tipoPedido}"/>
	<fieldset>
		<legend>::: Orçamento :::</legend>
		
		<div class="label" style="width: 56%">Dt. Envio:</div>
		<div class="input" style="width: 30%">
			<input type="text" id="dataEnvio" value="${pedido.dataEnvioFormatada}"  class="desabilitado" disabled="disabled" style="width: 100%" />
		</div>
		<div class="label">Núm.:</div>
		<div class="input" style="width: 7%">
			<input type="text" id="numeroPedido" value="${pedido.id}" class="pesquisavel" style="width: 100%"/>
		</div>
		<div class="input" style="width: 2%">
				<input type="button" id="botaoPesquisaOrcamento"
					title="Pesquisar Pedido" value="" class="botaoPesquisarPequeno" />
		</div>
		<div class="input" style="width: 1%">
				<input type="button" id="botaoCopiarOrcamento"
					title="Copiar Orçamento" value="" class="botaoCopiarPequeno" />
		</div>
		<div class="label" style="width: 8%">Núm. Cli.:</div>
		<div class="input" style="width: 11%">
			<input type="text" id="numeroPedidoCliente" name="pedido.numeroPedidoCliente" 
				value="${pedido.numeroPedidoCliente}" class="pesquisavel" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Situação:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="situacaoPedido"  value="${pedido.situacaoPedido}" 
					class="desabilitado" disabled="disabled" style="width: 75%"/>
			</div>
		<div class="label obrigatorio" >Fornecedor:</div>
		<div class="input" style="width: 30%">
			<select id="representada" name="pedido.representada.id" style="width: 100%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="representada" items="${listaRepresentada}">
					<option value="${representada.id}"
						<c:if test="${representada.id eq idRepresentadaSelecionada or (representada.id eq pedido.representada.id)}">selected</c:if>>${representada.nomeFantasia}</option>
				</c:forEach>
			</select>
		</div>
		<div class="label" style="width: 10%">Vendedor:</div>
		<div class="input" style="width: 40%">
			<input type="text" id="vendedor"  value="${pedido.vendedor.nome} - ${pedido.vendedor.email}" disabled="disabled"
					class="uppercaseBloqueado desabilitado" style="width: 75%"/>
		</div>
			
		<div class="label obrigatorio">Cliente:</div>
		<div class="input" style="width: 30%">
			<input type="text" id="nomeCliente" name="cliente.nomeFantasia" value="${cliente.nomeCompleto}" class="pesquisavel" style="width: 100%"/>
			<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
		</div>
		<div class="input" style="width: 2%">
			<input type="button" id="botaoListarOrcamento" title="Pesquisar Orçamento" value="" class="botaoPesquisarPequeno" />
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
			<div class="suggestionsBox" id="containerPesquisaContatoCliente" style="display: none; width: 30%"></div>
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
		<div class="label" style="width: 10%">Email Cont.:</div>
		<div class="input" style="width: 40%">
			<input type="checkbox" id="clienteNotificadoVenda" name="pedido.clienteNotificadoVenda"
				<c:if test="${pedido.clienteNotificadoVenda}">checked</c:if> class="checkbox" style="width: 4%"/>
		</div>
		<div class="label">Tipo Entrega:</div>
			<div class="input" style="width: 30%">
				<select id="tipoEntrega" name="pedido.tipoEntrega" style="width: 100%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="tipoEntrega" items="${listaTipoEntrega}">
						<option value="${tipoEntrega}"
							<c:if test="${tipoEntrega eq pedido.tipoEntrega}">selected</c:if>>${tipoEntrega.descricao}</option>
					</c:forEach>
				</select>
			</div>
		<div class="label" style="width: 10%">Transportadora:</div>
		<div class="input" style="width: 30%">
			<input type="text" id="transportadora" name="pedido.transportadora.nomeFantasia" value="${pedido.transportadora.nomeFantasia}" style="width: 100%"/>
			<div class="suggestionsBox" id="containerPesquisaTransportadora" style="display: none; width: 30%"></div>
		</div>
		<div class="label">Frete (R$):</div>
		<div class="input" style="width: 30%">
			<input id="frete" name="pedido.valorFrete" value="${pedido.valorFrete}" style="width: 100%" />
		</div>
		<div class="label" style="width: 10%">Pagamento:</div>
		<div class="input" style="width: 30%">
			<input type="text" id="pagamento" name="pedido.formaPagamento" value="${pedido.formaPagamento}" style="width: 100%"/>
		</div>
		<div class="label">Observação:</div>
			<div class="input areatexto" style="width: 71%">
				<textarea id="obervacao" name="pedido.observacao"
					style="width: 100%">${pedido.observacao}</textarea>
			</div>
		<div class="bloco_botoes">
			<input type="button" id="botaoInserirOrcamento" title="Inserir Orçamento" value="" class="botaoInserir"/>
			<input type="button" id="botaoPDFOrcamento" value="" title="PDF Orçamento" class="botaoPDF" />
			<input type="button" id="botaoLimparOrcamento" value="" title="Limpar Orçamento" class="botaoLimpar" />
			<input  type="button"id="botaoCancelarOrcamento" value="" title="Cancelar Orçamento" class="botaoCancelar" />
		</div>
	</fieldset>
	</form>
	
	<jsp:include page="/bloco/bloco_item_pedido.jsp" />
	<div class="bloco_botoes">
		<input type="button" id="botaoEnviarOrcamento" title="Enviar Orçamento" value="" class="botaoEnviarEmail" />
		<form id="formAnexo" enctype="multipart/form-data">
			<input type="button" id="botaoAnexarArquivo" title="Anexar Arquivo" value="" class="botaoAnexar" />
			<input type="file" id="botaoAnexarOculto" style="display: none;" name="anexo[]" multiple="multiple"/>
		</form>
		<input type="button" id="botaoAceitarOrcamento" title="Aceitar Orçamento" value="" class="botaoAceitar" />
	</div>
	
	<jsp:include page="/bloco/bloco_listagem_item_pedido.jsp"/>
	
</body>
</html>