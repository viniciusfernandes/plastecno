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

.impostosFieldset {
	width: 100%;
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
	
	$("#botaoInserirDuplicata").click(function() {
		inserirDuplicata();
	});
	
	$('#bloco_logradouro').addClass('fieldsetInterno');

	$('#botaoEmitirNF').click(function(){
		gerarInputDuplicata();
		gerarInputProdutoServico();
		$('#formEmissao').submit();
	});
	
	$('#botaoInserirIPI').click(function(){
		gerarInputIPI();
		fecharBlocoImposto('bloco_ipi');
	});
	
	$('#botaoInserirPIS').click(function(){
		gerarInputPIS();
		fecharBlocoImposto('bloco_pis');
	});
	
	$('#botaoInserirCOFINS').click(function(){
		gerarInputCOFINS();
		fecharBlocoImposto('bloco_cofins');
	});
	
	$('#bloco_tributos').fadeOut();
	
	$('#botaoInserirICMS').click(function(){
		gerarInputICMS();
		fecharBlocoImposto('bloco_icms');
	});
	
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
	inicializarFadeInBlockImposto('bloco_icms');
	inicializarFadeInBlockImposto('bloco_ipi');
	inicializarFadeInBlockImposto('bloco_pis');
	inicializarFadeInBlockImposto('bloco_cofins');
});

function abrirBlocoImposto(nomeBloco){
	$('#bloco_tributos #'+nomeBloco+' div').fadeIn();
};

function fecharBlocoImposto(nomeBloco){
	$('#bloco_tributos #'+nomeBloco+' div').fadeOut();
};

function inicializarFadeInBlockImposto(nomeBloco){
	$('#bloco_tributos #'+nomeBloco+' legend').click(function(){
		var innerHTML = $(this).html();
		if(innerHTML.indexOf('+') != -1){
			innerHTML = innerHTML.replace(/\+/g, '-');
			abrirBlocoImposto(nomeBloco);
		} else {
			innerHTML = innerHTML.replace(/\-/g, '+');
			fecharBlocoImposto(nomeBloco);
		}
		$(this).html(innerHTML);
	});
}

function gerarCamposICMS(){
	
}

function gerarInputHidden(objeto){
	var form = document.getElementById('formEmissao');
	var input = null;
	var campos = objeto.campos;
	
	for (var i = 0; i < campos.length; i++) {
		input = document.createElement('input');
		input.type = 'hidden';
		input.name = objeto.nomeObjeto +'.'+campos[i].nome;
		
		if(campos[i].valor == undefined) {
			input.value = document.getElementById(campos[i].id).value;
		} else {
			input.value = campos[i].valor;
		}
		
		form.appendChild(input)	
	};
}


function gerarInputICMS(){
	var tipoIcms = 
		{'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms',
			'campos':[{'nome':'codigoSituacaoTributaria', 'id':'tipoTributacaoICMS'},
			          {'nome':'aliquota', 'id':'aliquotaICMS'},
			          {'nome':'valor', 'id':'valorICMS'},
			          {'nome':'modalidadeDeterminacaoBC', 'id':'modBCICMS'},
			          {'nome':'modalidadeDeterminacaoBCST', 'id':'modBCSTICMS'},
			          {'nome':'percentualMargemValorAdicionadoICMSST', 'id':'percValSTICMS'},
			          {'nome':'percentualReducaoBC', 'id':'percRedBCSTICMS'},
			          {'nome':'valorBC', 'id':'valorBCICMS'},
			          {'nome':'valorBCST', 'id':'valorBCSTICMS'},
			          {'nome':'aliquotaST', 'id':'aliquotaSTICMS'},
			          {'nome':'valorST', 'id':'valorSTICMS'},
			          {'nome':'motivoDesoneracao', 'id':'motDesonerICMS'}
				]};
	
	var produto = 
	{'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe',
		'campos':[{'nome':'cfop', 'id':'cfop'}]};
	
	gerarInputHidden(tipoIcms);
	gerarInputHidden(produto);
}

function gerarInputIPI(){
	var tipoIpi = 
	{'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.ipi.tipoIcms',
		'campos':[{'nome':'aliquota', 'id':'aliquotaIPI'},
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribIPI'},
		          {'nome':'valor', 'id':'valorBCIPI'},
		          {'nome':'valorBC', 'id':'valorBCIPI'},
		          {'nome':'quantidadeUnidadeTributavel', 'id':'qtdeUnidTribIPI'},
		          {'nome':'valorUnidadeTributavel', 'id':'valorUnidTribIPI'}
			]};
	
	var ipi = 
	{'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.ipi',
		'campos':[{'nome':'classeEnquadramento', 'id':'clEnquadramentoIPI'},
		          {'nome':'codigoEnquadramento', 'id':'codEnquadramentoIPI'},
		          {'nome':'cnpjProdutor', 'id':'cnpjProdIPI'},
		          {'nome':'codigoSeloControle', 'id':'codSeloContrIPI'},
		          {'nome':'quantidadeSeloControle', 'id':'qtdeSeloContrIPI'}
			]};
	
	gerarInputHidden(tipoIpi);
	gerarInputHidden(ipi);
};

function gerarInputPIS(){
	var tipoPis = 
	{'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.pis.tipoPis',
		'campos':[{'nome':'aliquota', 'id':'aliquotaPIS'},
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribPIS'},
		          {'nome':'quantidadeVendida', 'id':'qtdeVendidaPIS'},
		          {'nome':'valor', 'id':'valorPIS'},
		          {'nome':'valorBC', 'id':'valorBCPIS'},
		          {'nome':'valorAliquota', 'id':'valorAliquotaPIS'}
			]};
	
	gerarInputHidden(tipoPis);
};

function gerarInputCOFINS(){
	var form = document.getElementById('formEmissao');
	var input = null;

	var cofins = 'nf.listaItem['+numeroProdutoEdicao+'].tributos.cofins';
	var tipoCofins = cofins+'.tipoCofins';
		
	
	var tipoPis = 
	{'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.ipi',
		'campos':[{'nome':'aliquota', 'id':'aliquotaCOFINS'},
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribCOFINS'},
		          {'nome':'quantidadeVendida', 'id':'qtdeVendidaCOFINS'},
		          {'nome':'valor', 'id':'valorCOFINS'},
		          {'nome':'valorBC', 'id':'valorBCCOFINS'},
		          {'nome':'valorAliquota', 'id':'valorAliquotaCOFINS'}
			]};
	gerarInputHidden(tipoCofins);	
};

function gerarInputDuplicata(){
	var tabela = document.getElementById('tabela_duplicata');
	var linhas = tabela.tBodies[0].rows;
	if(linhas.length <= 0){
		return;
	}
	
	var input = null;
	var celulas = null;
	
	var duplicata = null;
	for (var i = 0; i < linhas.length; i++) {
		celulas = linhas[i].cells;
		duplicata = {'nomeObjeto':'nf.cobrancaNFe.listaDuplicata['+i+']',
			'campos':[{'nome':'numero', 'valor': celulas[0].innerHTML},
			          {'nome':'dataVencimento', 'valor': celulas[1].innerHTML},
			          {'nome':'valor', 'valor': celulas[2].innerHTML}
				]};
		gerarInputHidden(duplicata);
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
	$('#bloco_tributos .impostosFieldset div').fadeIn();
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
				<%-- 
				<div class="label">CFOP:</div>
				<div class="input" style="width: 80%">
					<input id="cfop" type="text" name="cfop" style="width: 10%"/>
				</div>
				--%>
				
				<div class="impostosFieldset">
				<fieldset id="bloco_icms" class="fieldsetInterno">
					<legend id="legendICMS" title="Clique para exibir os campos ICMS">::: ICMS ::: +</legend>
					<div class="label">Regime:</div>
					<div class="input" style="width: 80%">
						<select style="width: 20%" class="semprehabilitado">
							<c:forEach var="icms" items="${listaRegime}">
								<option value="${icms.codigo}">${icms.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="label">Situação Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="tipoTributacaoICMS" 
							style="width: 77%" class="semprehabilitado">
							<c:forEach var="icms" items="${listaTipoTributacaoICMS}">
								<option value="${icms.codigo}">${icms.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="label">Origem:</div>
					<div class="input" style="width: 25%">
						<select id="pedidoAssociado" 
							style="width: 100%" class="semprehabilitado">
							<c:forEach var="origem" items="${listaTipoOrigemMercadoria}">
								<option value="${origem.codigo}">${origem.descricao}</option>
							</c:forEach>
						</select>
					</div>
					
					<div class="icms00 label">Modalidade:</div>
					<div class="icms00 input" style="width: 30%">
						<select id="modBCICMS" style="width: 65%" class="icms00 semprehabilitado">
							<c:forEach var="modalidade" items="${listaTipoModalidadeDeterminacaoBCICMS}">
								<option value="${modalidade.codigo}">${modalidade.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorBCICMS" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Alíquota:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="aliquotaICMS" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Valor:</div>
					<div class="input" style="width: 30%">
						<input type="text" id="valorICMS" style="width: 30%" class="semprehabilitado"/>
					</div>
					<div class="icms00 label">Modalidade ST:</div>
					<div class="icms00 input" style="width: 70%">
						<select id="modBCSTICMS" style="width: 30%" class="icms00 semprehabilitado">
							<c:forEach var="modalidade" items="${listaTipoModalidadeDeterminacaoBCICMSST}">
								<option value="${modalidade.codigo}">${modalidade.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Perc. Marg. Valor ST:</div>
					<div class="input" style="width: 10%">
						<input id="percValSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Perc. Redução BC ST:</div>
					<div class="input" style="width: 10%">
						<input id="percRedBCSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Valor BC ST:</div>
					<div class="input" style="width: 30%">
						<input id="valorBCSTICMS" type="text" style="width: 30%" class="semprehabilitado"/>
					</div>
					<div  class="label">Alíquota ST:</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Valor ST:</div>
					<div class="input" style="width: 50%">
						<input id="valorSTICMS" type="text" style="width: 20%" class="semprehabilitado"/>
					</div>
					<div class="icms00 label">Mot. Desoneração:</div>
					<div class="icms00 input" style="width: 80%">
						<select id="motDesonerICMS" style="width: 77%" class="icms00 semprehabilitado">
							<c:forEach var="motivo" items="${listaTipoMotivoDesoneracao}">
								<option value="${motivo.codigo}">${motivo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirICMS" title="Inserir ICMS do Produto" value="" class="botaoInserir"/>
					</div>
				</fieldset>
				</div>
				
				<div class="impostosFieldset">
				<fieldset id="bloco_ipi" class="fieldsetInterno">
					<legend>::: IPI ::: +</legend>
					<div class="label">Situação Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="codSitTribIPI" style="width: 77%" >
							<c:forEach var="tipo" items="${listaTipoTributacaoIPI}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Alíquota:</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Qtde. unid. Tributável:</div>
					<div class="input" style="width: 30%">
						<input id="qtdeUnidTribIPI" type="text" style="width: 30%" />
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
				</fieldset>
				</div>
				
				<div class="impostosFieldset">
				<fieldset id="bloco_pis" class="fieldsetInterno">
					<legend>::: PIS ::: +</legend>
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
				</div>
				
				<div class="impostosFieldset">
				<fieldset id="bloco_cofins" class="fieldsetInterno">
					<legend>::: COFINS ::: +</legend>
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
				</div>
				
				<fieldset class="fieldsetInterno">
					<legend>::: Valores Totais :::</legend>
					<div class="label">Valor BC:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorBCICMSTotal"/>
					</div>
					<div class="label">Valor Total:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalICMS"/>
					</div>
					<div class="label">Valor BC ST:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorBCSTICMSTotal"/>
					</div>
					<div class="label">Valor Total ST:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalICMSST"/>
					</div>
					<div class="label">Valor Total Prod. Serv.:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorProdServ"/>
					</div>
					<div class="label">Valor Total Frete:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalFrete"/>
					</div>
					<div class="label">Valor Total Seguro:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalSeguro"/>
					</div>
					<div class="label">Valor Total Desconto:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalDesconto"/>
					</div>
					<div class="label">Valor Total Imp. Import.:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalImpImport"/>
					</div>
					<div class="label">Valor Total IPI:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalIPI"/>
					</div>
					<div class="label">Valor Total PIS:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalPIS"/>
					</div>
					<div class="label">Valor Total COFINS:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalCOFINS"/>
					</div>
					<div class="label">Outras Desp. Acessórias:</div>
					<div class="input" style="width: 70%">
						<input type="text" id="valorTotalOutrasDesp"/>
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
