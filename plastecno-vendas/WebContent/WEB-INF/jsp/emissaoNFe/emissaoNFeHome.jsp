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
	
	$("#botaoInserirDuplicata").click(function() {
		inserirDuplicata();
	});
	
	$("#botaoInserirVolume").click(function() {
		inserirVolume();
	});
	
	$("#botaoInserirReboque").click(function() {
		inserirReboque();
	});
	
	$("#botaoInserirReferenciada").click(function() {
		inserirReferenciada();
	});
	
	$('#bloco_logradouro').addClass('fieldsetInterno');

	$('#botaoEmitirNF').click(function(){
		gerarInputDuplicata();
		gerarInputProdutoServico();
		gerarInputVolume();
		gerarInputReboque();
		gerarInputReferenciada();
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
	
	$('#botaoInserirII').click(function(){
		gerarInputImpostoImportacao();
		fecharBlocoImposto('bloco_ii');
	});
	
	$('#bloco_tributos').fadeOut();
	
	$('#botaoInserirICMS').click(function(){
		gerarInputICMS();
		fecharBlocoImposto('bloco_icms');
	});
	
	$('#botaoLimparICMS').click(function(){
		removerInputHidden(gerarJsonTipoIcms());
		fecharBlocoImposto('bloco_icms');
	});
	
	$('#botaoLimparIPI').click(function(){
		removerInputHidden(gerarJsonTipoIpi());
		fecharBlocoImposto('bloco_ipi');
	});
	
	$('#botaoLimparPIS').click(function(){
		removerInputHidden(gerarJsonTipoPis());
		fecharBlocoImposto('bloco_pis');
	});
	
	$('#botaoLimparCOFINS').click(function(){
		removerInputHidden(gerarJsonTipoCofins());
		fecharBlocoImposto('bloco_cofins');
	});
	
	$('#botaoLimparII').click(function(){
		removerInputHidden(gerarJsonImpostoImportacao());
		fecharBlocoImposto('bloco_ii');
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
	inicializarFadeInBlocoImposto('bloco_icms');
	inicializarFadeInBlocoImposto('bloco_ipi');
	inicializarFadeInBlocoImposto('bloco_pis');
	inicializarFadeInBlocoImposto('bloco_cofins');
	inicializarFadeInBlocoImposto('bloco_ii');
});

function gerarInputLinhasTabela(nomeTabela, parametroJson){
	var tabela = document.getElementById(nomeTabela);
	var linhas = tabela.tBodies[0].rows;
	if(linhas.length <= 0){
		return;
	}
	
	var input = null;
	var celulas = null;
	var campos = null;
	var max = null;
	for (var i = 0; i < linhas.length; i++) {
		campos = new Array();
		celulas = linhas[i].cells;
		<%-- devemos excluir a ultima celula da tabela pois eh a celula de botoes de acoes --%>
		max = celulas.length - 1;
		for (var j= 0; j < max; j++) {
			campos[j] = {'nome':parametroJson.nomes[j], 'valor':celulas[j].innerHTML};
		}
		
		gerarInputHidden({'nomeObjeto': parametroJson.nomeLista+'['+i+']', 'campos':campos});
	}
};

function gerarLegendaBloco(nomeBloco){
	var legend = $('#bloco_tributos #'+nomeBloco+' legend');
	var innerHTML = $(legend).html();
	if(innerHTML.indexOf('+') != -1){
		innerHTML = innerHTML.replace(/\+/g, '-');
	} else {
		innerHTML = innerHTML.replace(/\-/g, '+');
	}
	$(legend).html(innerHTML);
};

function abrirBlocoImposto(nomeBloco){
	gerarLegendaBloco(nomeBloco);
	$('#bloco_tributos #'+nomeBloco+' div').fadeIn();
};

function fecharBlocoImposto(nomeBloco){
	gerarLegendaBloco(nomeBloco);
	$('#bloco_tributos #'+nomeBloco+' div').fadeOut();
};

function inicializarLegendaBlocoImposto(nomeBloco){
	var legend = $('#bloco_tributos #'+nomeBloco+' legend');
	$(legend).html(legend.html().replace(/Prod.\s*\d*/g, 'Prod. '+(numeroProdutoEdicao+1)+' '));	

	legend.html(legend.html().replace(/\+/g, '-'));
	$('#bloco_tributos #'+nomeBloco+' div').fadeIn();
};

function inicializarFadeInBlocoImposto(nomeBloco){
	$('#bloco_tributos #'+nomeBloco+' legend').click(function(){
		var innerHTML = $(this).html();
		if(innerHTML.indexOf('+') != -1){
			abrirBlocoImposto(nomeBloco);
		} else {
			fecharBlocoImposto(nomeBloco);
		}
	});
};

function removerInputHidden(objeto){
	var form = document.getElementById('formEmissao');
	var input = null;
	var campos = objeto.campos;
	var id = null;
	for (var i = 0; i < campos.length; i++) {
		id = objeto.nomeObjeto +'.'+campos[i].nome;
		if((input = document.getElementById(id)) == undefined){
			continue;
		}
		form.removeChild(input);	
	};
};

function gerarInputHidden(objeto){
	var form = document.getElementById('formEmissao');
	var input = null;
	var campos = objeto.campos;
	var nome = null;
	for (var i = 0; i < campos.length; i++) {
		nome = objeto.nomeObjeto +'.'+campos[i].nome;
		<%-- Devemos verificar  se o input ja foi criado pelo usuario, caso nao existe devemos cria-lo--%>
		if((input = document.getElementById(nome)) == undefined){
			input = document.createElement('input');
			input.type = 'hidden';
			input.name = nome;
			<%-- Estamos usando o fato de que os parametros enviados ao servidor tem um unico nome, por exemplo o primeiro e o segundo elemento de uma lista 
			contendo o atributo situacao tributaria sera enviado como nf.listaItem[0].situacaoTributaria e nf.listaItem[1].situacaoTributaria, e devem ser unicos 
			para que os parametros da nfe sejam preenchidos corretamente, com isso podemos usar como id --%>
			input.id = nome;
			form.appendChild(input);
		} 
		
		if(campos[i].valor == undefined) {
			input.value = document.getElementById(campos[i].id).value;
		} else {
			input.value = campos[i].valor;
		}
	};
};

function gerarJsonTipoIcms(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms',
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
};

function gerarJsonTipoIpi(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.ipi.tipoIpi',
		'campos':[{'nome':'aliquota', 'id':'aliquotaIPI'},
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribIPI'},
		          {'nome':'valor', 'id':'valorBCIPI'},
		          {'nome':'valorBC', 'id':'valorBCIPI'},
		          {'nome':'quantidadeUnidadeTributavel', 'id':'qtdeUnidTribIPI'},
		          {'nome':'valorUnidadeTributavel', 'id':'valorUnidTribIPI'}
			]};
};

function gerarJsonTipoCofins(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.cofins.tipoCofins',
		'campos':[{'nome':'aliquota', 'id':'aliquotaCOFINS'},
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribCOFINS'},
		          {'nome':'quantidadeVendida', 'id':'qtdeVendidaCOFINS'},
		          {'nome':'valor', 'id':'valorCOFINS'},
		          {'nome':'valorBC', 'id':'valorBCCOFINS'},
		          {'nome':'valorAliquota', 'id':'valorAliquotaCOFINS'}
			]};
};

function gerarJsonImpostoImportacao(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.impostoImportacao',
		'campos':[{'nome':'valor', 'id':'valorII'},
		          {'nome':'valorBC', 'id':'valorBCII'},
		          {'nome':'valorDespesaAduaneira', 'id':'valorDespAduaneirasII'},
		          {'nome':'valorIOF', 'id':'valorIOFII'}
			]};
};

function gerarJsonTipoPis(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.pis.tipoPis',
		'campos':[{'nome':'aliquota', 'id':'aliquotaPIS'},
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribPIS'},
		          {'nome':'quantidadeVendida', 'id':'qtdeVendidaPIS'},
		          {'nome':'valor', 'id':'valorPIS'},
		          {'nome':'valorBC', 'id':'valorBCPIS'},
		          {'nome':'valorAliquota', 'id':'valorAliquotaPIS'}
			]};
};

function gerarInputICMS(){
	var tipoIcms = gerarJsonTipoIcms();
	var produto = {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe',
		'campos':[{'nome':'cfop', 'id':'cfop'}]};
	
	gerarInputHidden(tipoIcms);
	//gerarInputHidden(produto);
};


function gerarInputIPI(){
	var tipoIpi = gerarJsonTipoIpi();
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
	gerarInputHidden(gerarJsonTipoPis());
};

function gerarInputCOFINS(){
	gerarInputHidden(gerarJsonTipoCofins());	
};

function gerarInputImpostoImportacao(){
	gerarInputHidden(gerarJsonImpostoImportacao());	
};


function gerarInputDuplicata(){
	var parametros = {'nomeLista': 'nf.cobrancaNFe.listaDuplicata',
			'nomes': ['numero', 'dataVencimento', 'valor']};
	gerarInputLinhasTabela('tabela_duplicata', parametros)
};

function gerarInputReboque(){
	var parametros = {'nomeLista': 'nf.transporteNFe.listaReboque',
					'nomes': ['placa', 'uf', 'registroNacionalTransportador']};
	gerarInputLinhasTabela('tabela_reboque', parametros)
};

function gerarInputReferenciada(){
	var parametros = {'nomeLista': 'nf.identificacaoNFe.listaNFeReferenciada',
					'nomes': ['chaveAcessoReferenciada', 'identificacaoNFeReferenciada.numeroNF', 
					          'identificacaoNFeReferenciada.serie', 'identificacaoNFeReferenciada.modelo',
					          'identificacaoNFeReferenciada.cnpjEmitente', 'identificacaoNFeReferenciada.anoMes',
					          'identificacaoNFeReferenciada.ufEmitente']};
	
	gerarInputLinhasTabela('tabela_referenciada', parametros);
};


function gerarInputVolume(){
	var parametro = {'nomeLista':'nf.transporteNFe.listaVolume',
			'nomes':['quantidade', 'especie', 'marca', 'numeracao', 'pesoLiquido', 'pesoBruto']};
	
	gerarInputLinhasTabela('tabela_volume', parametro);
};

function gerarInputProdutoServico(){
	var tabela = document.getElementById('tabela_produtos');
	var linhas = tabela.tBodies[0].rows;
	if(linhas.length <= 0){
		return;
	}
	
	var input = null;
	var celulas = null;
	var produto = null;
	var detalhamento = null;
	for (var i = 0; i < linhas.length; i++) {
		celulas = linhas[i].cells;
		
		detalhamento = {'nomeObjeto':'nf.listaItem['+i+']',
			'campos':[{'nome':'numeroItem', 'valor': celulas[0].innerHTML}]};
		
		produto = {'nomeObjeto':'nf.listaItem['+i+'].produtoServicoNFe',
				'campos':[{'nome':'codigo', 'valor': celulas[1].innerHTML},
				          {'nome':'descricao', 'valor': celulas[1].innerHTML},
				          {'nome':'ncm', 'valor': celulas[2].innerHTML},
				          {'nome':'unidadeComercial', 'valor': celulas[4].innerHTML},
				          {'nome':'quantidadeComercial', 'valor': celulas[5].innerHTML},
				          {'nome':'quantidadeTributavel', 'valor': celulas[5].innerHTML},
				          {'nome':'valorUnitarioComercializacao', 'valor': celulas[6].innerHTML},
				          {'nome':'valorTotalBruto', 'valor': celulas[7].innerHTML}
					]};
		
		gerarInputHidden(detalhamento);
		gerarInputHidden(produto);
	}
};

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
	linha.insertCell(3).innerHTML = '<input type="button" title="Remover Duplicata" value="" class="botaoRemover" onclick="removerLinhaTabela(this);"/>';
	
	$('#bloco_duplicata input:text').val('');
};

function inserirVolume(){
	var quantidade = $('#bloco_volume #quantidadeVolume').val();
	var especie = $('#bloco_volume #especieVolume').val();
	var marca = $('#bloco_volume #marcaVolume').val();
	var numeracao = $('#bloco_volume #numeracaoVolume').val();
	var pesoLiq = $('#bloco_volume #pesoLiquidoVolume').val();
	var pesoBruto = $('#bloco_volume #pesoBrutoVolume').val();

	if(isEmpty(quantidade) && isEmpty(especie) && isEmpty(marca)
			&& isEmpty(numeracao) && isEmpty(pesoLiq) && isEmpty(pesoBruto)){
		return;
	}
	
	var tabela = document.getElementById('tabela_volume');
	var linha = tabela.tBodies[0].insertRow(0);
	
	linha.insertCell(0).innerHTML = quantidade;
	linha.insertCell(1).innerHTML = especie;
	linha.insertCell(2).innerHTML = marca;
	linha.insertCell(3).innerHTML = numeracao;
	linha.insertCell(4).innerHTML = pesoLiq;
	linha.insertCell(5).innerHTML = pesoBruto;
	linha.insertCell(6).innerHTML = '<input type="button" title="Remover Volume" value="" class="botaoRemover" onclick="removerLinhaTabela(this);"/>';
	
	$('#bloco_volume input:text').val('');
};

function inserirReboque(){
	var placa = $('#bloco_reboque #placaReboque').val();
	var uf = $('#bloco_reboque #ufReboque').val();
	var registro = $('#bloco_reboque #registroReboque').val();

	if(isEmpty(placa) || isEmpty(uf)){
		return;
	}
	
	var tabela = document.getElementById('tabela_reboque');
	var linha = tabela.tBodies[0].insertRow(0);
	
	linha.insertCell(0).innerHTML = placa;
	linha.insertCell(1).innerHTML = uf;
	linha.insertCell(2).innerHTML = registro;
	linha.insertCell(3).innerHTML = '<input type="button" title="Remover Reboque" value="" class="botaoRemover" onclick="removerLinhaTabela(this);"/>';
	
	$('#bloco_reboque input:text').val('');
};

function inserirLinhaTabela(linhaJson){
	var tabela = document.getElementById(linhaJson.nomeTabela);
	var linha = tabela.tBodies[0].insertRow(0);
	var valores = linhaJson.valores;
	for (var i = 0; i <= valores.length; i++) {
		if(i < valores.length){
			linha.insertCell(i).innerHTML = valores[i];
		} else {
			linha.insertCell(i).innerHTML = '<input type="button" title="Remover Registro" value="" class="botaoRemover" onclick="removerLinhaTabela(this);"/>';
		}
	}
	
	$('#'+linhaJson.nomeBloco +' input:text').val('');
};

function inserirReferenciada(){
	var chave = $('#bloco_referenciada #chaveReferenciada').val();
	var numero = $('#bloco_referenciada #numeroReferenciada').val();
	var serie = $('#bloco_referenciada #serieReferenciada').val();
	var mod = $('#bloco_referenciada #modReferenciada').val();
	var cnpj = $('#bloco_referenciada #cnpjReferenciada').val();
	var anoMes = $('#bloco_referenciada #anoMesReferenciada').val();
	var uf = $('#bloco_referenciada #ufReferenciada').val();
	
	if(isEmpty(chave) || isEmpty(numero) || isEmpty(serie) || 
			isEmpty(mod) || isEmpty(cnpj) || isEmpty(anoMes) || isEmpty(uf)){
		return;
	}
	
	var linha = {'nomeBloco':'bloco_referenciada', 'nomeTabela': 'tabela_referenciada',
			'valores':[chave, numero, serie, mod, cnpj, anoMes, uf]};
	inserirLinhaTabela(linha);
};

function removerLinhaTabela(botao){
	 var linha = $(botao).closest("tr")[0];
	 var tabela = $(botao).closest("table")[0];
	 tabela.deleteRow(linha.rowIndex);
};


function inicializarFiltro() {
	$("#filtroSigla").val($("#sigla").val());
	$("#filtroDescricao").val($("#descricao").val());	
};

function inicializarModalCancelamento(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja DESATIVAR esse ramo de atividade?',
		confirmar: function(){
			$(botao).closest('form').submit();	
		}
	});
};

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
	inicializarLegendaBlocoImposto('bloco_icms');
	inicializarLegendaBlocoImposto('bloco_ipi');
	inicializarLegendaBlocoImposto('bloco_pis');
	inicializarLegendaBlocoImposto('bloco_cofins');
	inicializarLegendaBlocoImposto('bloco_ii');
	
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
			<div class="label obrigatorio">Forma Emiss�o:</div>
			<div class="input" style="width: 20%">
				<select name="nf.identificacaoNFe.tipoEmissao" class="semprehabilitado" style="width: 50%">
					<c:forEach var="tipoEmissao" items="${listaTipoEmissao}">
						<option value="${tipoEmissao.codigo}" <c:if test="${tipoEmissao eq tipoEmissaoPadrao}">selected</c:if>>${tipoEmissao.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Finalidade Emiss�o:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.finalidadeEmissao"
					style="width: 100%" class="semprehabilitado">
					<c:forEach var="finalidade" items="${listaTipoFinalidadeEmissao}">
						<option value="${finalidade.codigo}" <c:if test="${finalidade eq finalidadeEmissaoPadrao}">selected</c:if>>${finalidade.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Tipo Impress�o:</div>
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
			<div class="label obrigatorio">Destino Opera��o:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado" 
					style="width: 100%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Natureza Opera��o:</div>
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
		
		<fieldset id="bloco_referenciada">
			<legend>::: NF/NFe Referenciada :::</legend>
			<div class="label">Chave Acesso:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="chaveReferenciada" style="width: 50%"/>
			</div>
			<div class="label">N�m. Doc. Fiscal:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="numeroReferenciada" />
			</div>
			<div class="label">S�rie. Doc. Fiscal:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="serieReferenciada" />
			</div>
			<div class="label">Mod. Doc. Fiscal:</div>
			<div class="input" style="width: 30%">
				<input type="text" id="modReferenciada" style="width: 30%"/>
			</div>
			<div class="label">CNPJ Emit.:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="cnpjReferenciada" />
			</div>
			<div class="label">Emis. Ano/M�s (AAMM):</div>
			<div class="input" style="width: 10%">
				<input type="text" id="anoMesReferenciada" />
			</div>
			<div class="label">UF Emit.:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="ufReferenciada"/>
			</div>
			<div class="bloco_botoes">
				<a id="botaoInserirReferenciada" title="Inserir Dados da NF referenciada" class="botaoAdicionar"></a>
			</div>
			<table id="tabela_referenciada" class="listrada" >
				<thead>
					<tr>
						<th>Chave</th>
						<th>N�m. Doc. Fisc.</th>
						<th>S�rie Doc. Fisc.</th>
						<th>Mod. Doc. Fisc.</th>
						<th>CNPJ Emit.</th>
						<th>Emis. Ano/M�s</th>
						<th>UF Emit.</th>
						<th>A��es</th>
					</tr>
				</thead>
						
				<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
				<tbody>
				</tbody>
			</table>
		</fieldset>
		
		<fieldset>
			<legend>::: Destinat�rio :::</legend>
			<div class="label">Raz�o Social/Nome:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="nomeCliente" name="nf.identificacaoDestinatarioNFe.nomeFantasia" value="${cliente.razaoSocial}" class="pesquisavel" style="width: 60%"/>
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
			<legend>::: Produtos e Servi�os :::</legend>
			
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
						<th style="width: 2%">A��es</th>
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
			
			<fieldset id="bloco_tributos">
				<legend class="fieldsetInterno">::: Tributos :::</legend>
				<%-- 
				<div class="label">CFOP:</div>
				<div class="input" style="width: 80%">
					<input id="cfop" type="text" name="cfop" style="width: 10%"/>
				</div>
				--%>
				
				<fieldset id="bloco_icms" class="fieldsetInterno">
					<legend id="legendICMS" title="Clique para exibir os campos ICMS">::: ICMS Prod.::: +</legend>
					<div class="label">Regime:</div>
					<div class="input" style="width: 80%">
						<select style="width: 20%" class="semprehabilitado">
							<c:forEach var="icms" items="${listaRegime}">
								<option value="${icms.codigo}">${icms.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="label">Situa��o Tribut.:</div>
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
					<div  class="label">Al�quota:</div>
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
					<div  class="label">Perc. Redu��o BC ST:</div>
					<div class="input" style="width: 10%">
						<input id="percRedBCSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Valor BC ST:</div>
					<div class="input" style="width: 30%">
						<input id="valorBCSTICMS" type="text" style="width: 30%" class="semprehabilitado"/>
					</div>
					<div  class="label">Al�quota ST:</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaSTICMS" type="text" style="width: 100%" class="semprehabilitado"/>
					</div>
					<div  class="label">Valor ST:</div>
					<div class="input" style="width: 50%">
						<input id="valorSTICMS" type="text" style="width: 20%" class="semprehabilitado"/>
					</div>
					<div class="icms00 label">Mot. Desonera��o:</div>
					<div class="icms00 input" style="width: 36%">
						<select id="motDesonerICMS" style="width: 100%" class="icms00 semprehabilitado">
							<c:forEach var="motivo" items="${listaTipoMotivoDesoneracao}">
								<option value="${motivo.codigo}">${motivo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirICMS" title="Inserir ICMS do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparICMS" title="Limpar ICMS do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				
				<fieldset id="bloco_ipi" class="fieldsetInterno">
					<legend>::: IPI Prod.::: +</legend>
					<div class="label">Situa��o Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="codSitTribIPI" style="width: 45%" >
							<c:forEach var="tipo" items="${listaTipoTributacaoIPI}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Al�quota:</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 50%">
						<input id="valorBCIPI" type="text" style="width: 20%" />
					</div>
					<div  class="label">Qtde. unid. Tribut�vel:</div>
					<div class="input" style="width: 10%">
						<input id="qtdeUnidTribIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Unid. Tribut�vel:</div>
					<div class="input" style="width: 50%">
						<input id="valorUnidTribIPI" type="text" style="width: 20%" />
					</div>
					<div  class="label">Cl. Enquadramento:</div>
					<div class="input" style="width: 10%">
						<input id="clEnquadramentoIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Cod. Enquadramento:</div>
					<div class="input" style="width: 50%">
						<input id="codEnquadramentoIPI" type="text" style="width: 20%" />
					</div>
					<div  class="label">CNPJ Produtor:</div>
					<div class="input" style="width: 70%">
						<input id="cnpjProdIPI" type="text" style="width: 20%" />
					</div>
					<div  class="label">Cod. Selo Controle:</div>
					<div class="input" style="width: 10%">
						<input id="codSeloContrIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Qtde. Selo Controle:</div>
					<div class="input" style="width: 50%">
						<input id="qtdeSeloContrIPI" type="text" style="width: 20%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirIPI" title="Inserir IPI do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparIPI" title="Limpar IPI do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				
				<fieldset id="bloco_pis" class="fieldsetInterno">
					<legend>::: PIS Prod.::: +</legend>
					<div class="label">Situa��o Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="codSitTribPIS" style="width: 45%">
							<c:forEach var="tipo" items="${listaTipoTributacaoPIS}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCPIS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Al�quota(%):</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaPIS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Al�quota(R$):</div>
					<div class="input" style="width: 20%">
						<input id="valorAliquotaPIS" type="text" style="width: 50%" />
					</div>
					<div  class="label">Qtde.Vendida:</div>
					<div class="input" style="width: 10%">
						<input id="qtdeVendidaPIS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor:</div>
					<div class="input" style="width: 10%">
						<input id="valorPIS" type="text" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirPIS" title="Inserir PIS do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparPIS" title="Limpar PIS do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				
				<fieldset id="bloco_cofins" class="fieldsetInterno">
					<legend>::: COFINS Prod.::: +</legend>
					<div class="label">Situa��o Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="codSitTribCOFINS" style="width: 45%">
							<c:forEach var="tipo" items="${listaTipoTributacaoCOFINS}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Al�quota(%):</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Al�quota(R$):</div>
					<div class="input" style="width: 20%">
						<input id="valorAliquotaCOFINS" type="text" style="width: 50%" />
					</div>
					<div  class="label">Qtde. Vendida:</div>
					<div class="input" style="width: 10%">
						<input id="qtdeVendidaCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor:</div>
					<div class="input" style="width: 10%">
						<input id="valorCOFINS" type="text" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirCOFINS" title="Inserir COFINS do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparCOFINS" title="Limpar COFINS do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				
				<fieldset id="bloco_ii" class="fieldsetInterno">
					<legend>::: Imp. Impor. Prod.::: +</legend>
					<div  class="label">Valor:</div>
					<div class="input" style="width: 10%">
						<input id="valorII" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 50%">
						<input id="valorBCII" type="text" style="width: 20%" />
					</div>
					<div  class="label">Valor IOF:</div>
					<div class="input" style="width: 10%">
						<input id="valorIOFII" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Desp. Aduan.:</div>
					<div class="input" style="width: 50%">
						<input id="valorDespAduaneirasII" type="text" style="width: 20%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirII" title="Inserir Imp. Importa��o do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparII" title="Limpar Imp. Importa��o do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				<%--
				<fieldset class="fieldsetInterno">
					<legend>::: Valores Totais :::</legend>
					<div class="label">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorBCICMSTotal"/>
					</div>
					<div class="label">Valor Total:</div>
					<div class="input" style="width: 50%">
						<input type="text" id="valorTotalICMS" style="width: 20%"/>
					</div>
					<div class="label">Valor BC ST:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorBCSTICMSTotal"/>
					</div>
					<div class="label">Valor Total ST:</div>
					<div class="input" style="width: 50%">
						<input type="text" id="valorTotalICMSST" style="width: 20%"/>
					</div>
					<div class="label">Valor Total Prod. Serv.:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorProdServ"/>
					</div>
					<div class="label">Valor Total Frete:</div>
					<div class="input" style="width: 50%">
						<input type="text" id="valorTotalFrete" style="width: 20%"/>
					</div>
					<div class="label">Valor Total Seguro:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorTotalSeguro"/>
					</div>
					<div class="label">Valor Total Desconto:</div>
					<div class="input" style="width: 50%">
						<input type="text" id="valorTotalDesconto" style="width: 20%"/>
					</div>
					<div class="label">Valor Total Imp. Import.:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorTotalImpImport"/>
					</div>
					<div class="label">Valor Total IPI:</div>
					<div class="input" style="width: 50%">
						<input type="text" id="valorTotalIPI" style="width: 20%"/>
					</div>
					<div class="label">Valor Total PIS:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorTotalPIS"/>
					</div>
					<div class="label">Valor Total COFINS:</div>
					<div class="input" style="width: 50%">
						<input type="text" id="valorTotalCOFINS" style="width: 20%"/>
					</div>
					<div class="label">Outras Desp. Acess�rias:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorTotalOutrasDesp"/>
					</div>
				</fieldset>
				--%>
			</fieldset>
		</fieldset>	
		
		<fieldset>
			<legend>::: Transporte :::</legend>
			<div class="label">Modal. Frete:</div>
			<div class="input" style="width: 80%">
			<select id="modFrete" name="nf.transporteNFe.modalidadeFrete" style="width: 45%">
				<c:forEach var="tipo" items="${listaTipoModalidadeFrete}">
					<option value="${tipo.codigo}">${tipo.descricao}</option>
				</c:forEach>
			</select>
			</div>
			
			<fieldset class="fieldsetInterno">
					<legend>::: Transportadora :::</legend>
					<div  class="label">Raz�o Soc./Nome:</div>
					<div class="input" style="width: 80%">
						<input type="text" name="nf.transporteNFe.transportadoraNFe.razaoSocial" value="${transportadora.razaoSocial}" style="width: 45%" />
					</div>
					<div  class="label">CNPJ:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.transportadoraNFe.cnpj" value="${transportadora.cnpj}" style="width: 100%" />
					</div>
					<div  class="label">CPF:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.transportadoraNFe.cpf" style="width: 100%" />
					</div>
					<div  class="label">Insc. Estadual:</div>
					<div class="input" style="width: 30%">
						<input type="text" name="nf.transporteNFe.transportadoraNFe.inscricaoEstadual" value="${transportadora.inscricaoEstadual}"  style="width: 50%" />
					</div>
					<div  class="label">Endere�o:</div>
					<div class="input" style="width: 80%">
						<input type="text" name="nf.transporteNFe.transportadoraNFe.enderecoCompleto" value="${transportadora.enderecoCompleto}" style="width: 84%" />
					</div>
					<div  class="label">Munic�pio:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.transportadoraNFe.municipio" value="${transportadora.municipio}" style="width: 100%" />
					</div>
					<div  class="label">UF:</div>
					<div class="input" style="width: 50%">
						<input type="text" name="nf.transporteNFe.transportadoraNFe.uf" value="${transportadora.uf}" style="width: 20%" />
					</div>
				</fieldset>
			
				<fieldset class="fieldsetInterno">
					<legend>::: Ve�culo :::</legend>
					<div  class="label">Placa:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.veiculo.placa" value="${transportadora.cnpj}" style="width: 100%" />
					</div>
					<div  class="label">UF:</div>
					<div class="input" style="width: 50%">
						<input type="text" name="nf.transporteNFe.veiculo.uf" style="width: 20%" />
					</div>
					<div  class="label">Regist. Trans. Cargo:</div>
					<div class="input" style="width: 30%">
						<input type="text" name="nf.transporteNFe.veiculo.registroNacionalTransportador" value="${transportadora.inscricaoEstadual}"  style="width: 50%" />
					</div>
				</fieldset>
				
				<fieldset id="bloco_reboque" class="fieldsetInterno">
					<legend>::: Reboque :::</legend>
					<div  class="label">Placa:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="placaReboque" style="width: 100%" />
					</div>
					<div  class="label">UF:</div>
					<div class="input" style="width: 50%">
						<input type="text" id="ufReboque" style="width: 20%" />
					</div>
					<div  class="label">Regist. Trans. Cargo:</div>
					<div class="input" style="width: 30%">
						<input type="text" id="registroReboque" style="width: 50%" />
					</div>
					<div class="bloco_botoes">
						<a id="botaoInserirReboque" title="Inserir Dados do Reboque" class="botaoAdicionar"></a>
					</div>
								
					<table id="tabela_reboque" class="listrada" >
						<thead>
							<tr>
								<th>Placa</th>
								<th>UF</th>
								<th>Registro</th>
								<th>A��es</th>
							</tr>
						</thead>
						
						<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
						<tbody>
						</tbody>
					</table>
				</fieldset>
				
				<fieldset class="fieldsetInterno">
					<legend>::: Reten��o ICMS :::</legend>
					<div  class="label">Valor Servi�o:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.valorServico" style="width: 100%" />
					</div>
					<div  class="label">Valor BC:</div>
					<div class="input" style="width: 50%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.valorBC" style="width: 20%" />
					</div>
					<div  class="label">Al�quota(%):</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.aliquota" style="width: 100%" />
					</div>
					<div  class="label">Valor Ret.:</div>
					<div class="input" style="width: 50%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.valorRetido" style="width: 20%" />
					</div>
					<div  class="label">CFOP:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.cfop" style="width: 100%" />
					</div>
					<div  class="label">Munic�pio Gerador:</div>
					<div class="input" style="width: 50%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.codigoMunicipioGerador" style="width: 20%" />
					</div>
				</fieldset>
				
				<fieldset id="bloco_volume" class="fieldsetInterno">
					<legend>::: Volumes :::</legend>
					<div class="label">Qtde.:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="quantidadeVolume"/>
					</div>
					
					<div class="label">Esp�cie:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="especieVolume"/>
					</div>
					<div class="label">Marca:</div>
					<div class="input" style="width: 30%">
						<input type="text" id="marcaVolume" style="width: 50%"/>
					</div>
					<div class="label">Numera��o:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="numeracaoVolume"/>
					</div>
					<div class="label">Peso L�q.(kg):</div>
					<div class="input" style="width: 10%">
						<input type="text" id="pesoLiquidoVolume"/>
					</div>
					<div class="label">Peso Bruto(kg):</div>
					<div class="input" style="width: 30%">
						<input type="text" id="pesoBrutoVolume" style="width: 50%"/>
					</div>
					<div class="bloco_botoes">
						<a id="botaoInserirVolume" title="Inserir Dados da Volume" class="botaoAdicionar"></a>
						<a id="botaoLimparVolume" title="Limpar Dados da Volume" class="botaoLimpar"></a>
					</div>
								
					<table id="tabela_volume" class="listrada" >
						<thead>
							<tr>
								<th>Qtde.</th>
								<th>Esp�cie</th>
								<th>Marca</th>
								<th>N�mera��o</th>
								<th>Peso L�q.(kg)</th>
								<th>Peso Bruto(kg)</th>
								<th>A��es</th>
							</tr>
						</thead>
						
						<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
						<tbody>
						</tbody>
					</table>
				</fieldset>
		</fieldset>
		
		<fieldset>
			<legend>::: Cobran�a :::</legend>
			<div class="label">N�mero:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.numero"/>
			</div>
			
			<div class="label">Valor Original:</div>
			<div class="input" style="width: 55%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorOriginal" style="width: 20%"/>
			</div>
			<div class="label">Valor Desconto:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorDesconto" />
			</div>
			<div class="label">Valor L�quido:</div>
			<div class="input" style="width: 55%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorLiquido" style="width: 20%"/>
			</div>
			
			<fieldset id="bloco_duplicata" class="fieldsetInterno">
				<legend>::: Duplicata :::</legend>
				<div class="label">N�mero:</div>
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
							<th>N�m.</th>
							<th>Dt. Venc.</th>
							<th>Valor(R$)</th>
							<th>A��es</th>
						</tr>
					</thead>
					
					<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
					<tbody>
					</tbody>
				</table>
			</fieldset>
		</fieldset>
		
		<fieldset>
			<legend>::: Exporta��o :::</legend>
			<div class="label">UF Embarque:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.exportacaoNFe.ufEmbarque" style="width: 5%"/>
			</div>
			<div class="label">Local Embarque:</div>
			<div class="input" style="width: 60%">
				<input type="text" name="nf.exportacaoNFe.localEmbarque"/>
			</div>		
		</fieldset>
		<fieldset>
			<legend>::: Compra :::</legend>
			<div class="label">Nota Empenho:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.compraNFe.notaEmpenho" style="width: 10%"/>
			</div>
			<div class="label">Pedido:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.compraNFe.pedido" style="width: 50%"/>
			</div>
			<div class="label">Contrato:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.compraNFe.contrato" style="width: 50%"/>
			</div>		
		</fieldset>
		<div class="bloco_botoes">
			<input type="button" id="botaoEmitirNF" title="Emitir Nota Fiscal" value="" class="botaoEnviarEmail"/>
		</div>
	</form>

	
</body>
</html>
