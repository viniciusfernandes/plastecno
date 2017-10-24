<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js?${versaoCache}"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/edicao_tabela.js?${versaoCache}"/>"></script>

<style type="text/css">
fieldset .fieldsetInterno {
	margin: 0 1% 1% 1%;
}
fieldset .fieldsetInterno legend {
	font-size: 10px;
}

.divFieldset {
	width: 100%;
	float: left;
}

</style>
<script type="text/javascript">
var numeroProdutoEdicao = null;
var numeroImportacaoProduto = null;
var editorTabelaImportacao = null;
var editorTabelaAdicao = null;
var editorTabelaExportacao = null;
var btProduto = null;
$(document).ready(function() {
	scrollTo('${ancora}');
	
	$("#botaoPesquisaPedido").click(function() {
		if(isEmpty($('#idPedido').val())){
			return;
		}
		$('#formPesquisa #idPedidoPesquisa').val($('#idPedido').val());
		$('#formPesquisa').submit();
	});
	
	$("#botaoPesquisaNFe").click(function() {
		if(isEmpty($('#numeroNFe').val())){
			return;
		}
		$('#formPesquisaNFe #numeroNFePesquisa').val($('#numeroNFe').val());
		$('#formPesquisaNFe').submit();
	});
	
	$("#botaoRemoverNFe").click(function() {
		if(isEmpty($('#numeroNFe').val())){
			return;
		}
		$('#formPesquisaNFe #numeroNFePesquisa').val($('#numeroNFe').val());
		$('#formPesquisaNFe').attr('action', '<c:url value="/emissaoNFe/remocao"/>').attr('method', 'post').submit();
	});
	
	$("#botaoPedidoPDF").click(function() {
		if(isEmpty($('#idPedido').val())){
			return;
		}
		$('#formPesquisa #idPedidoPesquisa').val($('#idPedido').val());
		$('#formPesquisa').attr('action', '<c:url value="/pedido/pdf"/>').submit();
	});

	$('#bloco_logradouro').addClass('fieldsetInterno');

	$('#botaoEmitirNF').click(function(){
		emitirNFe('ENTRADA');
	});
	
	$('#botaoDevolverNF').click(function(){
		emitirNFe('DEVOLUCAO');
	});
	
	$('#botaoTriangularNF').click(function(){
		emitirNFe('TRIANGULARIZACAO');
	});
	
	$('#botaoProximoProduto').click(function(){
		encontrarBotaoEdicaoProduto(true);
	});
	
	$('#botaoAnteriorProduto').click(function(){
		encontrarBotaoEdicaoProduto(false);
	});
	
	$('#botaoInserirInfoProd').click(function(){
		gerarInputInfoProduto();
		alterarValorDuplicata();
		fecharBloco('bloco_info_adicionais_prod');
	});
	
	$('#botaoInserirIPI').click(function(){
		gerarInputIPI();
		fecharBloco('bloco_ipi');
	});
	
	$('#botaoInserirPIS').click(function(){
		gerarInputPIS();
		fecharBloco('bloco_pis');
	});
	
	$('#botaoInserirCOFINS').click(function(){
		gerarInputCOFINS();
		fecharBloco('bloco_cofins');
	});
	
	$('#botaoInserirII').click(function(){
		gerarInputImpostoImportacao();
		fecharBloco('bloco_ii');
	});
	
	$('#botaoInserirISS').click(function(){
		gerarInputISS();
		fecharBloco('bloco_iss');
	});
	
	$('#botaoInserirICMS').click(function(){
		gerarInputICMS();
		fecharBloco('bloco_icms');
	});
	
	$('#botaoInserirICMSInter').click(function(){
		gerarInputICMSInter();
		fecharBloco('bloco_icms_interestadual');
	});
	
	$('#botaoLimparICMS').click(function(){
		removerInputHidden(gerarJsonTipoIcms());
		fecharBloco('bloco_icms');
		zerarImposto('valorBCICMS');
	});
	
	$('#botaoLimparICMSInter').click(function(){
		removerInputHidden(gerarJsonIcmsInterestadual());
		fecharBloco('bloco_icms_interestadual');
		calcularValoresImpostos(null, false);
	});
	
	$('#botaoLimparInfoProd').click(function(){
		removerInputHidden(gerarJsonInfoProduto());
		alterarValorDuplicata();
		fecharBloco('bloco_info_adicionais_prod');
		$('#bloco_info_adicionais_prod #fciProd').val('');
		$('#bloco_info_adicionais_prod #infoAdicionaisProd').val('');
		$('#bloco_info_adicionais_prod input:text').val('');
	});
	
	$('#botaoLimparIPI').click(function(){
		removerInputHidden(gerarJsonTipoIpi());
		removerInputHidden(gerarJsonEnquadramentoIpi());
		fecharBloco('bloco_ipi');
		zerarImposto('valorBCIPI');
	});
	
	$('#botaoLimparPIS').click(function(){
		removerInputHidden(gerarJsonTipoPis());
		fecharBloco('bloco_pis');
		zerarImposto('valorBCPIS');
	});
	
	$('#botaoLimparCOFINS').click(function(){
		removerInputHidden(gerarJsonTipoCofins());
		fecharBloco('bloco_cofins');
		zerarImposto('valorBCCOFINS');
	});
	
	$('#botaoLimparISS').click(function(){
		removerInputHidden(gerarJsonISS());
		fecharBloco('bloco_iss');
		zerarImposto('valorBCISS');
	});
	
	$('#botaoLimparII').click(function(){
		removerInputHidden(gerarJsonImpostoImportacao());
		fecharBloco('bloco_ii');
		zerarImposto('valorBCII');
	});
	
	$('#listaNumeroNFe').change(function(){
		$('#numeroNFe').val($(this).val());
		$('#botaoPesquisaNFe').click();
	});
	
	$('#botaoPesquisarCnpjTransp').click(function () {
		var cnpj = $('#cnpjTransportadora').val(); 
		if (cnpj == undefined || isEmpty(cnpj)) {
			return;
		}
		
		var request = $.ajax({
							type: "get",
							url: '<c:url value="/transportadora/cnpj"/>',
							data: 'cnpj='+cnpj,
						});
		request.done(function(response) {
			var transportadora = response.transportadora;
			if(transportadora == undefined){
				return;
			}
			$('#nomeTransportadora').val(transportadora.razaoSocial);
			$('#ieTransportadora').val(transportadora.inscricaoEstadual);
			$('#endTransportadora').val(transportadora.endereco);
			$('#munTransportadora').val(transportadora.cidade);
			$('#ufTransportadora').val(transportadora.uf);
		});
		
		request.fail(function(request, status) {
			alert('Falha na busca da transportadora por CNPJ: ' + cnpj+' => Status da requisicao: '+status);
		});
	});	
	
	$('#botaoPesquisarCnpjDest').click(function () {
		var cnpj = $('#cnpj').val();
		if (cnpj == undefined || isEmpty(cnpj)) {
			return;
		}
		
		var request = $.ajax({
							type: "get",
							url: '<c:url value="/cliente/serializacao/cnpj"/>',
							data: 'cnpj='+cnpj,
						});
		request.done(function(response) {
			popularCliente(response.cliente);
		});
		
		request.fail(function(request, status) {
			alert('Falha na busca da destinatário por CNPJ: ' + cnpj+' => Status da requisicao: '+status);
		});
	});
	
	$('#bloco_info_adicionais_prod #valorFreteProd').keyup(function(){
		calcularValoresImpostos(null, false);
	});
	
	$('#valorFretePedido').keyup(function(){
		calcularValoresImpostos(null, false);
		alterarValorDuplicata();
		var valFrete = calcularValorFreteProduto();
		<%--Aqui estamos carregando os valores que foram inseridos nos input hidden --%>
		$("input[name*='produtoServicoNFe.valorTotalFrete']").each(function(){
			$(this).val(valFrete);
		});
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
					popularCliente(response.cliente);
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
	
	autocompletar({
		url : '<c:url value="/transportadora/listagem/nome"/>',
		campoPesquisavel : 'nomeTransportadora',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaTransportadora',
		selecionarItem: function(itemLista) {
			var request = $.ajax({
				type : 'get',
				url : '<c:url value="emissaoNFe/transportadora/id"/>?id=' + itemLista.id
			});
			
			request.done(function(response) {
				var t = response.transportadora;
				if(t == undefined || t == null){
					return;
				}
				document.getElementById('nomeTransportadora').value = t.razaoSocial;
				document.getElementById('cnpjTransportadora').value = t.cnpj;
				document.getElementById('ieTransportadora').value = t.inscricaoEstadual;
				document.getElementById('endTransportadora').value = t.enderecoFormatado;
				document.getElementById('munTransportadora').value = t.municipioFormatado;
				document.getElementById('ufTransportadora').value = t.ufFormatado;
			});
			request.fail(function(request, status) {
				alert('Falha na pesquisa da transportadora => Status da requisicao: '
						+ status);
			});
		}
	});
	
	<%-- O valor das duplicatas era defini aqui, mas esta dando conflito com o que eh gerado no servidor --%>
	//alterarValorDuplicata();
	
	inicializarBotaoPesquisarCEP({'idBotao':'botaoCepRetirada',
		'idCep': 'cepRetirada', 'idEndereco': 'enderecoRetirada', 'idBairro': 'bairroRetirada', 
		'idCidade': 'cidadeRetirada', 'idUf': 'ufRetirada', 'idPais': ''});
	
	inicializarBotaoPesquisarCEP({'idBotao':'botaoCepEntrega',
		'idCep': 'cepEntrega', 'idEndereco': 'enderecoEntrega', 'idBairro': 'bairroEntrega', 
		'idCidade': 'cidadeEntrega', 'idUf': 'ufEntrega', 'idPais': ''});
	
	inserirMascaraHora('horaSaida');
	
	inserirMascaraData('dataVencimentoDuplicata');
	inserirMascaraData('dataSaida');
	inserirMascaraData('dtImportProd');
	inserirMascaraData('dataDesembImportProd');
	
	inserirMascaraDecimal('valorFretePedido', 10, 2);

	inicializarFadeInBloco('bloco_icms');
	inicializarFadeInBloco('bloco_icms_interestadual');
	inicializarFadeInBloco('bloco_ipi');
	inicializarFadeInBloco('bloco_pis');
	inicializarFadeInBloco('bloco_cofins');
	inicializarFadeInBloco('bloco_ii');
	inicializarFadeInBloco('bloco_iss');
	inicializarFadeInBloco('bloco_tributos');
	inicializarFadeInBloco('bloco_info_adicionais_prod');
	inicializarFadeInBloco('bloco_info_adicionais_nfe');
	inicializarFadeInBloco('bloco_importacao_prod');
	inicializarFadeInBloco('bloco_exportacao_prod');

	inicializarFadeInBloco('bloco_local_mercadoria');
	inicializarFadeInBloco('bloco_referenciada');
	inicializarFadeInBloco('bloco_destinatario');
	inicializarFadeInBloco('bloco_retencao_icms');
	inicializarFadeInBloco('bloco_exportacao');
	inicializarFadeInBloco('bloco_veiculo');
	inicializarFadeInBloco('bloco_transporte', ['bloco_retencao_icms' ,'bloco_veiculo']);
	
	fecharBloco('bloco_local_mercadoria');
	fecharBloco('bloco_referenciada');
	fecharBloco('bloco_exportacao');
	fecharBloco('bloco_retencao_icms');
	fecharBloco('bloco_veiculo');
	
	/* Aqui fazemos com que os blocos de tributos nao sejam visualizados de inicio na tela, mas apenas quando editar o item da nota */
	$('#bloco_tributos').fadeOut('fast');
	$('#bloco_info_adicionais_prod').fadeOut('fast');
	$('#bloco_importacao_prod').fadeOut('fast');
	$('#bloco_exportacao_prod').fadeOut('fast');

	inicializarTabelaImportacaoProd();
	inicializarTabelaAdicaoImportacao();
	inicializarTabelaExportacaoProd();
	inicializarTabelaReferenciada();
	inicializarTabelaVolumes();
	inicializarTabelaReboque();
	inicializarTabelaDuplicata();
	
	inicializarMascaraImpostos();
	inicializarCalculoImpostos();
	
	inicializarMascaraReferenciada();
	inicializarAlteracaoTabelaProdutos();
});

function popularCliente(cliente){
	if(cliente != undefined && cliente != null){
		$('#nomeCliente').val(cliente.razaoSocial);
		$('#cnpj').val(cliente.cnpj);
		$('#cpf').val(cliente.cpf);
		$('#inscricaoEstadual').val(cliente.inscricaoEstadual);
		$('#inscricaoSUFRAMA').val(cliente.inscricaoSUFRAMA);
		$('#telefone').val(removerCaracteresNaoDigitos(cliente.telefone));
		$('#email').val(cliente.email);
		
		var l = cliente.logradouroFaturamento; 
		if(l != undefined && l != null){
			$('#cep').val(l.cep);
			$('#endereco').val(l.endereco);
			$('#numero').val(l.numero);
			$('#complemento').val(l.complemento);
			$('#bairro').val(l.bairro);
			$('#cidade').val(l.cidade);
			$('#uf').val(l.uf);
			$('#pais').val(l.pais);
		}
	}
};

function alterarValorDuplicata(){
	var vlTot = calcularValorTotalProdutosFrete();
	if(!isEmpty(vlTot)){
		vlTot = parseFloat(vlTot);
	} else {
		vlTot = 0;
	}
	var vlDesp= 0;
	var vl = 0;

	<%--Aqui estamos carregando os valores que foram inseridos nos input hidden --%>
	$("input[name*='produtoServicoNFe.outrasDespesasAcessorias']").each(function(){
		if(!isEmpty(vl = $(this).val())){
			vlDesp += parseFloat(vl);
		}
	});
	
	var linhas = document.getElementById('tabela_duplicata').rows;
	<%--Removendo a quantidade referete ao cabecalho da tabela --%>
	var numDuplic = linhas.length -1;
	if(numDuplic<=0){
		return;
	}
	vl = ((vlTot+vlDesp)/numDuplic).toFixed(2);
	for (var i = 1; i < linhas.length; i++) {
		linhas[i].cells[2].innerHTML = vl;
	}
};

function encontrarBotaoEdicaoProduto(proximo){
	var linha = null;
	if(btProduto != null){
		if(proximo){
			linha = btProduto.parentNode.parentNode.nextElementSibling;
		} else {
			linha = btProduto.parentNode.parentNode.previousElementSibling;
		}
		if(linha != null){
			var last = linha.cells.length-1;
			var nodes = linha.cells[last].childNodes;
			for (var i = 0; i < nodes.length; i++) {
				if(nodes[i].nodeType == 1){
					btProduto = nodes[i];
					btProduto.onclick();
					break;
				}					
			}
			scrollTo('produtosInfo');
		}
	}
};

function inicializarAlteracaoTabelaProdutos(){
	var linhas = document.getElementById('tabela_produtos').rows;
	var alterarColuna = function(indice, valor){
		if(numeroProdutoEdicao == null || numeroProdutoEdicao < 0){
			return null;
		}
		
		for (var i = 0; i < linhas.length; i++) {
			if(numeroProdutoEdicao == linhas[i].cells[0].innerHTML){
				linhas[i].cells[indice].innerHTML = valor;
				return linhas[i];
			}
		}
		return null;
	};
	
	$('#bloco_info_adicionais_prod #quantidadeProduto').keyup(function (){
		var qtde = $(this).val();
		var linha  = alterarColuna(4, qtde);
		if(linha != null){
			var vu = linha.cells[5].innerHTML;
			var pICMS = linha.cells[10].innerHTML;
			var pIPI = linha.cells[11].innerHTML;
			var vTot = vu*qtde;
			var cells = linha.cells;
			cells[6].innerHTML = vTot.toFixed(2);
			cells[8].innerHTML = (vTot*pICMS/100).toFixed(2);
			cells[9].innerHTML = (vTot*pIPI/100).toFixed(2);
			
			calcularValoresImpostos(null, false);
			alterarValorDuplicata();
		}
	});
	
	$('#bloco_info_adicionais_prod #codigoProduto').keyup(function (){
		alterarColuna(1, $(this).val());
	});
	
	$('#bloco_info_adicionais_prod #descricaoProduto').keyup(function (){
		alterarColuna(2, $(this).val());
	});
	
	$('#bloco_tributos #cfop').change(function (){
		alterarColuna(13, $(this).val());
	});
	
	$('#bloco_tributos #ncm').keyup(function (){
		alterarColuna(12, $(this).val());
	});
	
	$('#bloco_tributos #aliquotaICMS').keyup(function (){
		alterarColuna(10, $(this).val());
		alterarColuna(8, $('#bloco_tributos #valorICMS').val());
	});
	
	$('#bloco_tributos #aliquotaIPI').keyup(function (){
		alterarColuna(11, $(this).val());
		alterarColuna(9, $('#bloco_tributos #valorIPI').val());
	});
};

function calcularValorTotalProdutosFrete(){
	var linhas = document.getElementById('tabela_produtos').rows;
	var tot = 0;
	for (var i = 1; i < linhas.length; i++) {
		tot += parseFloat(linhas[i].cells[6].innerHTML);
		tot += parseFloat(linhas[i].cells[9].innerHTML);
	}
	tot+=parseFloat(document.getElementById('valorFretePedido').value);
	return tot;
};

function emitirNFe(tipoNFe){
	gerarInputDuplicata();
	gerarInputProdutoServico();
	gerarInputVolume();
	gerarInputReboque();
	gerarInputReferenciada();
	$('#formEmissao #tipoNFe').val(tipoNFe);
	
	$("#formEmissao input, #formEmissao textarea, #formEmissao select" ).each(function(i) {
		/* A remocao dos campos em branco eh necessaria para que o vraptor nao popule os beans com seus atributos todos nulos  */
		if(isEmpty($(this).val())){
			$(this).remove();
		}
	});
	
	$('#formEmissao').submit();
};

function removerProduto(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja REMOVER esse item?',
		confirmar: function(){
			var tabela = document.getElementById('tabela_produtos');
			var l = botao.parentNode.parentNode;
			var indice = l.cells[0].innerHTML;
			tabela.deleteRow(l.rowIndex);
			var nome = 'nf.listaItem['+indice+']';
			$("input[name^='"+nome+"']").each(function(){
				$(this).remove();
			});
			alterarValorDuplicata();
			numeroImportacaoProduto = null;
			numeroProdutoEdicao = null;
			btProduto = null;
		}
	});
};

function inicializarMascaraReferenciada(){
	$('#chaveReferenciada').mask('9999.9999.9999.9999.9999.9999.9999.9999.9999.9999.9999');
};

function inicializarMascaraImpostos(){
	inserirMascaraDecimal('quantidadeProduto', 5, 0);

	inserirMascaraDecimal('valorBCICMS', 15, 2);
	inserirMascaraDecimal('valorBCSTICMS', 15, 2);
	inserirMascaraDecimal('valDesonICMS', 15, 2);
	inserirMascaraDecimal('aliquotaICMS', 7, 4);
	inserirMascaraDecimal('aliquotaSTICMS', 7, 4);
	inserirMascaraDecimal('percValSTICMS', 7, 4);
	inserirMascaraDecimal('percRedBCSTICMS', 7, 4);
	
	inserirMascaraDecimal('percFCPDestICMSInter', 7, 4);
	inserirMascaraDecimal('valorBCICMSInter', 15, 2);
	inserirMascaraDecimal('aliquotaUFDestICMSInter', 7, 4);

	inserirMascaraDecimal('valorBCCOFINS', 15, 2);
	inserirMascaraDecimal('aliquotaCOFINS', 7, 4);
	inserirMascaraDecimal('qtdeVendidaCOFINS', 16, 4);

	inserirMascaraDecimal('valorBCPIS', 15, 2);
	inserirMascaraDecimal('aliquotaPIS', 7, 4);
	inserirMascaraDecimal('qtdeVendidaPIS', 14, 4);
	
	inserirMascaraDecimal('valorBCIPI', 15, 2);
	inserirMascaraDecimal('aliquotaIPI', 7, 4);
	inserirMascaraDecimal('qtdeUnidTribIPI', 16, 4);
	inserirMascaraDecimal('valorUnidTribIPI', 15, 4);
	
	inserirMascaraDecimal('valorBCISS', 15, 2);
	inserirMascaraDecimal('aliquotaISS', 7, 4);
	inserirMascaraDecimal('qtdeVendidaPIS', 14, 4);
	$('#codItemServicoISS').mask('99.99');
	
	inserirMascaraDecimal('valorBCII', 15, 2);
	inserirMascaraDecimal('valorII', 15, 2);
	inserirMascaraDecimal('valorIOFII', 15, 2);
	inserirMascaraDecimal('valorDespAduaneirasII', 15, 2);
	
	inserirMascaraDecimal('despesasAcessoriasProd', 15, 2);
	inserirMascaraDecimal('valorFreteProd', 15, 2);
	inserirMascaraDecimal('valorTotaltributosProd', 15, 2);

	
	inserirMascaraDecimal('qExport', 15, 2);
	
	inserirMascaraDecimal('valServRetICMSTransp', 15, 2);
	inserirMascaraDecimal('valBCRetICMSTransp', 15, 2);
	inserirMascaraDecimal('aliqRetICMSTransp', 7, 4);
	
	inserirMascaraDecimal('pesoLiquidoVolume', 15, 3);
	inserirMascaraDecimal('pesoBrutoVolume', 15, 3);
	
	inserirMascaraDecimal('valorDuplicata', 15, 2);
};

function inicializarTabelaImportacaoProd(){
	var campos = ['cnpjImportProd', 'exportadorImportProd', 'dtImportProd', 'dataDesembImportProd',
		          'lcImportProd', 'numImportProd', 'tpImportProd', 'tpTranspImportProd', 'ufDesembImportProd',
		          'ufEncomendImportProd', 'vlAFRMMImportProd'];
	var config = {'idTabela': 'tabela_importacao_prod', 'idBotaoInserir':'botaoInserirImportacaoProd',
			'campos': campos,
			'idLinhaSequencial': true,
			'onValidar': function(){
				return numeroProdutoEdicao != null && obrigatorioPreenchido(campos, ['cnpjImportProd', 'ufEncomendImportProd', 'vlAFRMMImportProd']);
			},
			'onInserir': function(linha){
				if(numeroProdutoEdicao == null || linha == null){
					return;
				}
				/* EStamos definindo qual o numero de importacao aqui para que o usuario possa inserir as adicoes em sequencia pois la temos que saber qua eh a importacao associada */
				var isNovo = numeroImportacaoProduto == null || numeroImportacaoProduto != linha.id;
				if(isNovo){
					numeroImportacaoProduto = linha.id;
				}
				recuperarAdicaoImportacao();
				 $('#bloco_adicao_import').fadeIn('fast');
				var celulas = linha.cells;
				var json = {'nomeObjeto': 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.listaImportacao['+linha.id+']', 
					  'campos':[{'nome':'cnpjEncomendante', 'valor':celulas[0].innerHTML},
					            {'nome':'codigoExportador', 'valor':celulas[1].innerHTML},
					            {'nome':'dataImportacao', 'valor':celulas[2].innerHTML},
					            {'nome':'dataDesembaraco', 'valor':celulas[3].innerHTML},
					            {'nome':'localDesembaraco', 'valor':celulas[4].innerHTML},
					            {'nome':'numero', 'valor':celulas[5].innerHTML},
					            {'nome':'tipoIntermediacao', 'valor':celulas[6].innerHTML},
					            {'nome':'tipoTransporteInternacional', 'valor':celulas[7].innerHTML},
					            {'nome':'ufDesembaraco', 'valor':celulas[8].innerHTML},
					            {'nome':'ufEncomendante', 'valor':celulas[9].innerHTML},
					            {'nome':'valorAFRMM', 'valor':celulas[10].innerHTML}]};
					
				gerarInputHidden(json);
			 },
			 'onEditar': function(linha){
				 numeroImportacaoProduto = linha.id;
				 recuperarAdicaoImportacao();
				 $('#bloco_adicao_import').fadeIn('fast');
			 },
			'onRemover': function(linha){
				$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].produtoServicoNFe.listaImportacao["+linha.id+"]']").each(function(i){
					$(this).remove();
				});
				 $('#bloco_adicao_import').fadeOut('fast');
			}};
	
	editorTabelaImportacao  = new editarTabela(config);
};

function inicializarTabelaAdicaoImportacao(){
	var campos = ['codFabricAdicao', 'numAdicao', 'numDrawbackAdicao', 'numSeqAdicao', 'valDescAdicao'];
	var config = {'idTabela': 'tabela_adicao_import', 'idBotaoInserir':'botaoInserirAdicao',
			'campos':campos,
			'idLinhaSequencial': true,
			'onValidar': function(){
				return numeroProdutoEdicao != null && numeroImportacaoProduto != null && obrigatorioPreenchido(campos, ['numDrawbackAdicao', 'valDescAdicao']);
			},
			'onInserir': function(linha){
				if(numeroProdutoEdicao == null || numeroImportacaoProduto == null || linha == null){
					return;
				}
				
				var celulas = linha.cells;
				var json = {'nomeObjeto': 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.listaImportacao['+numeroImportacaoProduto+'].listaAdicao['+linha.id+']', 
					  'campos':[{'nome':'codigoFabricante', 'valor':celulas[0].innerHTML},
					            {'nome':'numero', 'valor':celulas[1].innerHTML},
					            {'nome':'numeroDrawback', 'valor':celulas[2].innerHTML},
					            {'nome':'numeroSequencialItem', 'valor':celulas[3].innerHTML},
					            {'nome':'valorDesconto', 'valor':celulas[4].innerHTML}
					            ]};
					
				gerarInputHidden(json);
			 },
			 'onRemover': function(linha){
					$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].produtoServicoNFe.listaImportacao["+numeroImportacaoProduto+"].listaAdicao["+linha.id+"]']").each(function(i){
						$(this).remove();
					});	
			 }};
	
	editorTabelaAdicao = new editarTabela(config);
};

function inicializarTabelaExportacaoProd(){
	var campos = ['drawbackExportProd', 'chAcessoExportIndir', 'registroExportIndir', 'qtdeExportIndir'];
	var config = {'idTabela': 'tabela_exportacao_prod', 'idBotaoInserir':'botaoInserirExportacaoProd',
			'campos': campos,
			'idLinhaSequencial': true,
			'onValidar': function(){
				return numeroProdutoEdicao != null && obrigatorioPreenchido(campos, ['drawbackExportProd']);
			},
			'onInserir': function(linha){
				if(numeroProdutoEdicao == null || linha == null){
					return;
				}
				
				var celulas = linha.cells;
				var json = {'nomeObjeto': 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.listaExportacao['+linha.id+']', 
					  'campos':[{'nome':'numeroDrawback', 'valor':celulas[0].innerHTML},
					            {'nome':'expIndireta.chaveAcessoRecebida', 'valor':celulas[1].innerHTML},
					            {'nome':'expIndireta.numeroRegistro', 'valor':celulas[2].innerHTML},
					            {'nome':'expIndireta.quantidadeItem', 'valor':celulas[3].innerHTML},
								]};
					
				gerarInputHidden(json);
			 },
			'onRemover': function(linha){
				$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].produtoServicoNFe.listaExportacao["+linha.id+"]']").each(function(i){
					$(this).remove();
				});
			}};
	
	editorTabelaExportacao = new editarTabela(config);
};

function obrigatorioPreenchido(ids, idExcl){
	for (var i = 0; i < ids.length; i++) {
		if(idExcl != null && idExcl.indexOf(ids[i]) >= 0){
			continue;
		}
		
		if(isEmpty(document.getElementById(ids[i]).value)){
			return false;
		}
	}
	return true;
};

function algumPreenchido(ids){
	for (var i = 0; i < ids.length; i++) {
		if(!isEmpty(document.getElementById(ids[i]).value)){
			return true;
		}
	}
	return false;
};

function inicializarTabelaReferenciada(){
	var campos = ['chaveReferenciada', 'numeroReferenciada', 'serieReferenciada', 'modReferenciada', 'cnpjReferenciada', 'anoMesReferenciada', 'ufReferenciada'];
	var config = {'idTabela': 'tabela_referenciada', 'idBotaoInserir':'botaoInserirReferenciada',
			'campos': campos,
			'onValidar': function(){
				return algumPreenchido(campos);
			},
			'idLinhaSequencial':true};
	editarTabela(config);
};

function inicializarTabelaVolumes(){
	var campos = ['quantidadeVolume', 'especieVolume', 'marcaVolume', 'numeracaoVolume', 'pesoLiquidoVolume', 'pesoBrutoVolume'];
	var config = {'idTabela': 'tabela_volume', 'idBotaoInserir':'botaoInserirVolume',
			'campos': campos,
			'onValidar': function(){
				return algumPreenchido(campos);
			},
			'idLinhaSequencial':true};
	editarTabela(config);
};

function inicializarTabelaDuplicata(){
	var campos = ['numeroDuplicata', 'dataVencimentoDuplicata', 'valorDuplicata'];
	var config = {'idTabela': 'tabela_duplicata', 'idBotaoInserir':'botaoInserirDuplicata',
			'campos': campos,
			'idLinhaSequencial': true,
			'onValidar': function(){
				return obrigatorioPreenchido(campos, ['numeroDuplicata', 'dataVencimentoDuplicata']);
			},
			'onRemover':function(linha){
				alterarValorDuplicata();
			},
			'idLinhaSequencial':true};
	editarTabela(config);
};

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
		/* Devemos excluir a ultima celula da tabela pois eh a celula de botoes de acoes */
		max = celulas.length - 1;
		for (var j= 0; j < max; j++) {
			campos[j] = {'nome':parametroJson.nomes[j] == null?'':parametroJson.nomes[j], 'valor':celulas[j].innerHTML};
		}
		gerarInputHidden({'nomeObjeto': parametroJson.nomeLista+'['+i+']', 'campos':campos});
	}
};

function gerarLegendaBloco(nomeBloco){
	var legend = $('#'+nomeBloco+' legend:first');
	var innerHTML = $(legend).html();
	if(innerHTML.indexOf('+') != -1){
		innerHTML = innerHTML.replace(/\+/g, '-');
	} else {
		innerHTML = innerHTML.replace(/\-/g, '+');
	}
	$(legend).html(innerHTML);
};

function abrirBloco(nomeBloco, blocosFechados){
	gerarLegendaBloco(nomeBloco);
	/* Aqui estamos evitando que o div de autocomplete de cliente seja exibido pelo fadeIn */
	$('#'+nomeBloco+' div:not(.suggestionsBox), '+'#'+nomeBloco+' table, #'+nomeBloco+' .fieldsetInterno').fadeIn('fast');
	if(blocosFechados == undefined || blocosFechados == null){
		return;
	}
	for (var i = 0; i < blocosFechados.length; i++) {
		fecharBloco(blocosFechados[i]);
	}
};

function fecharBloco(nomeBloco){
	gerarLegendaBloco(nomeBloco);
	/* Aqui estamos evitando que o div de autocomplete de cliente seja exibido pelo fadeIn */
	$('#'+nomeBloco+' div:not(.suggestionsBox), '+'#'+nomeBloco+' table, #'+nomeBloco+' .fieldsetInterno').fadeOut('fast');
};

function inicializarLegendaBlocoProduto(nomeBloco){
	var legend = $('#'+nomeBloco+' legend:first');
	$(legend).html(legend.html().replace(/Prod.\s*\d*/g, 'Prod. '+(numeroProdutoEdicao)+' '));	

	legend.html(legend.html().replace(/\+/g, '-'));
	$('#'+nomeBloco+' div:not(.suggestionsBox)').fadeIn('fast');
};

function inicializarFadeInBloco(nomeBloco, blocosFechados){
	$('#'+nomeBloco+' legend:first').click(function(){
		var innerHTML = $(this).html();
		if(innerHTML.indexOf('+') != -1){
			abrirBloco(nomeBloco, blocosFechados);
		} else {
			fecharBloco(nomeBloco);
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
		document.getElementById(campos[i].id).value = '';
	};
};

function gerarInputHidden(objeto){
	var form = document.getElementById('formEmissao');
	var input = null;
	var campos = objeto.campos;
	var nome = null;
	for (var i = 0; i < campos.length; i++) {
		nome = objeto.nomeObjeto +'.'+campos[i].nome;
		/* Devemos verificar se o input ja foi criado pelo usuario, caso nao existe devemos cria-lo */
		if((input = document.getElementById(nome)) == undefined || input == null){
			input = document.createElement('input');
			input.type = 'hidden';
			input.name = nome;
			/* Estamos usando o fato de que os parametros enviados ao servidor tem um unico nome, por exemplo o primeiro e o segundo elemento de uma lista 
			contendo o atributo situacao tributaria sera enviado como nf.listaItem[0].situacaoTributaria e nf.listaItem[1].situacaoTributaria, e devem ser unicos 
			para que os parametros da nfe sejam preenchidos corretamente, com isso podemos usar como id */
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
		          {'nome':'modalidadeDeterminacaoBC', 'id':'modBCICMS'},
		          {'nome':'modalidadeDeterminacaoBCST', 'id':'modBCSTICMS'},
		          {'nome':'percentualMargemValorAdicionadoICMSST', 'id':'percValSTICMS'},
		          {'nome':'percentualReducaoBCST', 'id':'percRedBCSTICMS'},
		          {'nome':'valorBC', 'id':'valorBCICMS'},
		          {'nome':'valorBCST', 'id':'valorBCSTICMS'},
		          {'nome':'valorDesonerado', 'id':'valDesonICMS'},
		          {'nome':'aliquotaST', 'id':'aliquotaSTICMS'},
		          {'nome':'motivoDesoneracao', 'id':'motDesonerICMS'},
		          {'nome':'origemMercadoria', 'id':'origemMercadoriaICMS'},
			]};
};

function gerarJsonIcmsInterestadual(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.icmsInterestadual',
		'campos':[{'nome':'valorBCUFDestino', 'id':'valorBCICMSInter'},
		          {'nome':'percentualFCPDestino', 'id':'percFCPDestICMSInter'},
		          {'nome':'aliquotaUFDestino', 'id':'aliquotaUFDestICMSInter'},
		          {'nome':'percentualProvisorioPartilha', 'id':'percProvPartilhaICMSInter'},
		          {'nome':'aliquotaInterestadual', 'id':'aliquotaICMSInter'}
			]};
};

function gerarJsonCfopNcm(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe',
			'campos':[{'nome':'cfop', 'id':'cfop'}, {'nome':'ncm', 'id':'ncm'}, {'nome':'cest', 'id':'cest'}]};
};

function gerarJsonEnquadramentoIpi(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.ipi',
		'campos':[{'nome':'classeEnquadramentoCigarrosBebidas', 'id':'clEnquadramentoIPI'},
		          {'nome':'codigoEnquadramento', 'id':'codEnquadramentoIPI'},
		          {'nome':'cnpjProdutor', 'id':'cnpjProdIPI'},
		          {'nome':'codigoSeloControle', 'id':'codSeloContrIPI'},
		          {'nome':'quantidadeSeloControle', 'id':'qtdeSeloContrIPI'}]};
};

function gerarJsonTipoIpi(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.ipi.tipoIpi',
		'campos':[{'nome':'aliquota', 'id':'aliquotaIPI'},
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribIPI'},
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
		          {'nome':'valorBC', 'id':'valorBCCOFINS'}
			]};
};

function gerarJsonISS(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.issqn',
		'campos':[{'nome':'aliquota', 'id':'aliquotaISS'},
		          {'nome':'valorBC', 'id':'valorBCISS'},
		          {'nome':'codigoMunicipioGerador', 'id':'codMunGeradorISS'},
		          {'nome':'itemListaServicos', 'id':'codItemServicoISS'}
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

function gerarJsonInfoProduto(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+']',
		'campos':[{'nome':'informacoesAdicionais', 'id':'infoAdicionaisProd'}, 
		          {'nome': 'tributos.valorTotalTributos', 'id':'valorTotaltributosProd'},
		          {'nome': 'produtoServicoNFe.outrasDespesasAcessorias', 'id':'despesasAcessoriasProd'},
		          {'nome': 'produtoServicoNFe.valorTotalFrete', 'id':'valorFreteProd'},
		          {'nome': 'produtoServicoNFe.numeroPedidoCompra', 'id':'numeroPedidoCompraProd'},
		          {'nome': 'produtoServicoNFe.itemPedidoCompra', 'id':'itemPedidoCompraProd'},
		          {'nome': 'produtoServicoNFe.fichaConteudoImportacao', 'id':'fciProd'}]};
};

function gerarJsonTipoPis(){
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.pis.tipoPis',
		'campos':[{'nome':'aliquota', 'id':'aliquotaPIS'},
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribPIS'},
		          {'nome':'quantidadeVendida', 'id':'qtdeVendidaPIS'},
		          {'nome':'valorBC', 'id':'valorBCPIS'}
			]};
};

function recuperarImportacaoProduto(){
	// limpando a tabela toda
	$("#tabela_importacao_prod tbody tr").remove();
	var id = null;
	var nome = null;
	var valor = null;
	var total = 11;
	var nome = 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.listaImportacao[';
	$("input[name^='"+nome+"']").each(function(){
		nome = $(this).attr('name');
		if(nome.split('.').length == 5){
			if(id == null){
				// Gerando o id da linha que sera utilizado para gerar os inputs que serao enviados para o servidor
				id = nome.match(/\d+/g)[1];
			}
			nome = nome.substring(nome.lastIndexOf('.')+1);
		 	valor = $(this).val();
		 	
			if(nome == 'cnpjEncomendante'){
				$('#bloco_importacao_prod #cnpjImportProd').val(valor);
				total--;
			} else if(nome == 'codigoExportador'){
				$('#bloco_importacao_prod #exportadorImportProd').val(valor);
				total--;
			} else if(nome == 'dataImportacao'){
				$('#bloco_importacao_prod #dtImportProd').val(valor);
				total--;
			} else if(nome == 'dataDesembaraco'){
				$('#bloco_importacao_prod #dataDesembImportProd').val(valor);
				total--;
			} else if(nome == 'localDesembaraco'){
				$('#bloco_importacao_prod #lcImportProd').val(valor);
				total--;
			} else if(nome == 'numero'){
				$('#bloco_importacao_prod #numImportProd').val(valor);
				total--;
			} else if(nome == 'tipoIntermediacao'){
				$('#bloco_importacao_prod #tpImportProd').val(valor);
				total--;
			} else if(nome == 'tipoTransporteInternacional'){
				$('#bloco_importacao_prod #tpTranspImportProd').val(valor);
				total--;
			} else if(nome == 'ufDesembaraco'){
				$('#bloco_importacao_prod #ufDesembImportProd').val(valor);
				total--;
			} else if(nome == 'ufEncomendante'){
				$('#bloco_importacao_prod #ufEncomendImportProd').val(valor);
				total--;
			} else if(nome == 'valorAFRMM'){
				$('#bloco_importacao_prod #vlAFRMMImportProd').val(valor);
				total--;
			}
			
			if(total <= 0) {
				editorTabelaImportacao.inserirLinha(id);
				total = 11;
				id = null;
			}
		}
	});
	
};

function recuperarAdicaoImportacao(){
	// limpando a tabela toda
	$("#tabela_adicao_import tbody tr").remove(); 
	
	var id = null;
	var nome = null;
	var valor = null;
	var total = 5;
	var nome = 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.listaImportacao['+numeroImportacaoProduto+'].listaAdicao[';
	$("input[name^='"+nome+"']").each(function(){
		nome = $(this).attr('name');
		if(nome.split('.').length == 6){
			if(id == null){
				// Gerando o id da linha que sera utilizado para gerar os inputs que serao enviados para o servidor
				id = nome.match(/\d+/g)[2];
			}
			nome = nome.substring(nome.lastIndexOf('.')+1);
		 	valor = $(this).val();
		 	
			if(nome == 'codigoFabricante'){
				$('#bloco_importacao_prod #codFabricAdicao').val(valor);
				total--;
			} else if(nome == 'numero'){
				$('#bloco_importacao_prod #numAdicao').val(valor);
				total--;
			} else if(nome == 'numeroDrawback'){
				$('#bloco_importacao_prod #numDrawbackAdicao').val(valor);
				total--;
			} else if(nome == 'numeroSequencialItem'){
				$('#bloco_importacao_prod #numSeqAdicao').val(valor);
				total--;
			} else if(nome == 'valorDesconto'){
				$('#bloco_importacao_prod #valDescAdicao').val(valor);
				total--;
			}
			
			if(total <= 0) {
				editorTabelaAdicao.inserirLinha(id);
				total = 5;
				id = null;
			}
		}
	});
};

function recuperarExportacaoProduto(){
	// limpando a tabela toda
	$("#tabela_exportacao_prod tbody tr").remove(); 
	
	var id = null;
	var nome = null;
	var total = 4;
	var indice = -1;
	var nome = 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.listaExportacao[';
	$("input[name^='"+nome+"']").each(function(){
		nome = $(this).attr('name');
		indice = nome.split('.').length;
		if(indice == 5){
			nome = nome.substring(nome.lastIndexOf('.')+1);
			if(nome == 'numeroDrawback'){
				$('#bloco_exportacao_prod #drawbackExportProd').val($(this).val());
				total--;
			} 
		} else if(indice == 6){
			if(id == null){
				// Gerando o id da linha que sera utilizado para gerar os inputs que serao enviados para o servidor
				id = nome.match(/\d+/g)[1];
			}
			nome = nome.substring(nome.lastIndexOf('.')+1);
		 	
			if(nome == 'chaveAcessoRecebida'){
				$('#bloco_exportacao_prod #chAcessoExportIndir').val($(this).val());
				total--;
			} else if(nome == 'numeroRegistro'){
				$('#bloco_exportacao_prod #registroExportIndir').val($(this).val());
				total--;
			} else if(nome == 'quantidadeItem'){
				$('#bloco_exportacao_prod #qtdeExportIndir').val($(this).val());
				total--;
			}
		}
		
		if(total <= 0) {
			editorTabelaExportacao.inserirLinha(id);
			total = 4;
			id = null;
		}
	});
};

function gerarInputICMS(){
	var tipoIcms = gerarJsonTipoIcms();
	var cfop = gerarJsonCfopNcm();
	
	gerarInputHidden(tipoIcms);
	gerarInputHidden(cfop);
};

function gerarInputICMSInter(){
	var icmsInterestadual = gerarJsonIcmsInterestadual();
	gerarInputHidden(icmsInterestadual);
};

function gerarInputInfoProduto(){
	gerarInputHidden(gerarJsonInfoProduto());
};

function gerarInputIPI(){
	var tipoIpi = gerarJsonTipoIpi();
	var ipi = gerarJsonEnquadramentoIpi();
	
	gerarInputHidden(tipoIpi);
	gerarInputHidden(ipi);
};

function gerarInputPIS(){
	gerarInputHidden(gerarJsonTipoPis());
};

function gerarInputCOFINS(){
	gerarInputHidden(gerarJsonTipoCofins());	
};

function gerarInputISS(){
	gerarInputHidden(gerarJsonISS());	
};

function gerarInputImpostoImportacao(){
	gerarInputHidden(gerarJsonImpostoImportacao());	
};

function gerarInputDuplicata(){
	var parametros = {'nomeLista': 'nf.cobrancaNFe.listaDuplicata',
			'nomes': ['numero', 'dataVencimento', 'valor']};
	gerarInputLinhasTabela('tabela_duplicata', parametros);
};

function gerarInputReboque(){
	var parametros = {'nomeLista': 'nf.transporteNFe.listaReboque',
					'nomes': ['placa', 'uf', 'registroNacionalTransportador']};
	gerarInputLinhasTabela('tabela_reboque', parametros);
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
	var numItem = null;
	for (var i = 0; i < linhas.length; i++) {
		celulas = linhas[i].cells;
		numItem = celulas[0].innerHTML;
		detalhamento = {'nomeObjeto':'nf.listaItem['+numItem+']',
			'campos':[{'nome':'numeroItem', 'valor': numItem}]};
		
		produto = {'nomeObjeto':'nf.listaItem['+numItem+'].produtoServicoNFe',
				'campos':[{'nome':'codigo', 'valor': celulas[1].innerHTML},
				          {'nome':'descricao', 'valor': celulas[2].innerHTML},
				          {'nome':'unidadeComercial', 'valor': celulas[3].innerHTML},
				          {'nome':'quantidadeComercial', 'valor': celulas[4].innerHTML},
				          {'nome':'quantidadeTributavel', 'valor': celulas[4].innerHTML},
				          {'nome':'valorUnitarioComercializacao', 'valor': celulas[5].innerHTML},
				          {'nome':'valorTotalBruto', 'valor': celulas[6].innerHTML},
				          {'nome':'unidadeTributavel', 'valor': celulas[3].innerHTML},
				          {'nome':'valorUnitarioTributacao', 'valor': celulas[5].innerHTML}
					]};
		
		gerarInputHidden(detalhamento);
		gerarInputHidden(produto);
	}
};

function inicializarTabelaReboque(){
	var campos = ['placaVeiculo', 'ufVeiculo', 'registroVeiculo'];
	var config = {'idTabela': 'tabela_reboque', 'idBotaoInserir':'botaoInserirReboque',
			'campos': campos,
			'idLinhaSequencial': true,
			'onValidar': function(){
				return obrigatorioPreenchido(campos, ['registroVeiculo']);
			},
			'idLinhaSequencial':true};
	editarTabela(config);
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
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja DESATIVAR esse ramo de atividade?',
		confirmar: function(){
			$(botao).closest('form').submit();	
		}
	});
};
<%--Essa funcao verifica se existe algum input hidden contendo os valores dos impostos, e caso nao contenha ele inclui os inputs no documento 
para manter o estado dos impostos que estao sendo preenchidos. Eh sempre executado no click de edicao de um item --%>
function recuperarValoresProduto(valoresTabela){
	var impostos = new Array();
	impostos [0] = gerarJsonTipoIcms();
	impostos [1] = gerarJsonTipoIpi();
	impostos [2] = gerarJsonTipoPis();
	impostos [3] = gerarJsonTipoCofins();
	impostos [4] = gerarJsonISS();
	impostos [5] = gerarJsonImpostoImportacao();
	impostos [6] = gerarJsonInfoProduto();
	impostos [7] = gerarJsonCfopNcm();
	impostos [8] = gerarJsonEnquadramentoIpi();
	impostos [9] = gerarJsonIcmsInterestadual();
	
	var idInput = null;
	var idBloco = null;
	var valorInput = null;
	var valorBloco = null;
	var idIgual = null;
	var camposImposto = null;
	var criadoInput = null;
	var nomeLista = null;
	var camposTabela = valoresTabela.campos;
	
	for (var i = 0; i < impostos.length; i++) {
		camposImposto = impostos[i].campos;
		nomeLista = impostos[i].nomeObjeto;
		impostos: 
		for (var k = 0; k < camposImposto.length; k++) {
			idInput = nomeLista + '.' + camposImposto[k].nome;
			idBloco = camposImposto[k].id;
			
			for (var j = 0; j < camposTabela.length; j++) {
				idIgual = idBloco == camposTabela[j].id;
				if(!idIgual){
					continue;
				}
				
				criadoInput = document.getElementById(idInput) != undefined;
				if(!criadoInput || isEmpty(document.getElementById(idInput).value)){
					document.getElementById(idBloco).value = camposTabela[j].valorTabela;
					continue impostos;
				} else {
					document.getElementById(idBloco).value = document.getElementById(idInput).value;
					continue impostos;
				}
			}
			criadoInput = document.getElementById(idInput) != undefined;
			if(criadoInput){
				document.getElementById(idBloco).value = document.getElementById(idInput).value;
			} else {
				document.getElementById(idBloco).value = '';
			}
		}
	}
};

function inicializarBotaoPesquisarCEP(config){
	$('#'+config.idBotao).click(function () {
		var cep = $('#'+config.idCep).val(); 
		if (cep == undefined || cep == null || cep.trim().length == 0) {
			gerarListaMensagemAlerta(new Array('O CEP precisa ser preenchido para a pesquisa de endereço'));
			return;
		}
		
		var request = $.ajax({
							type: "get",
							url: '<c:url value="/cep/endereco"/>',
							data: 'cep='+$('#'+config.idCep).val(),
						});
		request.done(function(response) {
			var endereco = response.endereco;
			if(endereco == undefined){
				return;
			}
			$('#'+config.idEndereco).val(endereco.descricao);
			$('#'+config.idBairro).val(endereco.bairro.descricao);
			$('#'+config.idCidade).val(endereco.cidade.descricao);
			$('#'+config.idUf).val(endereco.cidade.uf);
			$('#'+config.idPais).val(endereco.cidade.pais.descricao);
		});
		
		request.fail(function(request, status) {
			alert('Falha na busca do CEP: ' + $('#'+config.idCep).val()+' => Status da requisicao: '+status);
		});
	});	
	
};

function inicializarValoresDefaultProduto(valDefault){
	var campos = valDefault.campos;
	var id = null;
	var campo = null;
	var hidden = null;
	for (var i = 0; i < campos.length; i++) {
		if((hidden = document.getElementById(campos[i].idHidden)) != undefined && !isEmpty(hidden.value)){
			continue;
		}
		campo = document.getElementById(campos[i].idCampo);
		campo.value = campos[i].valor;
		if($(campo).is('select')){
			campo.selected='selected';
		}
	}
};

function editarProduto(botao){
	btProduto = botao;
	var linha = botao.parentNode.parentNode;
	var celulas = linha.cells;

	/* Aqui estamos apenas dando condicao ao usuario para alterar a descricao dos produtos da tabela que serao enviados para gerar a NFe */
	$('#bloco_info_adicionais_prod #quantidadeProduto').val(celulas[4].innerHTML);
	$('#bloco_info_adicionais_prod #codigoProduto').val(celulas[1].innerHTML);
    $('#bloco_info_adicionais_prod #descricaoProduto').val(celulas[2].innerHTML);
    
    /* Estamos supondo que a sequencia do item do pedido eh unica */
	numeroProdutoEdicao = celulas[0].innerHTML;
    /* A execucao dos metodos abaixo dependem da definicao do numero do produto que esta sendo editado */
	var valorBC = celulas[6].innerHTML;
	var valoresTabela = {'campos':[{'id': 'itemPedidoCompraProd', 'valorTabela':celulas[0].innerHTML},
								   {'id': 'ncm', 'valorTabela': celulas[12].innerHTML},
								   {'id': 'cfop', 'valorTabela': celulas[13].innerHTML},
	                               {'id': 'aliquotaICMS', 'valorTabela': celulas[10].innerHTML},
	                               {'id': 'aliquotaIPI', 'valorTabela': celulas[11].innerHTML},
	                               {'id': 'valorBCPIS', 'valorTabela': valorBC},
	                               {'id': 'valorBCCOFINS', 'valorTabela': valorBC},
	                               {'id': 'valorBCICMS', 'valorTabela': valorBC},
	                               {'id': 'valorBCIPI', 'valorTabela': valorBC},
	                               {'id': 'aliquotaPIS', 'valorTabela': $('#percentualPis').val()},
	                               {'id': 'aliquotaCOFINS', 'valorTabela': $('#percentualCofins').val()}
	                               ]};
	recuperarValoresProduto(valoresTabela);
	calcularValoresImpostos(null, false);

	/* Aqui estamos apenas dando condicao ao usuario para alterar a descricao dos produtos da tabela que serao enviados para gerar a NFe */
	$('#bloco_info_adicionais_prod #quantidadeProduto').val(celulas[4].innerHTML);
	$('#bloco_info_adicionais_prod #codigoProduto').val(celulas[1].innerHTML);
    $('#bloco_info_adicionais_prod #descricaoProduto').val(celulas[2].innerHTML);
	recuperarImportacaoProduto();
	recuperarExportacaoProduto();
	
	$('#bloco_tributos').fadeIn('fast');
	$('#bloco_info_adicionais_prod').fadeIn('fast');
	$('#bloco_importacao_prod').fadeIn('fast');
	$('#bloco_exportacao_prod').fadeIn('fast');
	
	inicializarLegendaBlocoProduto('bloco_icms');
	inicializarLegendaBlocoProduto('bloco_icms_interestadual');
	inicializarLegendaBlocoProduto('bloco_ipi');
	inicializarLegendaBlocoProduto('bloco_pis');
	inicializarLegendaBlocoProduto('bloco_cofins');
	inicializarLegendaBlocoProduto('bloco_ii');
	inicializarLegendaBlocoProduto('bloco_iss');
	inicializarLegendaBlocoProduto('bloco_tributos');
	inicializarLegendaBlocoProduto('bloco_info_adicionais_prod');
	inicializarLegendaBlocoProduto('bloco_importacao_prod');
	inicializarLegendaBlocoProduto('bloco_exportacao_prod');

	fecharBloco('bloco_ipi');
	fecharBloco('bloco_icms_interestadual');
	fecharBloco('bloco_ii');
	fecharBloco('bloco_iss');
	fecharBloco('bloco_importacao_prod');
	fecharBloco('bloco_exportacao_prod');
	fecharBloco('bloco_adicao_import');
	
	/* Aqui estamos atribuindo um valor default para o rapido preenchimento da tela pelo usuario. Esses valores sao utilizados com maior frequencia. */
	var valDefault = {'campos':
		[{'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.cfop', 'idCampo': 'cfop', 'valor': '5102'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms.codigoSituacaoTributaria', 'idCampo': 'tipoTributacaoICMS', 'valor': '00'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms.origemMercadoria', 'idCampo': 'origemMercadoriaICMS', 'valor': '0'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms.modalidadeDeterminacaoBC', 'idCampo': 'modBCICMS', 'valor': '0'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.cofins.tipoCofins.codigoSituacaoTributaria', 'idCampo': 'codSitTribCOFINS', 'valor': '01'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.pis.tipoPis.codigoSituacaoTributaria', 'idCampo': 'codSitTribPIS', 'valor': '01'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].informacoesAdicionais', 'idCampo': 'infoAdicionaisProd', 'valor': 'Total aproximado de tributos federais, estaduais e municipais: '+$('#valorTotaltributosProd').val()}]};
	inicializarValoresDefaultProduto(valDefault);
	
	/* Preenchendo o valor do frete que ja foi inserido no cadastro do pedido e negociado com o vendedor */
	var vFrete = document.getElementById('nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.valorTotalFrete');
	var contemFrete = vFrete != undefined && vFrete != null && !isEmpty(vFrete.value);
	if(!contemFrete){
		$('#valorFreteProd').val(calcularValorFreteProduto());
	}
};

function gerarJsonCalculoImpostos(){
	return [{'idVl':'valorBCICMS', 'idAliq':'aliquotaICMS', 'idImp':'valorICMS'},
			{'idVl':'valorBCCOFINS', 'idAliq':'aliquotaCOFINS', 'idImp':'valorCOFINS'},
	    	{'idVl':'valorBCPIS', 'idAliq':'aliquotaPIS', 'idImp':'valorPIS'},
	    	{'idVl':'valorBCIPI', 'idAliq':'aliquotaIPI', 'idImp':'valorIPI'},
	    	{'idVl':'valorBCISS', 'idAliq':'aliquotaISS', 'idImp':'valorISS'}];	
};

function calcularValorFreteProduto(){
	var qtde=document.getElementById('tabela_produtos').rows.length-1;
	if(qtde<=0){
		return 0;
	}
	var val = parseFloat(document.getElementById('valorFretePedido').value);
	return val / qtde;
};

function calcularValorICMSInterestadual(){
	var parametros = '';
	var campos = gerarJsonIcmsInterestadual().campos;
	
	for (var i = 0; i< campos.length; i++) {
		parametros += 'icms.'+campos[i].nome+'='+$('#bloco_icms_interestadual #'+campos[i].id).val()+'&';	
	}
	
	var request = $.ajax({
		type: "get",
		url: '<c:url value="/emissaoNFe/valorICMSInterestadual"/>',
		data: parametros,
	});
	request.done(function(response) {
		var icms = response.icms;
		if(icms == undefined){
			return;
		}
		$('#bloco_icms_interestadual #valorFCPICMSInter').val(icms.valorFCPDestino);
		$('#bloco_icms_interestadual #valorICMSInter').val(icms.valorUFDestino);
		$('#bloco_icms_interestadual #valorPartICMSInter').val(icms.valorUFRemetente);
	});
	
	request.fail(function(request, status) {
		alert('Falha no calculo do ICMS interestadual => Status da requisicao: '+status);
	});	
};

function calcularValoresImpostos(idValorRemovido, isAlteracaoAliq){
	var campos = gerarJsonCalculoImpostos();
	var aliq=null; 
	var idImp=null;
	var tot = 0;
	var vBC = 0;
	var vl = 0;
	if(btProduto != null){
		linha = btProduto.parentNode.parentNode;
		vBC = linha.cells[6].innerHTML;
	} else {
		return;
	}
	if(isEmpty(vBC)){
		return;
	}
	
	var frete = document.getElementById('valorFretePedido').value;
	if(isEmpty(frete)){
		frete = 0;
	}

	vBC = parseFloat(vBC)+parseFloat(frete);
	for (var i = 0; i < campos.length; i++) {
		aliq = document.getElementById(campos[i].idAliq).value;
		if(isEmpty(aliq)){
			continue;
		}
		
		if(isAlteracaoAliq){
			vBC = document.getElementById(campos[i].idVl).value;
		} else {
			document.getElementById(campos[i].idVl).value = vBC;
		}
		
		if(idValorRemovido != undefined && idValorRemovido == campos[i].idVl){
			vl = 0;
		} else {
			vl = Math.round(vBC*(aliq/100) * 100)/100;
		}
		tot += vl;
		document.getElementById(campos[i].idImp).value = vl;
	}
	tot = tot.toFixed(2);
	document.getElementById('valorTotaltributosProd').value = tot;
	
	var info = document.getElementById('infoAdicionaisProd');
	info.value = info.value.split(':')[0]+': '+tot;
	
	calcularValorICMSInterestadual();
	calcularValorUnidadeTributavelIPI();
};

function zerarImposto(idImposto){
	return calcularValoresImpostos(idImposto, false);
};

function calcularAlteracaoAliquota(){
	return calcularValoresImpostos(null, true);
};

function calcularValorUnidadeTributavelIPI(){
	var qtde = $('#bloco_info_adicionais_prod #quantidadeProduto').val();
	var vlIPI = $('#bloco_tributos #valorIPI').val();
	if(isEmpty(qtde) || isEmpty(vlIPI) || qtde == 0 || vlIPI == 0){
		$('#bloco_tributos #valorUnidTribIPI, #bloco_tributos #qtdeUnidTribIPI').val(0);
		return;
	}
	$('#bloco_tributos #qtdeUnidTribIPI').val(qtde)
	$('#bloco_tributos #valorUnidTribIPI').val(vlIPI/qtde);
};

function inicializarCalculoImpostos(){
	var campos = gerarJsonCalculoImpostos();
	for (var i = 0; i < campos.length; i++) {
		document.getElementById(campos[i].idVl).onkeyup = calcularAlteracaoAliquota;
		document.getElementById(campos[i].idAliq).onkeyup = calcularAlteracaoAliquota;
	}
	
	campos = gerarJsonIcmsInterestadual().campos;
	for (var i = 0; i < campos.length; i++) {
		$('#bloco_icms_interestadual #'+campos[i].id).keyup(function(){
			calcularValorICMSInterestadual();
		});
	}
};
</script>
</head>
<body>
	<%-- Os campos hidden abaixo foram criado para serem utilizados em algumas funcoes do emissaoNFe.js --%>
	<input type="hidden" id="percentualPis" value="${percentualPis}"/>
	<input type="hidden" id="percentualCofins" value="${percentualCofins}"/>
	<input type="hidden" id="valorFrete" value="${valorFrete}"/>
	
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
	<form id="formVazio" ></form>
	<%-- O valor do pedido sera utilizado para efetuar o recalculo do valor das duplicatas quando incluir o valor do frete --%>
	<input type="hidden" id="valorTotalProdutos"/>

	<form id="formPesquisa" action="<c:url value="/emissaoNFe/pedido"/>" method="get">
		<input type="hidden" id="idPedidoPesquisa" name="idPedido" value="${idPedido}"/>
	</form>
	
	<form id="formPesquisaNFe" action="<c:url value="/emissaoNFe/NFe"/>" method="get">
		<input type="hidden" id="numeroNFePesquisa" name="numeroNFe" value="${idPedido}"/>
	</form>
	
	<form id="formEmissao" action="<c:url value="/emissaoNFe/emitirNFe"/>" method="post">
		<input type="hidden" name="nf.identificacaoLocalEntrega.codigoMunicipio" value="${nf.identificacaoLocalEntrega.codigoMunicipio}"/>
		<input type="hidden" name="nf.identificacaoLocalRetirada.codigoMunicipio" value="${nf.identificacaoLocalRetirada.codigoMunicipio}"/>
		<input type="hidden" id="tipoNFe" name="tipoNFe"/>
		
		<fieldset id="bloco_dados_nfe">
			<legend>::: Dados da NF-e :::</legend>
			<div class="label">Pedido:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="idPedido" name="idPedido" value="${idPedido}" />
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoPesquisaPedido" title="Pesquisar Pedido" value="" class="botaoPesquisarPequeno" />
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoPedidoPDF" value="" title="Visualizar Pedido PDF" class="botaoPdf_16" />
			</div>
			<%--div para dar o correto alinhamento dos campos no formulario por causa do botao de PDF. Nao teve outra alternativa--%>
			<div class="input" style="width: 60%">
			</div>
			<div class="label">NFes:</div>
			<div class="input" style="width: 70%">
				<select id="listaNumeroNFe" style="width: 15%" >
					<c:forEach var="numero" items="${listaNumeroNFe}">
						<option value="${numero}" <c:if test="${numero eq numeroNFe}">selected</c:if>>${numero}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Núm. NFe:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="numeroNFe" name="nf.identificacaoNFe.numero" value="${numeroNFe}" maxlength="9" style="width: 100%"/>
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoPesquisaNFe" title="Pesquisar NFe" value="" class="botaoPesquisarPequeno" />
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoRemoverNFe" title="Remover NFe" value="" class="botaoRemover" />
			</div>
			<div class="label" style="width: 11%">Mod. NFe:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.identificacaoNFe.modelo" value="${modeloNFe}" maxlength="2" style="width: 100%" />
			</div>
			<div class="label" style="width: 10%">Série NFe:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.identificacaoNFe.serie" value="${serieNFe}" maxlength="3" style="width: 100%" />
			</div>
			<div class="label" style="width: 10%">Tipo Operação:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.tipoOperacao" style="width: 100%" >
					<c:forEach var="tipo" items="${listaTipoOperacao}">
						<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq tipoOperacaoSelecionada}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Dest. Operação:</div>
			<div class="input" style="width: 36%">
				<select name="nf.identificacaoNFe.destinoOperacao" style="width: 100%" >
					<c:forEach var="tipo" items="${listaTipoDestinoOperacao}">
						<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq tipoDestinoOperacaoSelecionada}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label" style="width: 10%">Dt. Ent./Saída:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="dataSaida" name="nf.identificacaoNFe.dataSaida" value="${dataSaida}" style="width: 100%"/>
			</div>
			<div class="label" style="width: 10%">Hr. Ent./Saída:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="horaSaida" name="nf.identificacaoNFe.horaSaida" value="${horaSaida}" style="width: 100%"/>
			</div>
			<div class="label">Forma Pagamento:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.indicadorFormaPagamento"  style="width: 100%">
					<c:forEach var="formaPagamento" items="${listaTipoFormaPagamento}">
						<option value="${formaPagamento.codigo}" <c:if test="${formaPagamento.codigo eq formaPagamentoSelecionada}">selected</c:if>>${formaPagamento.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Forma Emissão:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.tipoEmissao"  style="width: 100%">
					<c:forEach var="tipoEmissao" items="${listaTipoEmissao}">
						<option value="${tipoEmissao.codigo}" <c:if test="${tipoEmissao.codigo eq tipoEmissaoSelecionada}">selected</c:if>>${tipoEmissao.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label" style="width: 10%">Finald. Emiss.:</div>
			<div class="input" style="width: 30%">
				<select name="nf.identificacaoNFe.finalidadeEmissao" style="width: 33%" >
					<c:forEach var="finalidade" items="${listaTipoFinalidadeEmissao}">
						<option value="${finalidade.codigo}" <c:if test="${finalidade.codigo eq finalidadeEmissaoSelecionada}">selected</c:if>>${finalidade.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Presen. Comprador:</div>
			<div class="input" style="width: 80%">
				<select name="nf.identificacaoNFe.tipoPresencaComprador" style="width: 45%">
					<c:forEach var="tipo" items="${listaTipoPresencaComprador}">
						<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq tipoPresencaSelecionada}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Tipo Impressão:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.tipoImpressao" style="width: 100%">
					<c:forEach var="tipo" items="${listaTipoImpressao}">
						<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq tipoImpressaoSelecionada}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Oper. Consum. Final:</div>
			<div class="input" style="width: 50%">
				<select name="nf.identificacaoNFe.operacaoConsumidorFinal" style="width: 20%" >
					<c:forEach var="tipo" items="${listaTipoOperacaoConsumidorFinal}">
						<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq tipoOperacaoConsumidorSelecionada}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Natureza Operação:</div>
			<div class="input" style="width: 36%">
				<input type="text" name="nf.identificacaoNFe.naturezaOperacao" value="${nf.identificacaoNFe.naturezaOperacao}" style="width: 100%"/>
			</div>
			<div class="label" style="width: 10%">Frete Pedido:</div>
				<div class="input" style="width: 30%">
					<input type="text" id="valorFretePedido" value="${valorFretePedido}" style="width: 33%"/>
				</div>
			
			<div class="divFieldset">
			<fieldset id="bloco_destinatario" class="fieldsetInterno">
				<legend>::: Destinatário ::: -</legend>
				<div class="label">Razão Social/Nome:</div>
				<div class="input" style="width: 50%">
					<input type="text" id="nomeCliente" name="nf.identificacaoDestinatarioNFe.razaoSocial" value="${cliente.razaoSocial}"  maxlength="70" style="width: 100%"/>
					<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
				</div>
				<div class="label" style="width: 8%">Email:</div>
				<div class="input" style="width: 20%">
					<input type="text" id="email" name="nf.identificacaoDestinatarioNFe.email"
						value="${cliente.email}" class="apenasLowerCase uppercaseBloqueado lowerCase" />
				</div>
				
				<div class="label">CNPJ:</div>
				<div class="input" style="width: 15%">
					<input type="text" id="cnpj" name="nf.identificacaoDestinatarioNFe.cnpj"
						value="${cliente.cnpj}"  />
				</div>
				<div class="input" style="width: 2%">
						<input type="button" id="botaoPesquisarCnpjDest"
							title="Pesquisar CNPJ Destinatário" value="" class="botaoPesquisarPequeno" />
					</div>
				<div class="label" style="width: 10%">Insc. Estadual:</div>
				<div class="input" style="width: 50%">
					<input type="text" id="inscricaoEstadual" name="nf.identificacaoDestinatarioNFe.inscricaoEstadual"
						value="${cliente.inscricaoEstadual}" style="width: 44%; text-align: left;" />
				</div>
				<div class="label">SUFRAMA:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="inscricaoSUFRAMA" name="nf.identificacaoDestinatarioNFe.inscricaoSUFRAMA"
						value="${cliente.inscricaoSUFRAMA}"  style="width: 19%" maxlength="9"/>
				</div>
				<div class="label">CPF:</div>
				<div class="input" style="width: 15%">
					<input type="text" id="cpf" name="nf.identificacaoDestinatarioNFe.cpf"
						value="${cliente.cpf}"  />
				</div>
				<div class="label" style="width: 12%">Telefone:</div>
				<div class="input" style="width: 50%">
					<input type="text" id="telefone" name="telefoneDestinatario"
						value="${telefoneContatoPedido}" style="width: 44%"/>
				</div>
				<div class="divFieldset">
				<jsp:include page="/bloco/bloco_logradouro.jsp"></jsp:include>
				</div>
			</fieldset>
			</div>
		</fieldset>
		
		<fieldset id="bloco_referenciada">
			<legend>::: NF/NFe Referenciada ::: -</legend>
			<div class="label">Chave Acesso:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="chaveReferenciada" style="width: 45%"/>
			</div>
			<div class="label">Núm. Doc. Fiscal:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="numeroReferenciada" maxlength="9"/>
			</div>
			<div class="label">Série. Doc. Fiscal:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="serieReferenciada" maxlength="3"/>
			</div>
			<div class="label">Mod. Doc. Fiscal:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="modReferenciada" maxlength="2" style="width: 50%"/>
			</div>
			<div class="label">CNPJ Emit.:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="cnpjReferenciada" maxlength="14"/>
			</div>
			<div class="label">Emis. Ano/Mês (AAMM):</div>
			<div class="input" style="width: 10%">
				<input type="text" id="anoMesReferenciada" maxlength="4"/>
			</div>
			<div class="label">UF Emit.:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="ufReferenciada" maxlength="2"/>
			</div>
			<div class="bloco_botoes">
				<a id="botaoInserirReferenciada" title="Inserir Dados da NF referenciada" class="botaoAdicionar"></a>
			</div>
			<table id="tabela_referenciada" class="listrada" >
				<thead>
					<tr>
						<th>Chave</th>
						<th>Núm. Doc. Fisc.</th>
						<th>Série Doc. Fisc.</th>
						<th>Mod. Doc. Fisc.</th>
						<th>CNPJ Emit.</th>
						<th>Emis. Ano/Mês</th>
						<th>UF Emit.</th>
						<th>Ações</th>
					</tr>
				</thead>
						
				<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
				<tbody>
					<c:forEach var="ref" items="${nf.identificacaoNFe.listaNFeReferenciada}">
					<tr>
						<td>${ref.chaveAcessoReferenciada}</td>
						<td>${ref.identificacaoNFeReferenciada.numeroNF}</td>
						<td>${ref.identificacaoNFeReferenciada.serie}</td>
						<td>${ref.identificacaoNFeReferenciada.modelo}</td>
						<td>${ref.identificacaoNFeReferenciada.cnpjEmitente}</td>
						<td>${ref.identificacaoNFeReferenciada.anoMes}</td>
						<td>${ref.identificacaoNFeReferenciada.ufEmitente}</td>
						<td></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
		
		<fieldset id="bloco_local_mercadoria">
			<legend>::: Local Mercadoria ::: -</legend>
			<div class="divFieldset">
			<fieldset class="fieldsetInterno">
				<legend>::: Local Retirada :::</legend>
				<div class="label">CNPJ:</div>
				<div class="input" style="width: 15%">
					<input type="text" name="nf.identificacaoLocalRetirada.cnpj" maxlength="14" value="${nf.identificacaoLocalRetirada.cnpj}"/>
				</div>
				<div class="label">CPF:</div>
				<div class="input" style="width: 50%">
					<input type="text" name="nf.identificacaoLocalRetirada.cpf" maxlength="11" value="${nf.identificacaoLocalRetirada.cpf}" style="width: 30%"/>
				</div>
				<div class="label condicional">CEP:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="cepRetirada" name="nf.identificacaoLocalRetirada.cep" value="${nf.identificacaoLocalRetirada.cep}" maxlength="8" />
				</div>
				<div class="input" style="width: 70%">
					<input type="button" id="botaoCepRetirada"
						title="Pesquisar Endereço" value="" class="botaoPesquisarPequeno"
						style="width: 20px" />
				</div>
				<div class="label">Endereço:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="enderecoRetirada" name="nf.identificacaoLocalRetirada.logradouro" value="${nf.identificacaoLocalRetirada.logradouro}"/>
				</div>
				<div class="label" style="width: 8%">Número:</div>
				<div class="input" style="width: 30%">
					<input type="text" name="nf.identificacaoLocalRetirada.numero" value="${nf.identificacaoLocalRetirada.numero}" style="width: 20%"/>
				</div>
				<div class="label">Complemento:</div>
				<div class="input" style="width: 70%">
					<input type="text" name="nf.identificacaoLocalRetirada.complemento" value="${nf.identificacaoLocalRetirada.complemento}" style="width: 30%"/>
				</div>
				<div class="label">Cidade:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="cidadeRetirada" name="nf.identificacaoLocalRetirada.municipio" value="${nf.identificacaoLocalRetirada.municipio}" style="width: 40%" />
				</div>
				<div class="label">Bairro:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="bairroRetirada" name="nf.identificacaoLocalRetirada.bairro" value="${nf.identificacaoLocalRetirada.bairro}" style="width: 40%"/>
				</div>
				<div class="label">UF:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="ufRetirada" name="nf.identificacaoLocalRetirada.uf" value="${nf.identificacaoLocalRetirada.uf}" style="width: 5%"/>
				</div>
			</fieldset>
			</div>
			
			<div class="divFieldset">
			<fieldset class="fieldsetInterno" >
				<legend>::: Local Entrega ::: -</legend>
				<div class="label">CNPJ:</div>
				<div class="input" style="width: 15%">
					<input type="text" name="nf.identificacaoLocalEntrega.cnpj" maxlength="14" value="${nf.identificacaoLocalEntrega.cnpj}"/>
				</div>
				<div class="label">CPF:</div>
				<div class="input" style="width: 50%">
					<input type="text" name="nf.identificacaoLocalEntrega.cpf" maxlength="11" value="${nf.identificacaoLocalEntrega.cpf}" style="width: 30%"/>
				</div>
				<div class="label condicional">CEP:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="cepEntrega" name="nf.identificacaoLocalEntrega.cep" value="${nf.identificacaoLocalEntrega.cep}" maxlength="8" />
				</div>
				<div class="input" style="width: 70%">
					<input type="button" id="botaoCepEntrega"
						title="Pesquisar Endereço" value="" class="botaoPesquisarPequeno"
						style="width: 20px" />
				</div>
				<div class="label">Endereço:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="enderecoEntrega" name="nf.identificacaoLocalEntrega.logradouro" value="${nf.identificacaoLocalEntrega.logradouro}"/>
				</div>
				<div class="label" style="width: 8%">Número:</div>
				<div class="input" style="width: 30%">
					<input type="text" name="nf.identificacaoLocalEntrega.numero" value="${nf.identificacaoLocalEntrega.numero}" style="width: 20%"/>
				</div>
				<div class="label">Complemento:</div>
				<div class="input" style="width: 70%">
					<input type="text" name="nf.identificacaoLocalEntrega.complemento" value="${nf.identificacaoLocalEntrega.complemento}" style="width: 30%"/>
				</div>
				<div class="label">Cidade:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="cidadeEntrega" name="nf.identificacaoLocalEntrega.municipio" value="${nf.identificacaoLocalEntrega.municipio}" style="width: 40%" />
				</div>
				<div class="label">Bairro:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="bairroEntrega" name="nf.identificacaoLocalEntrega.bairro" value="${nf.identificacaoLocalEntrega.bairro}" style="width: 40%"/>
				</div>
				<div class="label">UF:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="ufEntrega" name="nf.identificacaoLocalEntrega.uf" value="${nf.identificacaoLocalEntrega.uf}" style="width: 5%"/>
				</div>
			</fieldset>
			</div>			
		</fieldset>
		
		<fieldset>
			<legend>::: Produtos e Serviços :::</legend>
			<table id="tabela_produtos" class="listrada">
				<thead>
					<%-- Muito cuidado ao alterar o numero de colunas dessa tabela pois varios eventos de campos e botoes estao indexados pela ordem das colunas das tabelas --%>
					<tr>
						<th>Item</th>
						<th>Cod.</th>
						<th>Desc.</th>
						<th>Venda</th>
						<th>Qtde.</th>
						<th>Unid.(R$)</th>
						<th>Tot.(R$)</th>
						<th>BC ICMS</th>
						<th>V. ICMS(R$)</th>
						<th>V IPI.(R$)</th>
						<th>Aliq. ICMS(%)</th>
						<th>Aliq. IPI(%)</th>
						<th>NCM</th>
						<th>CFOP</th>
						<th style="width: 2%">Ações</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="p" items="${listaProduto}" varStatus="count">
						<tr>
							<td>${p.numeroItem}</td>
							<td>${p.codigo}</td>
							<td>${p.descricao}</td>
							<td>${p.unidadeComercial}</td>
							<td>${p.quantidade}</td>
							<td>${p.valorUnitarioComercializacao}</td>
							<td>${p.valorTotalBruto}</td>
							<td>${p.valorTotalBruto}</td>
							<td>${p.valorICMS}</td>
							<td>${p.valorIPI}</td>
							<td>${p.aliquotaICMS}</td>
							<td>${p.aliquotaIPI}</td>
							<td>${p.ncm}</td>
							<td>${p.cfop}</td>
							<td>
								<input type="button" value="" title="Editar Produto" class="botaoEditar" onclick="editarProduto(this);"/>
								<input type="button" value="" title="Remover Produto" class="botaoRemover" onclick="removerProduto(this);"/>
							</td>
							
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<a id="produtosInfo"></a>
			<fieldset id="bloco_info_adicionais_prod" class="fieldsetInterno">
				<legend>::: Info. Adicionais Prod. ::: +</legend>
				<div class="label">Quantidade:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="quantidadeProduto" maxlength="20" style="width: 10%"/>
				</div>
				<div class="label">Código:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="codigoProduto" maxlength="20" style="width: 20%"/>
				</div>
				<div class="label">Descrição:</div>
				<div class="input" style="width: 70%">
					<input type="text" id="descricaoProduto" maxlength="120" style="width: 100%"/>
				</div>
				<div class="label">Num. Ped. Compra:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="numeroPedidoCompraProd" maxlength="15" style="width: 100%"/>
				</div>
				<div class="label">Item Ped. Compra.:</div>
				<div class="input" style="width: 50%">
					<input type="text" id="itemPedidoCompraProd"  maxlength="6" style="width: 20%"/>
				</div>
				<div class="label">Valor Desp. Acess.:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="despesasAcessoriasProd" />
				</div>
				<div class="label">Valor Frete:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="valorFreteProd" style="width: 100%"/>
				</div>
				<div class="label">Tot. Tributos:</div>
				<div class="input" style="width: 30%">
					<input type="text" id="valorTotaltributosProd" style="width: 30%"/>
				</div>
				<div class="label">Núm. FCI:</div>
				<div class="input" style="width: 70%">
					<input type="text" id="fciProd" maxlength="36" style="width: 50%"/>
				</div>
				<div class="label">Info. Produto:</div>
				<div class="input areatexto" style="width: 70%">
					<textarea id="infoAdicionaisProd" style="width: 100%"></textarea>
				</div>
				<div class="bloco_botoes">
						<input type="button" id="botaoInserirInfoProd" title="Inserir Informações do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparInfoProd" title="Limpar Informações do Produto" value="" class="botaoLimpar"/>
					</div>
			</fieldset>
			
			<fieldset id="bloco_tributos" class="fieldsetInterno">
				<legend class="fieldsetInterno">::: Tributos Prod.::: -</legend>
				<div class="divFieldset">
				<fieldset id="bloco_icms" class="fieldsetInterno">
					<legend title="Clique para exibir os campos ICMS">::: ICMS Prod.::: +</legend>
					<div class="label obrigatorio">NCM:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="ncm" name="ncm"  maxlength="8" style="width: 100%"/>
					</div>
					<div class="label obrigatorio">CEST:</div>
					<div class="input" style="width: 40%">
						<input type="text" id="cest" name="cest"  maxlength="7" style="width: 50%"/>
					</div>
					<div class="label obrigatorio">CFOP:</div>
					<div class="input" style="width: 80%">
						<select id="cfop" name="cfop" style="width: 80%">
							<c:forEach var="cfop" items="${listaCfop}" >
								<option value="${cfop[0]}">${cfop[1]}</option>
							</c:forEach>
						</select>
					</div>
					<div class="label obrigatorio">Situação Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="tipoTributacaoICMS" 
							style="width: 77%" >
							<c:forEach var="icms" items="${listaTipoTributacaoICMS}">
								<option value="${icms.codigo}">${icms.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="label obrigatorio">Origem:</div>
					<div class="input" style="width: 25%">
						<select id="origemMercadoriaICMS" 
							style="width: 100%" >
							<c:forEach var="origem" items="${listaTipoOrigemMercadoria}">
								<option value="${origem.codigo}">${origem.descricao}</option>
							</c:forEach>
						</select>
					</div>
					
					<div class="label  obrigatorio">Modalidade:</div>
					<div class="input" style="width: 30%">
						<select id="modBCICMS" style="width: 65%" class="icms00 semprehabilitado">
							<c:forEach var="modalidade" items="${listaTipoModalidadeDeterminacaoBCICMS}">
								<option value="${modalidade.codigo}">${modalidade.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label obrigatorio">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valorBCICMS" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Alíquota(%):</div>
					<div class="input" style="width: 10%">
						<input type="text" id="aliquotaICMS" style="width: 100%" />
					</div>
					<div  class="label">Valor Cal.:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="valorICMS" readonly="readonly" style="width: 50%" />
					</div>
					<div class="label">Modalidade ST:</div>
					<div class="input" style="width: 70%">
						<select id="modBCSTICMS" style="width: 30%" class="icms00 semprehabilitado">
							<option value=""></option>
							<c:forEach var="modalidade" items="${listaTipoModalidadeDeterminacaoBCICMSST}">
								<option value="${modalidade.codigo}">${modalidade.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Perc. Marg. Valor ST:</div>
					<div class="input" style="width: 10%">
						<input id="percValSTICMS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Perc. Redução BC ST:</div>
					<div class="input" style="width: 50%">
						<input id="percRedBCSTICMS" type="text" style="width: 25%" />
					</div>
					<div  class="label">Valor BC ST:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCSTICMS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Alíquota ST:</div>
					<div class="input" style="width: 50%">
						<input id="aliquotaSTICMS" type="text" style="width: 25%" />
					</div>
					<div  class="label">Val. Desoneração:</div>
					<div class="input" style="width: 10%">
						<input id="valDesonICMS" type="text" style="width: 100%" />
					</div>
					<div class="icms00 label">Mot. Desoneração:</div>
					<div class="icms00 input" style="width: 50%">
						<select id="motDesonerICMS" style="width: 50%" class="icms00 semprehabilitado">
							<option value=""></option>
							<c:forEach var="motivo" items="${listaTipoDesoneracao}">
								<option value="${motivo.codigo}">${motivo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirICMS" title="Inserir ICMS do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparICMS" title="Limpar ICMS do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_icms_interestadual" class="fieldsetInterno">
				<legend>::: ICMS Interestadual Prod.::: +</legend>
					<div  class="label obrigatorio">Perc. FCP:</div>
					<div class="input" style="width: 10%">
						<input id="percFCPDestICMSInter" type="text" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCICMSInter" type="text" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Alíq. Interna:</div>
					<div class="input" style="width: 20%">
						<input id="aliquotaUFDestICMSInter" type="text" style="width: 50%" />
					</div>
					<div  class="label obrigatorio">Alíq. Interest.:</div>
					<div class="input" style="width: 10%">
						<select id="aliquotaICMSInter" style="width: 100%">
							<option value=""></option>
							<c:forEach var="tipo" items="${listaTipoAliquotaICMSInterestadual}">
								<option value="${tipo.aliquota}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label obrigatorio">Perc. Partilha:</div>
					<div class="input" style="width: 50%">
						<select id="percProvPartilhaICMSInter" style="width: 20%">
							<option value=""></option>
							<c:forEach var="tipo" items="${listaTipoAliquotaICMSPartilha}">
								<option value="${tipo.aliquota}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Val. FCP.:</div>
					<div class="input" style="width: 10%">
						<input id="valorFCPICMSInter" type="text" readonly="readonly" style="width: 100%" />
					</div>
					<div  class="label">Val. ICMS Destino:</div>
					<div class="input" style="width: 10%">
						<input id="valorICMSInter" type="text" readonly="readonly" style="width: 100%" />
					</div>
					<div  class="label">Val. ICMS Remetente:</div>
					<div class="input" style="width: 10%">
						<input id="valorPartICMSInter" type="text" readonly="readonly" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirICMSInter" title="Inserir ICMS Interestadual do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparICMSInter" title="Limpar ICMS Interestadual do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_cofins" class="fieldsetInterno">
					<legend>::: COFINS Prod.::: +</legend>
					<div class="label obrigatorio">Situação Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="codSitTribCOFINS" style="width: 45%">
							<c:forEach var="tipo" items="${listaTipoTributacaoCOFINS}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label obrigatorio">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Alíquota(%):</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaCOFINS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Cal.:</div>
					<div class="input" style="width: 20%">
						<input id="valorCOFINS" type="text" readonly="readonly" style="width: 50%" />
					</div>
					<div  class="label">Qtde. Vendida:</div>
					<div class="input" style="width: 10%">
						<input id="qtdeVendidaCOFINS" type="text" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirCOFINS" title="Inserir COFINS do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparCOFINS" title="Limpar COFINS do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_pis" class="fieldsetInterno">
					<legend>::: PIS Prod.::: +</legend>
					<div class="label obrigatorio">Situação Tribut.:</div>
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
					<div  class="label obrigatorio">Alíquota(%):</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaPIS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Cal.:</div>
					<div class="input" style="width: 20%">
						<input id="valorPIS" type="text" readonly="readonly" style="width: 50%" />
					</div>
					<div  class="label">Qtde.Vendida:</div>
					<div class="input" style="width: 10%">
						<input id="qtdeVendidaPIS" type="text" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirPIS" title="Inserir PIS do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparPIS" title="Limpar PIS do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_ipi" class="fieldsetInterno">
					<legend>::: IPI Prod.::: +</legend>
					<div class="label obrigatorio">Situação Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="codSitTribIPI" style="width: 45%">
							<option value=""></option>
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
					<div  class="label">Valor Cal.:</div>
					<div class="input" style="width: 20%">
						<input id="valorIPI" type="text" readonly="readonly" style="width: 50%" />
					</div>
					<div  class="label">Qtde. unid. Tributável:</div>
					<div class="input" style="width: 10%">
						<input id="qtdeUnidTribIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Unid. Tributável:</div>
					<div class="input" style="width: 50%">
						<input id="valorUnidTribIPI" type="text" style="width: 20%" />
					</div>
					<div  class="label">Cl. Enquadramento:</div>
					<div class="input" style="width: 10%">
						<input id="clEnquadramentoIPI" type="text" style="width: 100%" maxlength="3"/>
					</div>
					<div  class="label">Cod. Enquadramento:</div>
					<div class="input" style="width: 50%">
						<input id="codEnquadramentoIPI" type="text" style="width: 20%" maxlength="3"/>
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
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_iss" class="fieldsetInterno">
					<legend>::: ISSQN Prod.::: +</legend>
					<%-- Acredito que esse campo tenha sido removido da versao 3.10 --%>
					<%--div class="label obribagorio">Situação Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="codSitTribISS" style="width: 45%">
							<option value=""></option>
							<c:forEach var="tipo" items="${listaTipoTributacaoISS}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div --%>
					<div  class="label obribagorio">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCISS" type="text" style="width: 100%" />
					</div>
					<div  class="label obribagorio">Alíquota(%):</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaISS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Cal.:</div>
					<div class="input" style="width: 20%">
						<input id="valorISS" type="text" readonly="readonly" style="width: 50%" />
					</div>
					<div  class="label obribagorio">Mun. Gerador:</div>
					<div class="input" style="width: 10%">
						<input id="codMunGeradorISS" type="text" maxlength="7" style="width: 100%" />
					</div>
					<div  class="label obribagorio">Item Serviço:</div>
					<div class="input" style="width: 10%">
						<input id="codItemServicoISS" type="text" maxlength="5" style="width: 100%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirISS" title="Inserir ISS do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparISS" title="Limpar ISS do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_ii" class="fieldsetInterno">
					<legend>::: Importação Prod.::: +</legend>
					<div  class="label obribagorio">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCII" type="text" style="width: 100%" />
					</div>
					<div  class="label obribagorio">Valor Import.(R$):</div>
					<div class="input" style="width: 50%">
						<input id="valorII" type="text" style="width: 20%" />
					</div>
					<div  class="label obribagorio">Valor IOF:</div>
					<div class="input" style="width: 10%">
						<input id="valorIOFII" type="text" style="width: 100%" />
					</div>
					<div  class="label obribagorio">Valor Desp. Aduan.:</div>
					<div class="input" style="width: 50%">
						<input id="valorDespAduaneirasII" type="text" style="width: 20%" />
					</div>
					<div class="bloco_botoes">
						<input type="button" id="botaoInserirII" title="Inserir Imp. Importação do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparII" title="Limpar Imp. Importação do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				</div>
			</fieldset>
			<fieldset id="bloco_importacao_prod" class="fieldsetInterno">
				<legend>::: Importação ::: +</legend>
				<div class="label">CNPJ:</div>
				<div class="input" style="width: 20%">
					<input type="text" id="cnpjImportProd" maxlength="14" style="width: 100%"/>
				</div>
				<div class="label obrigatorio">Cód. Export.:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="exportadorImportProd" maxlength="60" style="width: 50%"/>
				</div>
				<div class="label obrigatorio">Dt. Import.:</div>
				<div class="input" style="width: 20%">
					<input type="text" id="dtImportProd" style="width: 100%"/>
				</div>
				<div class="label obrigatorio">Dt. Desemb.:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="dataDesembImportProd" style="width: 50%"/>
				</div>
				<div class="label obrigatorio">Local:</div>
				<div class="input" style="width: 20%">
					<input type="text" id="lcImportProd" maxlength="60" style="width: 100%"/>
				</div>
				<div class="label obrigatorio">Número:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="numImportProd" maxlength="12" style="width: 50%"/>
				</div>
				<div class="label obrigatorio">Intermediação:</div>
				<div class="input" style="width: 20%">
					<select id="tpImportProd" style="width: 100%">
						<c:forEach var="tipo" items="${listaTipoIntermediacaoImportacao}">
							<option value="${tipo.codigo}">${tipo.descricao}</option>
						</c:forEach>
					</select>
				</div>
				<div class="label obrigatorio">Transporte:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="tpTranspImportProd" style="width: 50%"/>
				</div>
				<div class="label obrigatorio">UF Desemb.:</div>
				<div class="input" style="width: 20%">
					<input type="text" id="ufDesembImportProd" style="width: 100%"/>
				</div>
				<div class="label">UF Encomendante:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="ufEncomendImportProd" style="width: 50%"/>
				</div>
				<div class="label">Vl. AFRMM:</div>
				<div class="input" style="width: 20%">
					<input type="text" id="vlAFRMMImportProd" style="width: 100%"/>
				</div>
				
				<div class="bloco_botoes">
					<a id="botaoInserirImportacaoProd" title="Inserir Importação de Produto" class="botaoAdicionar"></a>
				</div>
				<table id="tabela_importacao_prod" class="listrada" >
					<thead>
						<tr>
							<th>CNPJ</th>
							<th>Cód. Export.</th>
							<th>Dt. Import.</th>
							<th>Dt. Desemb.</th>
							<th>Local</th>
							<th>Núm.</th>
							<th>Intermed.</th>
							<th>Transp.</th>
							<th>UF Desemb.</th>
							<th>UF Encomend.</th>
							<th>Vl. AFRMM</th>
							<th>Ações</th>
						</tr>
					</thead>
					
					<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
					<tbody>
					</tbody>
				</table>
				<fieldset id="bloco_adicao_import" class="fieldsetInterno">
					<legend>::: Adição de Importação :::</legend>
					<div class="label obrigatorio">Cód. Fabric.:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="codFabricAdicao" style="width: 100%"/>
					</div>
					<div class="label obrigatorio">Núm.:</div>
					<div class="input" style="width: 40%">
						<input type="text" id="numAdicao" style="width: 50%"/>
					</div>
					<div class="label">Núm. Drawback:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="numDrawbackAdicao" style="width: 100%"/>
					</div>
					<div class="label obrigatorio">Núm. Sequenc.:</div>
					<div class="input" style="width: 40%">
						<input type="text" id="numSeqAdicao" style="width: 50%"/>
					</div>
					<div class="label">Vl. Desc.:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="valDescAdicao" style="width: 100%"/>
					</div>
					
					<div class="bloco_botoes">
						<a id="botaoInserirAdicao" title="Inserir Adição de Importação" class="botaoAdicionar"></a>
					</div>
									
					<table id="tabela_adicao_import" class="listrada" >
						<thead>
							<tr>
								<th>Fabric.</th>
								<th>Núm.</th>
								<th>Núm. Drawback</th>
								<th>Núm. Sequenc.</th>
								<th>Vl. Desc.</th>
								<th>Ações</th>
							</tr>
						</thead>
						
						<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
						<tbody>
						</tbody>
					</table>
				</fieldset>
			</fieldset>
			<fieldset id="bloco_exportacao_prod" class="fieldsetInterno">
					<legend>::: Exportação ::: +</legend>
					<div class="label">Drawback:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="drawbackExportProd" maxlength="11" style="width: 100%"/>
					</div>
					<div class="label obrigatorio">Ch Acesso:</div>
					<div class="input" style="width: 40%">
						<input type="text" id="chAcessoExportIndir" maxlength="44" style="width: 100%"/>
					</div>
					<div class="label obrigatorio">Registro:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="registroExportIndir" maxlength="12" style="width: 100%"/>
					</div>
					<div class="label obrigatorio">Quantidade:</div>
					<div class="input" style="width: 40%">
						<input type="text" id="qtdeExportIndir" style="width: 50%"/>
					</div>
					
					<div class="bloco_botoes">
						<a id="botaoInserirExportacaoProd" title="Inserir Exportação do Produto" class="botaoAdicionar"></a>
					</div>
									
					<table id="tabela_exportacao_prod" class="listrada">
						<thead>
							<tr>
								<th>Drawback</th>
								<th>Ch Acesso</th>
								<th>Registro</th>
								<th>Qutde.</th>
								<th>Ações</th>
							</tr>
						</thead>
						
						<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
						<tbody>
						</tbody>
					</table>
				</fieldset>
				<div class="bloco_botoes">
					<a id="botaoProximoProduto" title="Próximo Produto" class="botaoProximo"></a>
					<a id="botaoAnteriorProduto" title="Produto Anterior" class="botaoAnterior"></a>
				</div>
		</fieldset>	
		
		<fieldset id="bloco_transporte">
			<legend>::: Transporte ::: -</legend>
			<div class="label obrigatorio">Modal. Frete:</div>
			<div class="input" style="width: 80%">
			<select id="modFrete" name="nf.transporteNFe.modalidadeFrete" style="width: 45%">
				<c:forEach var="tipo" items="${listaTipoModalidadeFrete}">
					<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq modalidadeFreteSelecionada}">selected</c:if>>${tipo.descricao}</option>
				</c:forEach>
			</select>
			</div>
			
			<div class="divFieldset">
			<fieldset class="fieldsetInterno">
					<legend>::: Transportadora :::</legend>
					<div  class="label">Razão Soc./Nome:</div>
					<div class="input" style="width: 80%">
						<input type="text" id="nomeTransportadora" name="nf.transporteNFe.transportadoraNFe.razaoSocial" value="${transportadora.razaoSocial}" style="width: 45%" />
						<div class="suggestionsBox" id="containerPesquisaTransportadora" style="display: none; width: 50%"></div>
					</div>
					<div  class="label">CNPJ:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="cnpjTransportadora" name="nf.transporteNFe.transportadoraNFe.cnpj" value="${transportadora.cnpj}" style="width: 100%" />
					</div>
					<div class="input" style="width: 2%">
						<input type="button" id="botaoPesquisarCnpjTransp"
							title="Pesquisar CNPJ Transportadora" value="" class="botaoPesquisarPequeno" />
					</div>
					<div  class="label">CPF:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.transportadoraNFe.cpf" style="width: 100%" />
					</div>
					<div  class="label">Insc. Estadual:</div>
					<div class="input" style="width: 30%">
						<input type="text" id="ieTransportadora" name="nf.transporteNFe.transportadoraNFe.inscricaoEstadual" value="${transportadora.inscricaoEstadual}"  style="width: 50%" />
					</div>
					<div  class="label">Endereço:</div>
					<div class="input" style="width: 80%">
						<input type="text" id="endTransportadora" name="nf.transporteNFe.transportadoraNFe.enderecoCompleto" value="${transportadora.endereco}" style="width: 84%" />
					</div>
					<div  class="label">Município:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="munTransportadora" name="nf.transporteNFe.transportadoraNFe.municipio" value="${transportadora.cidade}" style="width: 100%" />
					</div>
					<div  class="label">UF:</div>
					<div class="input" style="width: 50%">
						<select id="ufTransportadora" name="nf.transporteNFe.transportadoraNFe.uf" style="width: 20%">
							<option value=""></option>
							<c:forEach var="tipo" items="${listaTipoUF}">
								<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq transportadora.uf}">selected</c:if>>${tipo.codigo}</option>
							</c:forEach>
						</select>
					</div>
				</fieldset>
				</div>
			
				<div class="divFieldset">
				<fieldset id="bloco_volume" class="fieldsetInterno">
					<legend>::: Volumes :::</legend>
					<div class="label">Qtde.:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="quantidadeVolume" value="${quantidade}" maxlength="15"/>
					</div>
					
					<div class="label">Espécie:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="especieVolume" value="${especieVolume}" maxlength="60"/>
					</div>
					<div class="label">Marca:</div>
					<div class="input" style="width: 30%">
						<input type="text" id="marcaVolume" maxlength="60" style="width: 50%"/>
					</div>
					<div class="label">Numeração:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="numeracaoVolume" maxlength="60"/>
					</div>
					<div class="label">Peso Líq.(kg):</div>
					<div class="input" style="width: 10%">
						<input type="text" id="pesoLiquidoVolume" value="${pesoLiquido}"/>
					</div>
					<div class="label">Peso Bruto(kg):</div>
					<div class="input" style="width: 30%">
						<input type="text" id="pesoBrutoVolume" value="${pesoBruto}" style="width: 50%"/>
					</div>
					<div class="bloco_botoes">
						<a id="botaoInserirVolume" title="Inserir Dados da Volume" class="botaoAdicionar"></a>
					</div>
								
					<table id="tabela_volume" class="listrada" >
						<thead>
							<tr>
								<th>Qtde.</th>
								<th>Espécie</th>
								<th>Marca</th>
								<th>Númeração</th>
								<th>Peso Líq.(kg)</th>
								<th>Peso Bruto(kg)</th>
								<th>Ações</th>
							</tr>
						</thead>
						<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
						<tbody>
						<c:forEach var="vol" items="${nf.transporteNFe.listaVolume}">
							<tr>
								<td>${vol.quantidade}</td>
								<td>${vol.especie}</td>
								<td>${vol.marca}</td>
								<td>${vol.numeracao}</td>
								<td>${vol.pesoLiquido}</td>
								<td>${vol.pesoBruto}</td>
								<td></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</fieldset>
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_veiculo" class="fieldsetInterno fieldsetFechado">
					<legend>::: Veículo/Reboque/Balsa/Vagão ::: -</legend>
					<div  class="label obrigatorio">Placa:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="placaVeiculo" name="nf.transporteNFe.veiculo.placa"  value="${nf.transporteNFe.veiculo.placa}" maxlength="7" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">UF:</div>
					<div class="input" style="width: 50%">
						<select id="ufVeiculo" name="nf.transporteNFe.veiculo.uf" style="width: 20%">
							<option value=""></option>
							<c:forEach var="tipo" items="${listaTipoUF}">
								<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq nf.transporteNFe.veiculo.uf}">selected</c:if>>${tipo.codigo}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label">Regist. Trans. Cargo:</div>
					<div class="input" style="width: 30%">
						<input type="text" id="registroVeiculo" name="nf.transporteNFe.veiculo.registroNacionalTransportador" 
							value="${nf.transporteNFe.veiculo.registroNacionalTransportador}" maxlength="2" style="width: 50%" />
					</div>
					<div class="bloco_botoes">
						<a id="botaoInserirReboque" title="Inserir Dados do Reboque" class="botaoAdicionar"></a>
					</div>
								
					<table id="tabela_reboque" class="listrada" >
						<thead>
							<tr>
								<th>Placa Reboq.</th>
								<th>UF Reboq.</th>
								<th>Registro Reboq.</th>
								<th>Ações</th>
							</tr>
						</thead>
						
						<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
						<tbody>
							<c:forEach var="reboque" items="${nf.transporteNFe.listaReboque}">
							<tr>
								<td>${reboque.placa}</td>
								<td>${reboque.uf}</td>
								<td>${reboque.registroNacionalTransportador}</td>
								<td></td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_retencao_icms" class="fieldsetInterno fieldsetFechado">
					<legend>::: Retenção ICMS ::: -</legend>
					<div  class="label obrigatorio">Valor Serviço:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valServRetICMSTransp" name="nf.transporteNFe.retencaoICMS.valorServico" value="${nf.transporteNFe.retencaoICMS.valorServico}" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valBCRetICMSTransp" name="nf.transporteNFe.retencaoICMS.valorBC" value="${nf.transporteNFe.retencaoICMS.valorBC}" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Alíquota(%):</div>
					<div class="input" style="width: 30%">
						<input type="text" id="aliqRetICMSTransp" name="nf.transporteNFe.retencaoICMS.aliquota" value="${nf.transporteNFe.retencaoICMS.aliquota}" style="width: 20%" />
					</div>
					<div  class="label obrigatorio">CFOP:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.cfop" value="${nf.transporteNFe.retencaoICMS.cfop}" maxlength="4" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Município Gerador:</div>
					<div class="input" style="width: 50%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.codigoMunicipioGerador" value="${nf.transporteNFe.retencaoICMS.codigoMunicipioGerador}" maxlength="7" style="width: 20%" />
					</div>
				</fieldset>
				</div>
		</fieldset>
		
		<fieldset>
			<legend>::: Cobrança :::</legend>
			<div class="label">Número:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.numero" value="${nf.cobrancaNFe.faturaNFe.numero}"/>
			</div>
			
			<div class="label">Valor Original:</div>
			<div class="input" style="width: 55%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorOriginal" value="${nf.cobrancaNFe.faturaNFe.valorOriginal}" style="width: 20%"/>
			</div>
			<div class="label">Valor Desconto:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorDesconto" value="${nf.cobrancaNFe.faturaNFe.valorDesconto}"/>
			</div>
			<div class="label">Valor Líquido:</div>
			<div class="input" style="width: 55%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorLiquido" value="${nf.cobrancaNFe.faturaNFe.valorLiquido}" style="width: 20%"/>
			</div>
			
			<div class="divFieldset">
			<fieldset id="bloco_duplicata" class="fieldsetInterno">
				<legend>::: Duplicata :::</legend>
				<div class="label">Número:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="numeroDuplicata" maxlength="60"/>
				</div>
				
				<div class="label">Dt. Vencimento:</div>
				<div class="input" style="width: 10%">
					<input id="dataVencimentoDuplicata" type="text" />
				</div>
				<div class="label obrigatorio">Valor:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="valorDuplicata"/>
				</div>
				<div class="bloco_botoes">
					<input type="button" id="botaoInserirDuplicata" title="Inserir Dados da Duplicata" class="botaoAdicionar"/>
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
						<c:forEach var="dup" items="${listaDuplicata}">
							<tr>
								<td>${dup.numero}</td>
								<td>${dup.dataVencimento}</td>
								<td>${dup.valor}</td>
								<td></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
			</div>
		</fieldset>
		<fieldset id="bloco_info_adicionais_nfe">
			<legend>::: Info. Adicionais ::: -</legend>
			<div class="label">Info. Adicionais Fisco:</div>
			<div class="input areatexto" style="width: 70%">
				<textarea name="nf.informacoesAdicionaisNFe.informacoesAdicionaisInteresseFisco" style="width: 100%">${infoAdFisco}</textarea>
			</div>
			<div class="label">Info. Adicionais Contrib.:</div>
			<div class="input areatexto" style="width: 70%">
				<textarea name="nf.informacoesAdicionaisNFe.informacoesComplementaresInteresseContribuinte" style="width: 100%">${nf.informacoesAdicionaisNFe.informacoesComplementaresInteresseContribuinte}</textarea>
			</div>
		</fieldset>
		<fieldset id="bloco_exportacao">
			<legend>::: Exportação/Compra ::: -</legend>
			<div class="label">UF Saída:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.exportacaoNFe.ufSaida" value="${nf.exportacaoNFe.ufSaida}" maxlength="2" style="width: 5%"/>
			</div>
			<div class="label">Local Export.:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.exportacaoNFe.localExportacao" value="${nf.exportacaoNFe.localExportacao}" maxlength="60" style="width: 60%"/>
			</div>
			<div class="label">Local Despach.:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.exportacaoNFe.localDespacho" value="${nf.exportacaoNFe.localDespacho}" maxlength="60" style="width: 60%"/>
			</div>		
			<div class="label">Nota Empenho:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.compraNFe.notaEmpenho" value="${nf.compraNFe.notaEmpenho}" maxlength="17" style="width: 20%"/>
			</div>
			<div class="label">Pedido:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.compraNFe.pedido" value="${nf.compraNFe.pedido}" maxlength="60" style="width: 60%"/>
			</div>
			<div class="label">Contrato:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.compraNFe.contrato" value="${nf.compraNFe.contrato}" maxlength="60" style="width: 60%"/>
			</div>		
		</fieldset>
		<div class="bloco_botoes">
			<input type="button" id="botaoEmitirNF" title="Emitir NFe Saída" value="" class="botaoEnviarEmail"/>
			<input type="button" id="botaoTriangularNF" title="Triangularizar NFe" value="" class="botaoTriangulo"/>
			<input type="button" id="botaoDevolverNF" title="Emitir NFe Devolução" value="" class="botaoRefazer"/>
		</div>

		<%-- Esse bloco contem todos os input hidden utilizados para manter o estado dos impostos dos itens que foram editados --%>
		<jsp:include page="/bloco/bloco_detalhe_items_nfe.jsp"></jsp:include>		
	</form>

</body>
</html>
