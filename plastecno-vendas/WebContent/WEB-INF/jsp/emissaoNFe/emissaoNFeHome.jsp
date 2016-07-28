<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>

<style type="text/css">
fieldset .fieldsetInterno {
	margin: 0 1% 1% 1%;
}
fieldset .fieldsetInterno legend {
	font-size: 10px;
}
</style>
<script type="text/javascript">

var numeroProdutoEdicao = null;

$(document).ready(function() {
	
	$("#botaoPesquisaPedido").click(function() {
		if(isEmpty($('#idPedido').val())){
			return;
		}
		$('#formPesquisa #idPedidoPesquisa').val($('#idPedido').val());
		$('#formPesquisa').submit();
	});
	
	$("#tipoTributacaoICMS").change(function() {
		if("00" === $(this).val()){
			$('.icms00').fadeIn();
			$('.icms10').fadeOut();
		} else if("10" === $(this).val()){
			$('.icms10').fadeIn();
			$('.icms00').fadeOut();
		} 
	});

	$("#botaoInserirDuplicata").click(function() {
		inserirDuplicata();
	});
	
	$('#bloco_logradouro').addClass('fieldsetInterno');

	$('#botaoEmitirNF').click(function(){
		gerarInputDuplicata();
		gerarInputProdutoServico();
		$('#formEmissao').submit();
	});
	
	$('#botaoInserirICMS').click(function(){
		gerarInputICMS();
		$('#bloco_tributos #bloco_icms').fadeOut();
	});
	
	$('#botaoInserirIPI').click(function(){
		gerarInputIPI();
		$('#bloco_tributos #bloco_ipi').fadeOut();
	});
	
	$('#botaoInserirPIS').click(function(){
		gerarInputPIS();
		$('#bloco_tributos #bloco_pis').fadeOut();
	});
	
	$('#botaoInserirCOFINS').click(function(){
		gerarInputCOFINS();
		$('#bloco_tributos #bloco_cofins').fadeOut();
	});
	
	$('#bloco_tributos').fadeOut();
	
	autocompletar({
		url : '<c:url value="/cliente/listagem/nome"/>',
		campoPesquisavel : 'nomeCliente',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaCliente',
		selecionarItem : function(itemLista) {
			// Vamos utilizar a conversao de pedido/cliente/1, onde o ultimo
			// termo se refere ao ID do cliente
			var request = $.ajax({
				type : "get",
				url : '<c:url value="/cliente/serializacao"/>'+ '/' + itemLista.id
			});

			request.done(function(response) {
				var erros = response.erros;
				var contemErro = erros != undefined;
				if (!contemErro) {
					var cliente = response.cliente;
					$('#email').val(cliente.email);
					$('#cnpj').val(cliente.cnpj);
					$('#cpf').val(cliente.cpf);
					$('#inscricaoEstadual').val(cliente.inscricaoEstadual);
					$('#telefone').val(cliente.telefone);
					$('#nomeCliente').val(cliente.razaoSocial);

				} else if (erros != undefined) {
					gerarListaMensagemErro(erros);
				}

			});
			
			request.fail(function(request, status, excecao) {
				var mensagem = 'Falha na pesquisa do autocomplete para o campo: '+ idCampoPesquisavel;
				mensagem += ' para a URL ' + url;
				mensagem += ' contendo o valor de requisicao ' + parametro;
				mensagem += ' => Excecao: ' + excecao;
				gerarListaMensagemErro(new Array(mensagem));
			});
		}
	});
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
	inserirMascaraDataAmericano('dataVencimentoDuplicata');

});

function gerarInputICMS(){
	var form = document.getElementById('formEmissao');
	var input = null;

	var produto = 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe';
	var icms = 'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms';
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = produto+'.cfop';
	input.value = document.getElementById('cfop').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.codigoSituacaoTributaria';
	input.value = document.getElementById('tipoTributacaoICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.aliquota';
	input.value = document.getElementById('aliquotaICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.valor';
	input.value = document.getElementById('valorICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.modalidadeDeterminacaoBC';
	input.value = document.getElementById('modBCICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.modalidadeDeterminacaoBCST';
	input.value = document.getElementById('modBCSTICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.percentualMargemValorAdicionadoICMSST';
	input.value = document.getElementById('percValSTICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.percentualReducaoBC';
	input.value = document.getElementById('percRedBCSTICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.valorBC';
	input.value = document.getElementById('valorBCICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.valorBCST';
	input.value = document.getElementById('valorBCSTICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.aliquotaST';
	input.value = document.getElementById('aliquotaSTICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.valorST';
	input.value = document.getElementById('valorSTICMS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = icms+'.motivoDesoneracao';
	input.value = document.getElementById('motDesonerICMS').value;
	form.appendChild(input);
}

function gerarInputIPI(){
	var form = document.getElementById('formEmissao');
	var input = null;

	var ipi = 'nf.listaItem['+numeroProdutoEdicao+'].tributos.ipi';
	var tipoIpi = ipi+'.tipoIpi';
		
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoIpi+'.aliquota';
	input.value = document.getElementById('aliquotaIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoIpi+'.codigoSituacaoTributaria';
	input.value = document.getElementById('codSitTribIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoIpi+'.valor';
	input.value = document.getElementById('valorBCIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoIpi+'.valorBC';
	input.value = document.getElementById('valorBCIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoIpi+'.quantidadeUnidadeTributavel';
	input.value = document.getElementById('qtdeUnidTribIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoIpi+'.valorUnidadeTributavel';
	input.value = document.getElementById('valorUnidTribIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = ipi+'.classeEnquadramento';
	input.value = document.getElementById('clEnquadramentoIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = ipi+'.codigoEnquadramento';
	input.value = document.getElementById('codEnquadramentoIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = ipi+'.cnpjProdutor';
	input.value = document.getElementById('cnpjProdIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = ipi+'.codigoSeloControle';
	input.value = document.getElementById('codSeloContrIPI').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = ipi+'.quantidadeSeloControle';
	input.value = document.getElementById('qtdeSeloContrIPI').value;
	form.appendChild(input);
};

function gerarInputPIS(){
	var form = document.getElementById('formEmissao');
	var input = null;

	var pis = 'nf.listaItem['+numeroProdutoEdicao+'].tributos.pis';
	var tipoPis = pis+'.tipoPis';
		
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoPis+'.aliquota';
	input.value = document.getElementById('aliquotaPIS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoPis+'.codigoSituacaoTributaria';
	input.value = document.getElementById('codSitTribPIS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoPis+'.quantidadeVendida';
	input.value = document.getElementById('qtdeVendidaPIS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoPis+'.valor';
	input.value = document.getElementById('valorPIS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoPis+'.valorBC';
	input.value = document.getElementById('valorBCPIS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoPis+'.valorAliquota';
	input.value = document.getElementById('valorAliquotaPIS').value;
	form.appendChild(input);
};

function gerarInputCOFINS(){
	var form = document.getElementById('formEmissao');
	var input = null;

	var cofins = 'nf.listaItem['+numeroProdutoEdicao+'].tributos.cofins';
	var tipoCofins = cofins+'.tipoCofins';
		
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoCofins+'.aliquota';
	input.value = document.getElementById('aliquotaCOFINS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoCofins+'.codigoSituacaoTributaria';
	input.value = document.getElementById('codSitTribCOFINS').value;
	form.appendChild(input);

	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoCofins+'.quantidadeVendida';
	input.value = document.getElementById('qtdeVendidaCOFINS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoCofins+'.valor';
	input.value = document.getElementById('valorCOFINS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoCofins+'.valorAliquota';
	input.value = document.getElementById('valorAliquotaCOFINS').value;
	form.appendChild(input);
	
	input = document.createElement('input');
	input.type = 'hidden';
	input.name = tipoCofins+'.valorBC';
	input.value = document.getElementById('valorBCCOFINS').value;
	form.appendChild(input);
	
};

function gerarInputDuplicata(){
	var tabela = document.getElementById('tabela_duplicata');
	var linhas = tabela.tBodies[0].rows;
	if(linhas.length <= 0){
		return;
	}
	
	var form = document.getElementById('formEmissao');
	var input = null;
	var celulas = null;
	for (var i = 0; i < linhas.length; i++) {
		celulas = linhas[i].cells;
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.cobrancaNFe.listaDuplicata['+i+'].numero';
		input.value = celulas[0].innerHTML;
		form.appendChild(input);
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.cobrancaNFe.listaDuplicata['+i+'].dataVencimento';
		input.value = celulas[1].innerHTML;
		form.appendChild(input);
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.cobrancaNFe.listaDuplicata['+i+'].valor';
		input.value = celulas[2].innerHTML;
		form.appendChild(input);
	}
}

function gerarInputProdutoServico(){
	var tabela = document.getElementById('tabela_produtos');
	var linhas = tabela.tBodies[0].rows;
	if(linhas.length <= 0){
		return;
	}
	
	var form = document.getElementById('formEmissao');
	var input = null;
	var celulas = null;
	for (var i = 0; i < linhas.length; i++) {
		celulas = linhas[i].cells;
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].numeroItem';
		input.value = celulas[0].innerHTML;
		form.appendChild(input);
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].produtoServicoNFe.codigo';
		input.value = celulas[1].innerHTML;
		form.appendChild(input);

		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].produtoServicoNFe.descricao';
		input.value = celulas[1].innerHTML;
		form.appendChild(input);
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].produtoServicoNFe.ncm';
		input.value = celulas[2].innerHTML;
		form.appendChild(input);

		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].produtoServicoNFe.unidadeComercial';
		input.value = celulas[4].innerHTML;
		form.appendChild(input);
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].produtoServicoNFe.quantidadeComercial';
		input.value = celulas[5].innerHTML;
		form.appendChild(input);
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].produtoServicoNFe.quantidadeTributavel';
		input.value = celulas[5].innerHTML;
		form.appendChild(input);
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].produtoServicoNFe.valorUnitarioComercializacao';
		input.value = celulas[6].innerHTML;
		form.appendChild(input);
		
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'nf.listaItem['+i+'].produtoServicoNFe.valorTotalBruto';
		input.value = celulas[7].innerHTML;
		form.appendChild(input);
	}
}

function inserirDuplicata(){
	var numero = $('#bloco_duplicata #numeroDuplicata').val();
	var vencimento = $('#bloco_duplicata #dataVencimentoDuplicata').val();
	var valor = $('#bloco_duplicata #valorDuplicata').val();
	
	if(isEmpty(numero) || isEmpty(vencimento) || isEmpty(valor)){
		return;
	}
	
	var tabela = document.getElementById('tabela_duplicata');
	var linha = tabela.tBodies[0].insertRow(0);
	
	linha.insertCell(0).innerHTML = numero;
	linha.insertCell(1).innerHTML = vencimento;
	linha.insertCell(2).innerHTML = valor;
	linha.insertCell(3).innerHTML = '<input type="button" title="Remover Duplicata" value="" class="botaoRemover" onclick="removerDuplicata(this);"/>';
	
	$('#bloco_duplicata input:text').val('');
}

function removerDuplicata(botao){
	 var linha = $(botao).closest("tr")[0];
	 document.getElementById('tabela_duplicata').deleteRow(linha.rowIndex);
}

function inicializarFiltro() {
	$("#filtroSigla").val($("#sigla").val());
	$("#filtroDescricao").val($("#descricao").val());	
}

function inicializarModalCancelamento(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja DESATIVAR esse ramo de atividade?',
		confirmar: function(){
			$(botao).closest('form').submit();	
		}
	});
}

function editarTributos(linha){
	var celulas = linha.cells;
	<%-- Estamos supondo que a sequencia do item do pedido eh unica --%>
	numeroProdutoEdicao = celulas[0].innerHTML;
	
	<%-- Aqui estamos diminuindo o valor da numero do item pois a indexacao das listas comecam do  zero --%>
	--numeroProdutoEdicao;
	
	$('#bloco_tributos #valorBCICMS').val(celulas[8].innerHTML);
	$('#bloco_tributos #valorICMS').val(celulas[9].innerHTML);
	$('#bloco_tributos #aliquotaICMS').val(celulas[11].innerHTML);
	
	$('#bloco_tributos #valorBCIPI').val(celulas[10].innerHTML);
	$('#bloco_tributos #aliquotaIPI').val(celulas[12].innerHTML);
	
	$('#bloco_tributos').fadeIn();
};
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
	<form id="formVazio" ></form>

	<form id="formPesquisa" action="<c:url value="/emissaoNFe/pedido"/>"
		method="get">
		<input type="hidden" id="idPedidoPesquisa" name="idPedido" value="${idPedido}"/>
	</form>
	<form id="formEmissao" action="<c:url value="/emissaoNFe/emitirNFe"/>"
		method="post">
		<fieldset>
			<legend>::: Dados da NF-e :::</legend>
			<div class="label">Pedido:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="idPedido" name="idPedido" value="${idPedido}"
					class="pesquisavel" />
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoPesquisaPedido"
					title="Pesquisar Pedido" value="" class="botaoPesquisarPequeno" />
			</div>
			<div class="input" style="width: 60%">
			</div>
			<div class="label obrigatorio">Tipo Documento:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado"  class="semprehabilitado" style="width: 100%">
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Forma Pagamento:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado" name="nf.identificacaoNFe.indicadorFormaPagamento" class="semprehabilitado" style="width: 100%">
					<c:forEach var="formaPagamento" items="${listaTipoFormaPagamento}">
						<option value="${formaPagamento.codigo}" <c:if test="${formaPagamento eq formaPagamentoPadrao}">selected</c:if>>${formaPagamento.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Forma Emissão:</div>
			<div class="input" style="width: 20%">
				<select name="nf.identificacaoNFe.tipoEmissao" class="semprehabilitado" style="width: 50%">
					<c:forEach var="tipoEmissao" items="${listaTipoEmissao}">
						<option value="${tipoEmissao.codigo}" <c:if test="${tipoEmissao eq tipoEmissaoPadrao}">selected</c:if>>${tipoEmissao.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Finalidade Emissão:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.finalidadeEmissao"
					style="width: 100%" class="semprehabilitado">
					<c:forEach var="finalidade" items="${listaTipoFinalidadeEmissao}">
						<option value="${finalidade.codigo}" <c:if test="${finalidade eq finalidadeEmissaoPadrao}">selected</c:if>>${finalidade.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Tipo Impressão:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado"
					style="width: 100%" class="semprehabilitado">
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Consumidor Final:</div>
			<div class="input" style="width: 20%">
				<select id="pedidoAssociado" 
					style="width: 50%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Destino Operação:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado" 
					style="width: 100%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Natureza Operação:</div>
			<div class="input" style="width: 50%">
				<select id="pedidoAssociado"
					style="width: 80%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Info. Adicionais.:</div>
			<div class="input areatexto" style="width: 70%">
				<textarea name="nf.informacoesAdicionaisNFe.informacoesAdicionaisInteresseFisco" style="width: 100%"></textarea>
			</div>
		</fieldset>
		
		<fieldset>
			<legend>::: Destinatário :::</legend>
			<div class="label obrigatorio">Razão Social/Nome:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="nomeCliente" name="nf.identificacaoDestinatarioNFe.nomeFantasia" value="${cliente.razaoSocial}" class="pesquisavel" style="widows: 60%"/>
				<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
			</div>
			
			<div class="label">CNPJ:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="cnpj" name="nf.identificacaoDestinatarioNFe.cnpj"
					value="${cliente.cnpj}" class="pesquisavel" />
			</div>
			<div class="label">Insc. Estadual:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="inscricaoEstadual"
					name="nf.identificacaoDestinatarioNFe.inscricaoEstadual"
					value="${cliente.inscricaoEstadual}"
					style="width: 40%; text-align: right;" />
			</div>
			<div class="label">CPF:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="cpf" name="nf.identificacaoDestinatarioNFe.cpf"
					value="${cliente.cpf}" class="pesquisavel" />
			</div>
			<div class="label">Telefone:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="telefone" name="cliente.cpf"
					value="${cliente.contatoPrincipal.telefoneFormatado}" class="pesquisavel" />
			</div>
			<div class="label">Email:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="email" name="nf.identificacaoDestinatarioNFe.email"
					value="${cliente.email}" class="apenasLowerCase uppercaseBloqueado lowerCase" />
			</div>
			<jsp:include page="/bloco/bloco_logradouro.jsp"></jsp:include>
		</fieldset>
		
		<fieldset>
			<legend>::: Produtos e Serviços :::</legend>
			
			<table id="tabela_produtos" class="listrada">
				<thead>
					<tr>
						<th>Item</th>
						<th>Desc.</th>
						<th>NCM</th>
						<th>CFOP</th>
						<th>Venda</th>
						<th>Qtde.</th>
						<th>Unid.(R$)</th>
						<th>Tot.(R$)</th>
						<th>BC ICMS</th>
						<th>V. ICMS(R$)</th>
						<th>V IPI.(R$)</th>
						<th>Aliq. ICMS(R$)</th>
						<th>Aliq. IPI(R$)</th>
						<th style="width: 2%">Ações</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${listaItem}" varStatus="count">
						<tr>
							<td>${item.sequencial}</td>
							<td>${item.descricaoSemFormatacao}</td>
							<td>${item.ncm}</td>
							<td>null</td>
							<td>${item.tipoVenda}</td>
							<td>${item.quantidade}</td>
							<td>${item.precoUnidade}</td>
							<td>${item.valorTotal}</td>
							<td>${item.valorTotal}</td>
							<td>${item.valorICMS}</td>
							<td>null</td>
							<td>${item.aliquotaICMS}</td>
							<td>${item.aliquotaIPI}</td>
							<td>
								<input type="button" value="" title="Editar Tributos" class="botaoDinheiroPequeno" onclick="editarTributos(this.parentNode.parentNode);"/>
							</td>
							
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<fieldset id="bloco_tributos" class="fieldsetInterno">
				<legend class="fieldsetInterno">::: Tributos :::</legend>
				<div class="label">CFOP:</div>
				<div class="input" style="width: 80%">
					<input id="cfop" type="text" name="cfop" style="width: 10%"/>
				</div>
				
				<fieldset id="bloco_icms" class="fieldsetInterno">
					<legend>::: ICMS :::</legend>
					<div class="label">Regime:</div>
					<div class="input" style="width: 10%">
						<select id="pedidoAssociado" 
							style="width: 100%" class="semprehabilitado">
							<c:forEach var="icms" items="${listaRegime}">
								<option value="${icms.codigo}">${icms.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="label">Situação Tribut.:</div>
					<div class="input" style="width: 50%">
						<select id="tipoTributacaoICMS" 
							style="width: 80%" class="semprehabilitado">
							<c:forEach var="icms" items="${listaTipoTributacaoICMS}">
								<option value="${icms.codigo}">${icms.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="label">Origem:</div>
					<div class="input" style="width: 70%">
						<select id="pedidoAssociado" 
							style="width: 100%" class="semprehabilitado">
							<c:forEach var="origem" items="${listaTipoOrigemMercadoria}">
								<option value="${origem.codigo}">${origem.descricao}</option>
							</c:forEach>
						</select>
					</div>
					
					<div class="icms00 label">Modalidade:</div>
					<div class="icms00 input" style="width: 70%">
						<select id="modBCICMS" style="width: 100%" class="icms00 semprehabilitado">
							<c:forEach var="modalidade" items="${listaTipoModalidadeDeterminacaoBCICMS}">
								<option value="${modalidade.codigo}">${modalidade.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorBCICMS" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Alíquota:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="aliquotaICMS" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Valor:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorICMS" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div class="icms00 label">Modalidade ST:</div>
					<div class="icms00 input" style="width: 70%">
						<select id="modBCSTICMS" style="width: 100%" class="icms00 semprehabilitado">
							<c:forEach var="modalidade" items="${listaTipoModalidadeDeterminacaoBCICMSST}">
								<option value="${modalidade.codigo}">${modalidade.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Perc. Marg. Valor ST:</div>
					<div class="input" style="width: 70%">
						<input id="percValSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Perc. Redução BC ST:</div>
					<div class="input" style="width: 70%">
						<input id="percRedBCSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Valor BC ST:</div>
					<div class="input" style="width: 70%">
						<input id="valorBCSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Alíquota ST:</div>
					<div class="input" style="width: 70%">
						<input id="aliquotaSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Valor ST:</div>
					<div class="input" style="width: 70%">
						<input id="valorSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div class="icms00 label">Mot. Desoneração:</div>
					<div class="icms00 input" style="width: 70%">
						<select id="motDesonerICMS" style="width: 100%" class="icms00 semprehabilitado">
							<c:forEach var="motivo" items="${listaTipoMotivoDesoneracao}">
								<option value="${motivo.codigo}">${motivo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirICMS" title="Inserir ICMS do Produto" value="" class="botaoInserir"/>
					</div>
				</fieldset>
				
				<fieldset class="fieldsetInterno">
					<legend>::: IPI :::</legend>
					<div id="bloco_ipi">
					<div class="label">Situação Tribut.:</div>
					<div class="input" style="width: 70%">
						<select id="codSitTribIPI" style="width: 100%" >
							<c:forEach var="tipo" items="${listaTipoTributacaoIPI}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 70%">
						<input id="valorBCIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Alíquota:</div>
					<div class="input" style="width: 70%">
						<input id="aliquotaIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Qtde. unid. Tributável:</div>
					<div class="input" style="width: 70%">
						<input id="qtdeUnidTribIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">valor Unid. Tributável:</div>
					<div class="input" style="width: 70%">
						<input id="valorUnidTribIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Cl. Enquadramento:</div>
					<div class="input" style="width: 70%">
						<input id="clEnquadramentoIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Cod. Enquadramento:</div>
					<div class="input" style="width: 70%">
						<input id="codEnquadramentoIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">CNPJ Produtor:</div>
					<div class="input" style="width: 70%">
						<input id="cnpjProdIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Cod. Selo Controle:</div>
					<div class="input" style="width: 70%">
						<input id="codSeloContrIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Qtde. Selo Controle:</div>
					<div class="input" style="width: 70%">
						<input id="qtdeSeloContrIPI" type="text" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirIPI" title="Inserir IPI do Produto" value="" class="botaoInserir"/>
					</div>
					</div>
				</fieldset>
				
				<fieldset id="bloco_pis" class="fieldsetInterno">
					<legend>::: PIS :::</legend>
					<div class="label">Situação Tribut.:</div>
					<div class="input" style="width: 70%">
						<select id="codSitTribPIS" 
							style="width: 100%">
							<c:forEach var="tipo" items="${listaTipoTributacaoPIS}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 70%">
						<input id="valorBCPIS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Alíquota(%):</div>
					<div class="input" style="width: 70%">
						<input id="aliquotaPIS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Alíquota(R$):</div>
					<div class="input" style="width: 70%">
						<input id="valorAliquotaPIS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Qtde.Vendida:</div>
					<div class="input" style="width: 70%">
						<input id="qtdeVendidaPIS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor:</div>
					<div class="input" style="width: 70%">
						<input id="valorPIS" type="text" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirPIS" title="Inserir PIS do Produto" value="" class="botaoInserir"/>
					</div>
				</fieldset>
				
				<fieldset id="bloco_cofins" class="fieldsetInterno">
					<legend>::: COFINS :::</legend>
					<div class="label">Situação Tribut.:</div>
					<div class="input" style="width: 70%">
						<select id="codSitTribCOFINS" style="width: 100%">
							<c:forEach var="tipo" items="${listaTipoTributacaoCOFINS}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 70%">
						<input id="valorBCCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Alíquota(%):</div>
					<div class="input" style="width: 70%">
						<input id="aliquotaCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Alíquota(R$):</div>
					<div class="input" style="width: 70%">
						<input id="valorAliquotaCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Qtde. Vendida:</div>
					<div class="input" style="width: 70%">
						<input id="qtdeVendidaCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor:</div>
					<div class="input" style="width: 70%">
						<input id="valorCOFINS" type="text" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirCOFINS" title="Inserir COFINS do Produto" value="" class="botaoInserir"/>
					</div>
				</fieldset>
			</fieldset>
		</fieldset>	
		
		<div class="bloco_botoes">
			<input type="button" id="botaoEmitirNF" title="Emitir Nota Fiscal" value="" class="botaoEnviarEmail"/>
		</div>
		
		<fieldset>
			<legend>::: Cobrança :::</legend>
			<div class="label">Número:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.numero" style="widows: 60%"/>
			</div>
			
			<div class="label">Valor Original:</div>
			<div class="input" style="width: 55%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorOriginal" style="width: 20%"/>
			</div>
			<div class="label">Valor Desconto:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorDesconto" />
			</div>
			<div class="label">Valor Líquido:</div>
			<div class="input" style="width: 55%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorLiquido" style="width: 20%"/>
			</div>
			
			<fieldset id="bloco_duplicata" class="fieldsetInterno">
				<legend>::: Duplicata :::</legend>
				<div class="label">Número:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="numeroDuplicata"/>
				</div>
				
				<div class="label">Dt. Vencimento:</div>
				<div class="input" style="width: 10%">
					<input id="dataVencimentoDuplicata" type="text" />
				</div>
				<div class="label">Valor:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="valorDuplicata"/>
				</div>
				<div class="bloco_botoes">
					<a id="botaoInserirDuplicata" title="Inserir Dados da Duplicata" class="botaoAdicionar"></a>
					<a id="botaoLimparDuplicata" title="Limpar Dados da Duplicata" class="botaoLimpar"></a>
				</div>
							
				<table id="tabela_duplicata" class="listrada" >
					<thead>
						<tr>
							<th>Núm.</th>
							<th>Dt. Venc.</th>
							<th>Valor(R$)</th>
							<th>Ações</th>
						</tr>
					</thead>
					
					<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
					<tbody>
					</tbody>
				</table>
			</fieldset>
		</fieldset>
	</form>

	
</body>
</html>
