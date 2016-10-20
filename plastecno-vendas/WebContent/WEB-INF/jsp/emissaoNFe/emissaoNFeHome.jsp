<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${tempoInicial}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${tempoInicial}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js?${tempoInicial}"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${tempoInicial}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/edicao_tabela.js?${tempoInicial}"/>"></script>



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
$(document).ready(function() {
	scrollTo('${ancora}');
	
	$("#botaoPesquisaPedido").click(function() {
		if(isEmpty($('#idPedido').val())){
			return;
		}
		$('#formPesquisa #idPedidoPesquisa').val($('#idPedido').val());
		$('#formPesquisa').submit();
	});

	$('#bloco_logradouro').addClass('fieldsetInterno');

	$('#botaoEmitirNF').click(function(){
		gerarInputDuplicata();
		gerarInputProdutoServico();
		gerarInputVolume();
		gerarInputReboque();
		gerarInputReferenciada();
		
		$("#formEmissao input, #formEmissao textarea, #formEmissao select" ).each(function(i) {
			<%-- A remocao dos campos em branco eh necessaria para que o vraptor nao popule os beans com seus atributos todos nulos --%>
			if(isEmpty($(this).val())){
				$(this).remove();
			}
		});
		$('#formEmissao').submit();
	});
	
	$('#botaoInserirInfoProd').click(function(){
		gerarInputInfoProduto();
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
	});
	
	$('#botaoLimparICMSInter').click(function(){
		removerInputHidden(gerarJsonIcmsInterestadual());
		fecharBloco('bloco_icms_interestadual');
	});
	
	$('#botaoLimparInfoProd').click(function(){
		removerInputHidden(gerarJsonInfoProduto());
		fecharBloco('bloco_info_adicionais_prod');
		$('#bloco_info_adicionais_prod #fciProd').val('');
		$('#bloco_info_adicionais_prod #infoAdicionaisProd').val('');
		$('#bloco_info_adicionais_prod input:text').val('');
	});
	
	$('#botaoLimparIPI').click(function(){
		removerInputHidden(gerarJsonTipoIpi());
		removerInputHidden(gerarJsonEnquadramentoIpi());
		fecharBloco('bloco_ipi');
	});
	
	$('#botaoLimparPIS').click(function(){
		removerInputHidden(gerarJsonTipoPis());
		fecharBloco('bloco_pis');
	});
	
	$('#botaoLimparCOFINS').click(function(){
		removerInputHidden(gerarJsonTipoCofins());
		fecharBloco('bloco_cofins');
	});
	
	$('#botaoLimparISS').click(function(){
		removerInputHidden(gerarJsonISS());
		fecharBloco('bloco_iss');
	});
	
	$('#botaoLimparII').click(function(){
		removerInputHidden(gerarJsonImpostoImportacao());
		fecharBloco('bloco_ii');
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
							url: '<c:url value="/cliente/cnpj"/>',
							data: 'cnpj='+cnpj,
						});
		request.done(function(response) {
			var cliente = response.cliente;
			if(cliente == undefined){
				return;
			}
			$('#nomeCliente').val(cliente.razaoSocial);
			$('#inscricaoEstadual').val(cliente.inscricaoEstadual);
			$('#telefone').val(cliente.telefone);
			$('#email').val(cliente.email);
		});
		
		request.fail(function(request, status) {
			alert('Falha na busca da destinat�rio por CNPJ: ' + cnpj+' => Status da requisicao: '+status);
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
	
	inicializarBotaoPesquisarCEP({'idBotao':'botaoCepRetirada',
		'idCep': 'cepRetirada', 'idEndereco': 'enderecoRetirada', 'idBairro': 'bairroRetirada', 
		'idCidade': 'cidadeRetirada', 'idUf': 'ufRetirada', 'idPais': ''});
	
	inicializarBotaoPesquisarCEP({'idBotao':'botaoCepEntrega',
		'idCep': 'cepEntrega', 'idEndereco': 'enderecoEntrega', 'idBairro': 'bairroEntrega', 
		'idCidade': 'cidadeEntrega', 'idUf': 'ufEntrega', 'idPais': ''});
	
	inserirMascaraData('dataVencimentoDuplicata');
	inserirMascaraData('dataHoraEntradaSaida');
	inserirMascaraData('dtImportProd');
	inserirMascaraData('dataDesembImportProd');

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
	inicializarFadeInBloco('bloco_transporte');
	inicializarFadeInBloco('bloco_exportacao');
	
	fecharBloco('bloco_local_mercadoria');
	fecharBloco('bloco_referenciada');
	fecharBloco('bloco_destinatario');
	fecharBloco('bloco_transporte');
	fecharBloco('bloco_exportacao');
	
	<%-- Aqui fazemos com que os blocos de tributos nao sejam visualizados de inicio na tela, mas apenas quando editar o item da nota --%>
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
});
function inicializarMascaraReferenciada(){
	$('#chaveReferenciada').mask('9999.9999.9999.9999.9999.9999.9999.9999.9999.9999.9999');
};

function inicializarMascaraImpostos(){
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
				
				var celulas = linha.cells;
				var json = {'nomeObjeto': 'nf.listaItem['+numeroProdutoEdicao+'].listaImportacao['+linha.id+']', 
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
				$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].listaImportacao["+linha.id+"]']").each(function(i){
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
				return obrigatorioPreenchido(campos, ['numDrawbackAdicao', 'valDescAdicao']);
			},
			'onInserir': function(linha){
				if(numeroProdutoEdicao == null || numeroImportacaoProduto == null || linha == null){
					return;
				}
				
				var celulas = linha.cells;
				var json = {'nomeObjeto': 'nf.listaItem['+numeroProdutoEdicao+'].listaImportacao['+numeroImportacaoProduto+'].listaAdicao['+linha.id+']', 
					  'campos':[{'nome':'codigoFabricante', 'valor':celulas[0].innerHTML},
					            {'nome':'numero', 'valor':celulas[1].innerHTML},
					            {'nome':'numeroDrawback', 'valor':celulas[2].innerHTML},
					            {'nome':'numeroSequencialItem', 'valor':celulas[3].innerHTML},
					            {'nome':'valorDesconto', 'valor':celulas[4].innerHTML}
					            ]};
					
				gerarInputHidden(json);
			 },
			 'onRemover': function(linha){
					$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].listaImportacao["+numeroImportacaoProduto+"].listaAdicao["+linha.id+"]']").each(function(i){
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
				var json = {'nomeObjeto': 'nf.listaItem['+numeroProdutoEdicao+'].listaExportacao['+linha.id+']', 
					  'campos':[{'nome':'numeroDrawback', 'valor':celulas[0].innerHTML},
					            {'nome':'expIndireta.chaveAcessoRecebida', 'valor':celulas[1].innerHTML},
					            {'nome':'expIndireta.numeroRegistro', 'valor':celulas[2].innerHTML},
					            {'nome':'expIndireta.quantidadeItem', 'valor':celulas[3].innerHTML},
								]};
					
				gerarInputHidden(json);
			 },
			'onRemover': function(linha){
				$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].listaExportacao["+linha.id+"]']").each(function(i){
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
		<%-- devemos excluir a ultima celula da tabela pois eh a celula de botoes de acoes --%>
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

function abrirBloco(nomeBloco){
	gerarLegendaBloco(nomeBloco);
	<%-- Aqui estamos evitando que o div de autocomplete de cliente seja exibido pelo fadeIn --%>
	$('#'+nomeBloco+' div:not(.suggestionsBox), '+'#'+nomeBloco+' table, #'+nomeBloco+' .fieldsetInterno').fadeIn('fast');
};

function fecharBloco(nomeBloco){
	gerarLegendaBloco(nomeBloco);
	<%-- Aqui estamos evitando que o div de autocomplete de cliente seja exibido pelo fadeIn --%>
	$('#'+nomeBloco+' div:not(.suggestionsBox), '+'#'+nomeBloco+' table, #'+nomeBloco+' .fieldsetInterno').fadeOut('fast');
};

function inicializarLegendaBlocoProduto(nomeBloco){
	var legend = $('#'+nomeBloco+' legend:first');
	$(legend).html(legend.html().replace(/Prod.\s*\d*/g, 'Prod. '+(numeroProdutoEdicao+1)+' '));	

	legend.html(legend.html().replace(/\+/g, '-'));
	$('#'+nomeBloco+' div:not(.suggestionsBox)').fadeIn('fast');
};

function inicializarFadeInBloco(nomeBloco){
	$('#'+nomeBloco+' legend:first').click(function(){
		var innerHTML = $(this).html();
		if(innerHTML.indexOf('+') != -1){
			abrirBloco(nomeBloco);
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
		<%-- Devemos verificar  se o input ja foi criado pelo usuario, caso nao existe devemos cria-lo--%>
		if((input = document.getElementById(nome)) == undefined || input == null){
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
	return {'nomeObjeto':'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.icmsInterestadual',
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
		'campos':[{'nome':'classeEnquadramento', 'id':'clEnquadramentoIPI'},
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
		          {'nome':'codigoSituacaoTributaria', 'id':'codSitTribISS'},
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
		          {'nome': 'produtoServicoNFe.outrasDespesasAcessorias', 'id':'despesasAcessoriasProd'},
		          {'nome': 'produtoServicoNFe.valorTotalFrete', 'id':'valorFreteProd'},
		          {'nome': 'numeroPedidoCompra', 'id':'numeroPedidoCompraProd'},
		          {'nome': 'itemPedidoCompra', 'id':'itemPedidoCompraProd'},
		          {'nome': 'fichaConteudoImportacao', 'id':'fciProd'}]};
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
	
	$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].listaImportacao']").each(function(){
		nome = $(this).attr('name');
		if(nome.split('.').length == 4){
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
	$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].listaImportacao["+numeroImportacaoProduto+"].listaAdicao']").each(function(){
		nome = $(this).attr('name');
		if(nome.split('.').length == 5){
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
	$("input[name^='nf.listaItem["+numeroProdutoEdicao+"].listaExportacao']").each(function(){
		nome = $(this).attr('name');
		indice = nome.split('.').length;
		
		if(indice == 4){
			nome = nome.substring(nome.lastIndexOf('.')+1);
			if(nome == 'numeroDrawback'){
				$('#bloco_exportacao_prod #drawbackExportProd').val($(this).val());
				total--;
			} 
		}else if(indice == 5){
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
	for (var i = 0; i < linhas.length; i++) {
		celulas = linhas[i].cells;
		
		detalhamento = {'nomeObjeto':'nf.listaItem['+i+']',
			'campos':[{'nome':'numeroItem', 'valor': celulas[0].innerHTML}]};
		
		produto = {'nomeObjeto':'nf.listaItem['+i+'].produtoServicoNFe',
				'campos':[{'nome':'codigo', 'valor': celulas[1].innerHTML},
				          {'nome':'descricao', 'valor': celulas[1].innerHTML},
				          {'nome':'unidadeComercial', 'valor': celulas[4].innerHTML},
				          {'nome':'quantidadeComercial', 'valor': celulas[5].innerHTML},
				          {'nome':'quantidadeTributavel', 'valor': celulas[5].innerHTML},
				          {'nome':'valorUnitarioComercializacao', 'valor': celulas[6].innerHTML},
				          {'nome':'valorTotalBruto', 'valor': celulas[7].innerHTML},
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
		mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja DESATIVAR esse ramo de atividade?',
		confirmar: function(){
			$(botao).closest('form').submit();	
		}
	});
};

function recuperarValoresImpostos(valoresTabela){
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
			gerarListaMensagemAlerta(new Array('O CEP precisa ser preenchido para a pesquisa de endere�o'));
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

function inicializarOpcoesSelect(json){
	var campos = json.campos;
	var id = null;
	var sel = null;
	for (var i = 0; i < campos.length; i++) {
		if(document.getElementById(campos[i].idHidden) != undefined){
			continue;
		}
		sel = document.getElementById(campos[i].idSelect);
		sel.value = campos[i].valor;
		sel.selected='selected';
	}
};

function editarProduto(linha){
	var celulas = linha.cells;
	<%-- Estamos supondo que a sequencia do item do pedido eh unica --%>
	numeroProdutoEdicao = celulas[0].innerHTML;
	
	<%-- Aqui estamos diminuindo o valor da numero do item pois a indexacao das listas comecam do  zero --%>
	--numeroProdutoEdicao;
	var valorBC = celulas[6].innerHTML;
	
	var valoresTabela = {'campos':[{'id': 'itemPedidoCompraProd', 'valorTabela':celulas[0].innerHTML},
								   {'id': 'ncm', 'valorTabela': celulas[2].innerHTML},
	                               {'id': 'aliquotaICMS', 'valorTabela': celulas[10].innerHTML},
	                               {'id': 'aliquotaIPI', 'valorTabela': celulas[11].innerHTML},
	                               {'id': 'valorBCPIS', 'valorTabela': valorBC},
	                               {'id': 'valorBCCOFINS', 'valorTabela': valorBC},
	                               {'id': 'valorBCICMS', 'valorTabela': valorBC},
	                               {'id': 'valorBCIPI', 'valorTabela': valorBC},
	                               {'id': 'aliquotaPIS', 'valorTabela': '<c:out value="${percentualPis}"/>'},
	                               {'id': 'aliquotaCOFINS', 'valorTabela': '<c:out value="${percentualCofins}"/>'}
	                               ]};
	
	recuperarValoresImpostos(valoresTabela);
	
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
	fecharBloco('bloco_ii');
	fecharBloco('bloco_iss');
	fecharBloco('bloco_importacao_prod');
	fecharBloco('bloco_exportacao_prod');
	fecharBloco('bloco_adicao_import');
	
	var opcoes = {'campos':
		[{'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].produtoServicoNFe.cfop', 'idSelect': 'cfop', 'valor': '5102'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms.codigoSituacaoTributaria', 'idSelect': 'tipoTributacaoICMS', 'valor': '00'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms.origemMercadoria', 'idSelect': 'origemMercadoriaICMS', 'valor': '0'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.icms.tipoIcms.modalidadeDeterminacaoBC', 'idSelect': 'modBCICMS', 'valor': '0'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.cofins.tipoCofins.codigoSituacaoTributaria', 'idSelect': 'codSitTribCOFINS', 'valor': '01'},
		 {'idHidden': 'nf.listaItem['+numeroProdutoEdicao+'].tributos.pis.tipoPis.codigoSituacaoTributaria', 'idSelect': 'codSitTribPIS', 'valor': '01'}]};
	
	inicializarOpcoesSelect(opcoes);
	
	calcularValoresImpostos();
};

function gerarJsonCalculoImpostos(){
	return [{'idVl':'valorBCICMS', 'idAliq':'aliquotaICMS', 'idImp':'valorICMS'},
			{'idVl':'valorBCCOFINS', 'idAliq':'aliquotaCOFINS', 'idImp':'valorCOFINS'},
	    	{'idVl':'valorBCPIS', 'idAliq':'aliquotaPIS', 'idImp':'valorPIS'},
	    	{'idVl':'valorBCIPI', 'idAliq':'aliquotaIPI', 'idImp':'valorIPI'},
	    	{'idVl':'valorBCISS', 'idAliq':'aliquotaISS', 'idImp':'valorISS'}];	
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

function calcularValoresImpostos(){
	var campos = gerarJsonCalculoImpostos();
	var vl=null; 
	var aliq=null; 
	var idImp=null;
	for (var i = 0; i < campos.length; i++) {
		vl = document.getElementById(campos[i].idVl).value;
		aliq = document.getElementById(campos[i].idAliq).value;
		if(isEmpty(vl) || isEmpty(aliq)){
			continue;
		}
		document.getElementById(campos[i].idImp).value = Math.round(vl*(aliq/100) * 100)/100;
	}
};

function inicializarCalculoImpostos(){
	var campos = gerarJsonCalculoImpostos();
	for (var i = 0; i < campos.length; i++) {
		document.getElementById(campos[i].idVl).onblur = calcularValoresImpostos;
		document.getElementById(campos[i].idAliq).onblur = calcularValoresImpostos;
	}
	
	campos = gerarJsonIcmsInterestadual().campos;
	for (var i = 0; i < campos.length; i++) {
		$('#bloco_icms_interestadual #'+campos[i].id).blur(function(){
			calcularValorICMSInterestadual();
		});
	}
};
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
	<form id="formVazio" ></form>

	<form id="formPesquisa" action="<c:url value="/emissaoNFe/pedido"/>" method="get">
		<input type="hidden" id="idPedidoPesquisa" name="idPedido" value="${idPedido}"/>
	</form>
	<form id="formEmissao" action="<c:url value="/emissaoNFe/emitirNFe"/>" method="post">
		<input type="hidden" name="nf.identificacaoLocalEntrega.codigoMunicipio" value="${nf.identificacaoLocalEntrega.codigoMunicipio}"/>
		<input type="hidden" name="nf.identificacaoLocalRetirada.codigoMunicipio" value="${nf.identificacaoLocalRetirada.codigoMunicipio}"/>
		
		<fieldset id="bloco_dados_nfe">
			<legend>::: Dados da NF-e :::</legend>
			<div class="label">Pedido:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="idPedido" name="idPedido" value="${idPedido}" />
			</div>
			<div class="input" style="width: 2%">
				<input type="button" id="botaoPesquisaPedido"
					title="Pesquisar Pedido" value="" class="botaoPesquisarPequeno" />
			</div>
			<%--div para dar o correto alinhamento dos campos no formulario. Nao teve outra alternativa--%>
			<div class="input" style="width: 60%">
			</div>
			<div class="label">N�m. NFe:</div>
			<div class="input" style="width: 25%">
				<input type="text" name="nf.identificacaoNFe.numero" value="${nf.identificacaoNFe.numero}" maxlength="9" style="width: 100%" />
			</div>
			<div class="label">Mod. NFe:</div>
			<div class="input" style="width: 5%">
				<input type="text" name="nf.identificacaoNFe.modelo" value="${nf.identificacaoNFe.modelo}" maxlength="2" style="width: 100%" />
			</div>
			<div class="label">S�rie NFe:</div>
			<div class="input" style="width: 10%">
				<input type="text" name="nf.identificacaoNFe.serie" value="${nf.identificacaoNFe.serie}" maxlength="3" style="width: 100%" />
			</div>
			<div class="label">Tipo Opera��o:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.tipoOperacao" style="width: 100%" >
					<c:forEach var="tipo" items="${listaTipoOperacao}">
						<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq tipoOperacaoSelecionada}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Dest. Opera��o:</div>
			<div class="input" style="width: 20%">
				<select name="nf.identificacaoNFe.destinoOperacao" style="width: 100%" >
					<c:forEach var="tipo" items="${listaTipoDestinoOperacao}">
						<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq tipoDestinoOperacaoSelecionada}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Dt. Ent./Sa�da:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="dataHoraEntradaSaida" name="nf.identificacaoNFe.dataHoraEntradaSaidaProduto" value="${nf.identificacaoNFe.dataHoraEntradaSaidaProduto}" style="width: 100%"/>
			</div>
			<div class="label">Forma Pagamento:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.indicadorFormaPagamento"  style="width: 100%">
					<c:forEach var="formaPagamento" items="${listaTipoFormaPagamento}">
						<option value="${formaPagamento.codigo}" <c:if test="${formaPagamento.codigo eq formaPagamentoSelecionada}">selected</c:if>>${formaPagamento.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Forma Emiss�o:</div>
			<div class="input" style="width: 20%">
				<select name="nf.identificacaoNFe.tipoEmissao"  style="width: 50%">
					<c:forEach var="tipoEmissao" items="${listaTipoEmissao}">
						<option value="${tipoEmissao.codigo}" <c:if test="${tipoEmissao.codigo eq tipoEmissaoSelecionada}">selected</c:if>>${tipoEmissao.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Finalidade Emiss�o:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.finalidadeEmissao"
					style="width: 100%" >
					<c:forEach var="finalidade" items="${listaTipoFinalidadeEmissao}">
						<option value="${finalidade.codigo}" <c:if test="${finalidade.codigo eq finalidadeEmissaoSelecionada}">selected</c:if>>${finalidade.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Presen. Comprador:</div>
			<div class="input" style="width: 80%">
				<select name="nf.identificacaoNFe.tipoPresencaComprador" style="width: 20%">
					<c:forEach var="tipo" items="${listaTipoPresencaComprador}">
						<option value="${tipo.codigo}" <c:if test="${tipo.codigo eq tipoPresencaSelecionada}">selected</c:if>>${tipo.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label">Tipo Impress�o:</div>
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
			<div class="label">Natureza Opera��o:</div>
			<div class="input" style="width: 50%">
				<input type="text" name="nf.identificacaoNFe.naturezaOperacao" value="${nf.identificacaoNFe.naturezaOperacao}" style="width: 100%"/>
			</div>
			
			<div class="divFieldset">
			<fieldset id="bloco_destinatario" class="fieldsetInterno">
				<legend>::: Destinat�rio ::: -</legend>
				<div class="label">Raz�o Social/Nome:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="nomeCliente" name="nf.identificacaoDestinatarioNFe.nomeFantasia" value="${cliente.razaoSocial}"  maxlength="70" style="width: 60%"/>
					<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
				</div>
				
				<div class="label">CNPJ:</div>
				<div class="input" style="width: 15%">
					<input type="text" id="cnpj" name="nf.identificacaoDestinatarioNFe.cnpj"
						value="${cliente.cnpj}"  />
				</div>
				<div class="input" style="width: 2%">
						<input type="button" id="botaoPesquisarCnpjDest"
							title="Pesquisar CNPJ Destinat�rio" value="" class="botaoPesquisarPequeno" />
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
						value="${cliente.cpf}"  />
				</div>
				<div class="label">Telefone:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="telefone" name="nf.identificacaoDestinatarioNFe.enderecoDestinatarioNFe.telefone"
						value="${telefoneContatoPedido}" />
				</div>
				<div class="label">Email:</div>
				<div class="input" style="width: 20%">
					<input type="text" id="email" name="nf.identificacaoDestinatarioNFe.email"
						value="${cliente.email}" class="apenasLowerCase uppercaseBloqueado lowerCase" />
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
				<input type="text" id="chaveReferenciada" style="width: 50%"/>
			</div>
			<div class="label">N�m. Doc. Fiscal:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="numeroReferenciada" maxlength="9"/>
			</div>
			<div class="label">S�rie. Doc. Fiscal:</div>
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
			<div class="label">Emis. Ano/M�s (AAMM):</div>
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
						title="Pesquisar Endere�o" value="" class="botaoPesquisarPequeno"
						style="width: 20px" />
				</div>
				<div class="label">Endere�o:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="enderecoRetirada" name="nf.identificacaoLocalRetirada.logradouro" value="${nf.identificacaoLocalRetirada.logradouro}"/>
				</div>
				<div class="label" style="width: 8%">N�mero:</div>
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
						title="Pesquisar Endere�o" value="" class="botaoPesquisarPequeno"
						style="width: 20px" />
				</div>
				<div class="label">Endere�o:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="enderecoEntrega" name="nf.identificacaoLocalEntrega.logradouro" value="${nf.identificacaoLocalEntrega.logradouro}"/>
				</div>
				<div class="label" style="width: 8%">N�mero:</div>
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
			<legend>::: Produtos e Servi�os :::</legend>
			
			<table id="tabela_produtos" class="listrada">
				<thead>
					<tr>
						<th>Item</th>
						<th>Desc.</th>
						<th>NCM</th>
						<th>Venda</th>
						<th>Qtde.</th>
						<th>Unid.(R$)</th>
						<th>Tot.(R$)</th>
						<th>BC ICMS</th>
						<th>V. ICMS(R$)</th>
						<th>V IPI.(R$)</th>
						<th>Aliq. ICMS(%)</th>
						<th>Aliq. IPI(%)</th>
						<th style="width: 2%">A��es</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${listaItem}" varStatus="count">
						<tr>
							<td>${item.sequencial}</td>
							<td>${item.descricaoSemFormatacao}</td>
							<td>${item.ncm}</td>
							<td>${item.tipoVenda}</td>
							<td>${item.quantidade}</td>
							<td>${item.precoUnidadeFormatado}</td>
							<td>${item.valorTotalFormatado}</td>
							<td>${item.valorTotalFormatado}</td>
							<td>${item.valorICMSFormatado}</td>
							<td>${item.valorIPIFormatado}</td>
							<td>${item.aliquotaICMS}</td>
							<td>${item.aliquotaIPI}</td>
							<td>
								<input type="button" value="" title="Editar Produto" class="botaoDinheiroPequeno" onclick="editarProduto(this.parentNode.parentNode);"/>
							</td>
							
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<fieldset id="bloco_info_adicionais_prod" class="fieldsetInterno">
				<legend>::: Info. Adicionais Prod. ::: +</legend>
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
				<div class="input" style="width: 50%">
					<input type="text" id="valorFreteProd" style="width: 20%"/>
				</div>
				<div class="label">N�m. FCI:</div>
				<div class="input" style="width: 70%">
					<input type="text" id="fciProd" maxlength="36" style="width: 50%"/>
				</div>
				<div class="label">Info. Produto:</div>
				<div class="input areatexto" style="width: 70%">
					<textarea id="infoAdicionaisProd" style="width: 100%"></textarea>
				</div>
				<div class="bloco_botoes">
						<input type="button" id="botaoInserirInfoProd" title="Inserir Informa��es do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparInfoProd" title="Limpar Informa��es do Produto" value="" class="botaoLimpar"/>
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
					<div class="label obrigatorio">Situa��o Tribut.:</div>
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
					<div  class="label obrigatorio">Al�quota(%):</div>
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
					<div  class="label">Perc. Redu��o BC ST:</div>
					<div class="input" style="width: 50%">
						<input id="percRedBCSTICMS" type="text" style="width: 25%" />
					</div>
					<div  class="label">Valor BC ST:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCSTICMS" type="text" style="width: 100%" />
					</div>
					<div  class="label">Al�quota ST:</div>
					<div class="input" style="width: 50%">
						<input id="aliquotaSTICMS" type="text" style="width: 25%" />
					</div>
					<div  class="label">Val. Desonera��o:</div>
					<div class="input" style="width: 10%">
						<input id="valDesonICMS" type="text" style="width: 100%" />
					</div>
					<div class="icms00 label">Mot. Desonera��o:</div>
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
					<div  class="label obrigatorio">Al�q. Interna:</div>
					<div class="input" style="width: 20%">
						<input id="aliquotaUFDestICMSInter" type="text" style="width: 50%" />
					</div>
					<div  class="label obrigatorio">Al�q. Interest.:</div>
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
					<div  class="label">Val. ICMS:</div>
					<div class="input" style="width: 10%">
						<input id="valorICMSInter" type="text" readonly="readonly" style="width: 100%" />
					</div>
					<div  class="label">Val. Part:</div>
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
					<div class="label obrigatorio">Situa��o Tribut.:</div>
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
					<div  class="label obrigatorio">Al�quota(%):</div>
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
					<div class="label obrigatorio">Situa��o Tribut.:</div>
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
					<div  class="label obrigatorio">Al�quota(%):</div>
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
					<div class="label obrigatorio">Situa��o Tribut.:</div>
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
					<div  class="label">Al�quota:</div>
					<div class="input" style="width: 10%">
						<input id="aliquotaIPI" type="text" style="width: 100%" />
					</div>
					<div  class="label">Valor Cal.:</div>
					<div class="input" style="width: 20%">
						<input id="valorIPI" type="text" readonly="readonly" style="width: 50%" />
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
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_iss" class="fieldsetInterno">
					<legend>::: ISSQN Prod.::: +</legend>
					<div class="label obribagorio">Situa��o Tribut.:</div>
					<div class="input" style="width: 80%">
						<select id="codSitTribISS" style="width: 45%">
							<option value=""></option>
							<c:forEach var="tipo" items="${listaTipoTributacaoISS}">
								<option value="${tipo.codigo}">${tipo.descricao}</option>
							</c:forEach>
						</select>
					</div>
					<div  class="label obribagorio">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input id="valorBCISS" type="text" style="width: 100%" />
					</div>
					<div  class="label obribagorio">Al�quota(%):</div>
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
					<div  class="label obribagorio">Item Servi�o:</div>
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
					<legend>::: Importa��o Prod.::: +</legend>
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
						<input type="button" id="botaoInserirII" title="Inserir Imp. Importa��o do Produto" value="" class="botaoInserir"/>
						<input type="button" id="botaoLimparII" title="Limpar Imp. Importa��o do Produto" value="" class="botaoLimpar"/>
					</div>
				</fieldset>
				</div>
			</fieldset>
			<fieldset id="bloco_importacao_prod" class="fieldsetInterno">
				<legend>::: Importa��o ::: +</legend>
				<div class="label">CNPJ:</div>
				<div class="input" style="width: 20%">
					<input type="text" id="cnpjImportProd" maxlength="14" style="width: 100%"/>
				</div>
				<div class="label obrigatorio">C�d. Export.:</div>
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
				<div class="label obrigatorio">N�mero:</div>
				<div class="input" style="width: 40%">
					<input type="text" id="numImportProd" maxlength="12" style="width: 50%"/>
				</div>
				<div class="label obrigatorio">Intermedia��o:</div>
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
					<a id="botaoInserirImportacaoProd" title="Inserir Importa��o de Produto" class="botaoAdicionar"></a>
				</div>
				<table id="tabela_importacao_prod" class="listrada" >
					<thead>
						<tr>
							<th>CNPJ</th>
							<th>C�d. Export.</th>
							<th>Dt. Import.</th>
							<th>Dt. Desemb.</th>
							<th>Local</th>
							<th>N�m.</th>
							<th>Intermed.</th>
							<th>Transp.</th>
							<th>UF Desemb.</th>
							<th>UF Encomend.</th>
							<th>Vl. AFRMM</th>
							<th>A��es</th>
						</tr>
					</thead>
					
					<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
					<tbody>
					</tbody>
				</table>
				<fieldset id="bloco_adicao_import" class="fieldsetInterno">
					<legend>::: Adi��o de Importa��o :::</legend>
					<div class="label obrigatorio">C�d. Fabric.:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="codFabricAdicao" style="width: 100%"/>
					</div>
					<div class="label obrigatorio">N�m.:</div>
					<div class="input" style="width: 40%">
						<input type="text" id="numAdicao" style="width: 50%"/>
					</div>
					<div class="label">N�m. Drawback:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="numDrawbackAdicao" style="width: 100%"/>
					</div>
					<div class="label obrigatorio">N�m. Sequenc.:</div>
					<div class="input" style="width: 40%">
						<input type="text" id="numSeqAdicao" style="width: 50%"/>
					</div>
					<div class="label">Vl. Desc.:</div>
					<div class="input" style="width: 20%">
						<input type="text" id="valDescAdicao" style="width: 100%"/>
					</div>
					
					<div class="bloco_botoes">
						<a id="botaoInserirAdicao" title="Inserir Adi��o de Importa��o" class="botaoAdicionar"></a>
					</div>
									
					<table id="tabela_adicao_import" class="listrada" >
						<thead>
							<tr>
								<th>Fabric.</th>
								<th>N�m.</th>
								<th>N�m. Drawback</th>
								<th>N�m. Sequenc.</th>
								<th>Vl. Desc.</th>
								<th>A��es</th>
							</tr>
						</thead>
						
						<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
						<tbody>
						</tbody>
					</table>
				</fieldset>
			</fieldset>
			<fieldset id="bloco_exportacao_prod" class="fieldsetInterno">
					<legend>::: Exporta��o ::: +</legend>
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
						<a id="botaoInserirExportacaoProd" title="Inserir Exporta��o do Produto" class="botaoAdicionar"></a>
					</div>
									
					<table id="tabela_exportacao_prod" class="listrada">
						<thead>
							<tr>
								<th>Drawback</th>
								<th>Ch Acesso</th>
								<th>Registro</th>
								<th>Qutde.</th>
								<th>A��es</th>
							</tr>
						</thead>
						
						<%-- Devemos ter um tbody pois eh nele que sao aplicados os estilos em cascata, por exemplo, tbody tr td. --%>
						<tbody>
						</tbody>
					</table>
				</fieldset>
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
					<div  class="label">Raz�o Soc./Nome:</div>
					<div class="input" style="width: 80%">
						<input type="text" id="nomeTransportadora" name="nf.transporteNFe.transportadoraNFe.razaoSocial" value="${transportadora.razaoSocial}" style="width: 45%" />
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
					<div  class="label">Endere�o:</div>
					<div class="input" style="width: 80%">
						<input type="text" id="endTransportadora" name="nf.transporteNFe.transportadoraNFe.enderecoCompleto" value="${transportadora.endereco}" style="width: 84%" />
					</div>
					<div  class="label">Munic�pio:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="munTransportadora" name="nf.transporteNFe.transportadoraNFe.municipio" value="${transportadora.municipio}" style="width: 100%" />
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
				<fieldset id="bloco_veiculo" class="fieldsetInterno">
					<legend>::: Ve�culo/Reboque/Balsa/Vag�o :::</legend>
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
								<th>A��es</th>
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
				<fieldset class="fieldsetInterno">
					<legend>::: Reten��o ICMS :::</legend>
					<div  class="label obrigatorio">Valor Servi�o:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valServRetICMSTransp" name="nf.transporteNFe.retencaoICMS.valorServico" value="${nf.transporteNFe.retencaoICMS.valorServico}" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Valor BC:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="valBCRetICMSTransp" name="nf.transporteNFe.retencaoICMS.valorBC" value="${nf.transporteNFe.retencaoICMS.valorBC}" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Al�quota(%):</div>
					<div class="input" style="width: 30%">
						<input type="text" id="aliqRetICMSTransp" name="nf.transporteNFe.retencaoICMS.aliquota" value="${nf.transporteNFe.retencaoICMS.aliquota}" style="width: 20%" />
					</div>
					<div  class="label obrigatorio">CFOP:</div>
					<div class="input" style="width: 10%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.cfop" value="${nf.transporteNFe.retencaoICMS.cfop}" maxlength="4" style="width: 100%" />
					</div>
					<div  class="label obrigatorio">Munic�pio Gerador:</div>
					<div class="input" style="width: 50%">
						<input type="text" name="nf.transporteNFe.retencaoICMS.codigoMunicipioGerador" value="${nf.transporteNFe.retencaoICMS.codigoMunicipioGerador}" maxlength="7" style="width: 20%" />
					</div>
				</fieldset>
				</div>
				
				<div class="divFieldset">
				<fieldset id="bloco_volume" class="fieldsetInterno">
					<legend>::: Volumes :::</legend>
					<div class="label">Qtde.:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="quantidadeVolume" maxlength="15"/>
					</div>
					
					<div class="label">Esp�cie:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="especieVolume" maxlength="60"/>
					</div>
					<div class="label">Marca:</div>
					<div class="input" style="width: 30%">
						<input type="text" id="marcaVolume" maxlength="60" style="width: 50%"/>
					</div>
					<div class="label">Numera��o:</div>
					<div class="input" style="width: 10%">
						<input type="text" id="numeracaoVolume" maxlength="60"/>
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
		</fieldset>
		
		<fieldset>
			<legend>::: Cobran�a :::</legend>
			<div class="label">N�mero:</div>
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
			<div class="label">Valor L�quido:</div>
			<div class="input" style="width: 55%">
				<input type="text" name="nf.cobrancaNFe.faturaNFe.valorLiquido" value="${nf.cobrancaNFe.faturaNFe.valorLiquido}" style="width: 20%"/>
			</div>
			
			<div class="divFieldset">
			<fieldset id="bloco_duplicata" class="fieldsetInterno">
				<legend>::: Duplicata :::</legend>
				<div class="label">N�mero:</div>
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
							<th>N�m.</th>
							<th>Dt. Venc.</th>
							<th>Valor(R$)</th>
							<th>A��es</th>
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
				<textarea name="nf.informacoesAdicionaisNFe.informacoesAdicionaisInteresseFisco" style="width: 100%">${nf.informacoesAdicionaisNFe.informacoesAdicionaisInteresseFisco}</textarea>
			</div>
			<div class="label">Info. Adicionais Contrib.:</div>
			<div class="input areatexto" style="width: 70%">
				<textarea name="nf.informacoesAdicionaisNFe.informacoesComplementaresInteresseContribuinte" style="width: 100%">${nf.informacoesAdicionaisNFe.informacoesComplementaresInteresseContribuinte}</textarea>
			</div>
		</fieldset>
		<fieldset id="bloco_exportacao">
			<legend>::: Exporta��o/Compra ::: -</legend>
			<div class="label">UF Embarque:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.exportacaoNFe.ufEmbarque" value="${nf.exportacaoNFe.ufEmbarque}" maxlength="2" style="width: 5%"/>
			</div>
			<div class="label">Local Embarque:</div>
			<div class="input" style="width: 80%">
				<input type="text" name="nf.exportacaoNFe.localEmbarque" value="${nf.exportacaoNFe.localEmbarque}" maxlength="60" style="width: 60%"/>
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
			<input type="button" id="botaoEmitirNF" title="Emitir Nota Fiscal" value="" class="botaoEnviarEmail"/>
		</div>

		<jsp:include page="/bloco/bloco_detalhe_items_nfe.jsp"></jsp:include>		
	</form>

</body>
</html>
