function inicializarSelectFormaMaterial() {
	$('#bloco_item_pedido #formaMaterial').change(function() {
		var forma = $(this).val();
		habilitarPreenchimentoPeca('PC' == forma);
		habilitarPreenchimentoMedidaInterna('CH' == forma || 'TB' == forma);
	});
};

function habilitarPreenchimentoPeca(isPeca) {
	habilitar('#bloco_item_pedido #descricao', isPeca);
	habilitar('#bloco_item_pedido #peso', isPeca);
	habilitar('#bloco_item_pedido #medidaExterna', !isPeca);
	habilitar('#bloco_item_pedido #medidaInterna', !isPeca);
	habilitar('#bloco_item_pedido #comprimento', !isPeca);
};

function habilitarPreenchimentoMedidaInterna(temMedidaInterna) {
	habilitar('#bloco_item_pedido #medidaInterna', temMedidaInterna);
};



function inserirPedido(itemPedidoAcionado, urlInclusaoPedido,
		urlInclusaoItemPedido) {
	toUpperCaseInput();
	toLowerCaseInput();
	
	var parametros = $('#formPedido').serialize();
	parametros += serializarBloco('bloco_dados_nota_fiscal');
	parametros += recuperarParametrosBlocoContato();
	// Esse termo foi incluido para podermos recuperar o nome do cliente no caso em que temos um orcamento e o cliente nao exista.
	parametros += '&pedido.cliente.nomeFantasia='+$('#formPedido #nomeCliente').val(); 
	var request = $.ajax({
		type : "post",
		url : urlInclusaoPedido,
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
			$('#numeroPedido').val(pedidoJson.id);
			$('#tipoPedido').val(pedidoJson.tipoPedido);
			$('#numeroPedidoPesquisa').val(pedidoJson.id);
			$('#formEnvioPedido #idPedido').val(pedidoJson.id);
			
			$('#situacaoPedido').val(pedidoJson.situacaoPedido);
			
			// preenchendo esse campo caso o usuario queira cancelar o pedido
			$('#idPedidoCancelamento').val(pedidoJson.id);
			$('#dataInclusao').val(pedidoJson.dataInclusaoFormatada);
			$('#proprietario').val(
					pedidoJson.proprietario.nome + ' - '
							+ pedidoJson.proprietario.email);
			$('#idVendedor').val(pedidoJson.proprietario.id);
			$('#formEnvioPedido #botaoEnviarPedido').show();
			
			habilitar('#numeroPedidoPesquisa', false);

			/*
			 * Nao adicionaremos a mensagem de sucesso no caso em que o usuario
			 * ja esteja incluindo os itens do pedido
			 */
			if (!itemPedidoAcionado) {
				gerarListaMensagemSucesso(new Array('O pedido No. '
						+ pedidoJson.id + ' foi incluido com sucesso.'));
			}

		} else if(!contemErro && !contemPedido) {
			gerarListaMensagemAlerta(['O usuario pode nao estar logado no sistema']);
		} else if(contemErro) {
			gerarListaMensagemErro(erros);
		}
	});

	request.always(function(response) {
		// se nao contem erro e foi clicao o botao de inclusao de item de
		// pedidos
		var pedido = response.pedido;
		if (itemPedidoAcionado && response.erros == undefined && pedido != undefined && pedido != null && !isEmpty(pedido.id)) {
			inserirItemPedido(pedido.id, urlInclusaoItemPedido);
		}
	});

	request.fail(function(request, status) {
		alert('Falha inclusao do pedido => Status da requisicao: ' + status);
	});
};

function recuperarParametrosBlocoContato() {
	if (isEmpty($('#bloco_contato #contato_nome').val())) {
		return '';
	}
	return '&' + $('#bloco_contato').serialize();
};

function inicializarAutocompleteCliente(url, preencherCampos) {
	autocompletar({
		url : url,
		campoPesquisavel : 'nomeCliente',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaCliente',
		selecionarItem : function(itemLista) {
			// Vamos utilizar a conversao de pedido/cliente/1, onde o ultimo
			// termo se refere ao ID do cliente
			var request = $.ajax({
				type : "get",
				url : url + '/' + itemLista.id
			});

			request.done(function(response) {
				var erros = response.erros;
				var contemErro = erros != undefined;
				if (!contemErro) {
					var clienteJson = response.cliente;
					if(clienteJson==undefined || clienteJson==null){
						return;
					}
					if(preencherCampos != undefined){
						preencherCampos(clienteJson);
					}
					/*
					 * As mensagens de alerta sempre serao exibidas pois nao
					 * devem comprometer o fluxo da navegacao do usuario.
					 */
					gerarListaMensagemAlerta(clienteJson.listaMensagem);

				} else if (erros != undefined) {
					gerarListaMensagemErro(erros);
				}

			});
		}
	});
};

function inicializarAutocompleteMaterial(url) {
	autocompletar({
		url : url,
		campoPesquisavel : 'material',
		parametro : 'sigla',
		containerResultados : 'containerPesquisaMaterial',
		gerarVinculo : function () {return 'idRepresentada=' + $('#representada').val()},
		selecionarItem : function(itemLista) {
			$('#idMaterial').val(itemLista.id);
		}
	});
};

function inicializarAutocompleteDescricaoPeca(url) {
	autocompletar({
		url : url,
		campoPesquisavel : 'descricao',
		parametro : 'descricao',
		containerResultados : 'containerPesquisaDescricaoPeca',
		selecionarItem : function(itemLista) {
			$('#idPecaEstoque').val(itemLista.id);
		}
	});
};

function inserirItemPedido(numeroPedido, urlInclusaoItemPedido) {
		var parametros = serializarBloco('bloco_item_pedido');
		parametros += '&numeroPedido=' + numeroPedido;
		var request = $.ajax({
			type : 'post',
			url : urlInclusaoItemPedido,
			data : parametros,
			async: false
		});

		request.done(function(response) {
			var erros = response.erros;
			var contemErro = erros != undefined;
			// Ocultando no caso de que o usuario envie um novo request com a
			// area de mensagem renderizada
			$('#bloco_mensagem').fadeOut();

			// Verificando se existe algum erro no processo de inclusao
			var itemPedido = response.itemPedido;
			var contemItem = itemPedido != undefined && itemPedido !=null;
			if (!contemErro && contemItem) {
				var itemPedido = response.itemPedido;
				// Esses valore de campos hidden serao utilizados na inclusao do
				// novo item na tabela de itens do pedido
				$('#bloco_item_pedido #tabelaItemPedido #valorPedido').html(
						itemPedido.valorPedido);
				$('#bloco_item_pedido #tabelaItemPedido #valorPedidoIPI').html(
						itemPedido.valorPedidoIPI);

				$('#bloco_item_pedido #aliquotaICMS').val(
						itemPedido.aliquotaICMS);
				$('#bloco_item_pedido #precoUnidade').val(
						itemPedido.precoUnidade);
				// Esse campo sera usado para popular a tabela de itens com os
				// dados pois nao estao no grid de inputs com os dados do item
				$('#bloco_item_pedido #aliquotaIPI').val(
						itemPedido.aliquotaIPI);
				// Esse valores foram preenchidos no controller de acordo com a
				// forma do material
				$('#bloco_item_pedido #descricaoItemPedido').val(
						itemPedido.descricaoItemPedido);
				/*
				 * Eh necessario esse campo pois cada linha da tabela de itens
				 * do pedido deve conter um id para posteriormente efetuarmos a
				 * edicao do item
				 */
				$('#bloco_item_pedido #idItemPedido').val(itemPedido.id);
				
				/*
				 * O sequencial eh o indicador de qual item o vendedor esta atuando assim pode
				 * fazer referencia a esse item no campo de observacao.
				 */
				$('#bloco_item_pedido #sequencial').val(itemPedido.sequencial);
				
				$('#bloco_item_pedido #aliquotaComissao').val(itemPedido.aliquotaComissao);
				
				/*
				 * Aqui o campo de representada sera desabilitado e nao sera
				 * enviado na submissao do formulario, por isso criamos um campo
				 * oculto idRepresentada.
				 */
				$('#idRepresentada').val($('#representada').val());
				$('#idRepresentada').attr('disabled', false);
				
				/*
				 * Preenchendo o campo com o valor do item que foi calculado no servidor.
				 */
				$('#bloco_item_pedido #precoItem').val(itemPedido.precoItem);
				
				habilitar('#representada', false);

				tabelaItemHandler.adicionar();
			} else if(!contemErro && !contemItem) {
				gerarListaMensagemAlerta(['O usuário pode não estar logado no sistema']);
			} else if(contemErro) {
				gerarListaMensagemErro(erros);
			}
		});

		request.fail(function(request, status) {
			alert('Falha inclusao do tem do pedido => Status da requisicao: '
					+ status);
		});

		request.always(function(response) {
			$('#tipoVendaKilo').val('KILO');
			$('#tipoVendaPeca').val('PECA');
		});
};

function inicializarFiltro() {
	$("#filtro_nomeFantasia").val($("#nomeFantasia").val());
	$("#filtro_cnpj").val($("#cnpj").val());
	$("#filtro_cpf").val($("#cpf").val());
	$("#filtro_email").val($("#email").val());
};

function contactarCliente(idCliente) {
	$('#formContactarCliente #idClienteContactado').val(idCliente);
	$('#formContactarCliente').submit();
};

function preencherComboTransportadora(combo, listaTransportadora) {
	if(combo == undefined){
		return;
	}
	var TOTAL_TRANSPORTADORAS = listaTransportadora.length;
	for (var i = 0; i < TOTAL_TRANSPORTADORAS; i++) {
		combo.add(new Option(listaTransportadora[i].nomeFantasia,
				listaTransportadora[i].id), null);
	}
};

function habilitarIPI(urlTela, idRepresentada) {
	if(isEmpty(idRepresentada)){
		return;
	}
	var request = $.ajax({
		type : 'get',
		url : urlTela + '/representada/' + idRepresentada + '/aliquotaIPI/',
		async: true
	});

	request.done(function(response) {
		habilitar('#bloco_item_pedido #aliquotaIPI',
				response.representada.ipiHabilitado);
	});

	request
			.fail(function(request, status) {
				alert('Falha na verificação se é possível o cálculo do IPI pela representada => '
						+ request.responseText);
			});
};