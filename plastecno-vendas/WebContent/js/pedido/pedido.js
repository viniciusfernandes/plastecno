var tabelaItemHandler = null;

function inicializarBlocoItemPedido(urlTela) {
	
	var TOTAL_COLUNAS_ITEM_PEDIDO = 10;
	
	tabelaItemHandler = new BlocoTabelaHandler(urlTela, 'ItemPedido',
			'tabelaItemPedido', 'bloco_item_pedido');
	tabelaItemHandler.setTotalColunas(TOTAL_COLUNAS_ITEM_PEDIDO);

	// Estamos passando uma funcao de callback para a tabela_handler popular o
	// valor do pedido atualizado
	tabelaItemHandler.removerRegistroCallback(function(response) {
		var pedido = response.pedido;
		$('#tabelaItemPedido tfoot #valorPedido').html(pedido.valorPedido);
		$('#tabelaItemPedido tfoot #valorPedidoIPI')
				.html(pedido.valorPedidoIPI);
	});

	tabelaItemHandler
			.incluirRegistro(function(ehEdicao, linha) {
				var celula = null;
				for (var i = 0; i < this.TOTAL_COLUNAS; i++) {
					celula = ehEdicao ? linha.cells[i] : linha.insertCell(i);

					// Ocultando a primeira celula que eh reservada ao ID do
					// registro
					switch (i) {
					case 0:
						celula.innerHTML = $('#bloco_item_pedido #idItemPedido')
								.val();
						
						celula.style.display="none";
						break;
						
					case 1:
						celula.innerHTML = $('#bloco_item_pedido #sequencial')
								.val();
						break;

					case 2:
						celula.innerHTML = $('#bloco_item_pedido #quantidade')
								.val();
						break;
					case 3:
						/*
						 * Esse item foi recuperado o json retornado pelo
						 * controller pois assim teremos e que eh identica ao
						 * que em outros pontos da aplicacao
						 */
						celula.innerHTML = $(
								'#bloco_item_pedido #descricaoItemPedido')
								.val();
						break;
					case 4:
						// Pegando o valor que foi escolhido no ragio group do
						// tipo de vendas
						celula.innerHTML = $(
								"#bloco_item_pedido input[type='radio']:checked")
								.val();
						break;
					case 5:
						celula.innerHTML = $("#bloco_item_pedido #precoVenda")
								.val();
						break;
					case 6:
						celula.innerHTML = $("#bloco_item_pedido #precoUnidade")
								.val();
						break;

					case 7:
						celula.innerHTML = $("#bloco_item_pedido #precoItem").val();
						break;
						
					case 8:
						celula.innerHTML = $("#bloco_item_pedido #aliquotaIPI").val();
						break;
					
					case 9:
						celula.innerHTML = $("#bloco_item_pedido #aliquotaICMS")
								.val();
						break;
					}

				}
			});

	tabelaItemHandler
			.editarRegistro(function(linha) {

				var request = $.ajax({
					type : 'get',
					url : '/vendas/pedido/item/' + linha.cells[0].innerHTML
				});

				request.done(function(response) {
					var itemPedidoJson = response.itemPedido;
					$('#idItemPedido').val(itemPedidoJson.id);
					$('#sequencial').val(itemPedidoJson.sequencial);
					
					$('#formaMaterial').val(itemPedidoJson.formaMaterial);

					if (itemPedidoJson.vendaKilo) {
						$('#tipoVendaKilo').prop('checked', true);
					} else {
						$('#tipoVendaPeca').prop('checked', true);
					}

					$('#quantidade').val(itemPedidoJson.quantidade);
					$('#material').val(itemPedidoJson.siglaMaterial);
					$('#idMaterial').val(itemPedidoJson.idMaterial);
					$('#medidaExterna').val(itemPedidoJson.medidaExterna);
					$('#medidaInterna').val(itemPedidoJson.medidaInterna);
					$('#comprimento').val(itemPedidoJson.comprimento);
					$('#precoVenda').val(itemPedidoJson.precoVenda);
					$('#precoUnidade').val(itemPedidoJson.precoUnidade);
					$('#descricao').val(itemPedidoJson.descricaoPeca);
					$('#aliquotaICMS').val(itemPedidoJson.aliquotaICMS);
					$('#aliquotaIPI').val(itemPedidoJson.aliquotaIPI);
					
					habilitarPreenchimentoPeca(itemPedidoJson.peca);
				});

				request
						.fail(function(request, status) {
							alert('Falha na pesquisa do item do pedido => Status da requisicao: '
									+ status);
						});

			});

	inicializarSelectFormaMaterial();
};

function inicializarSelectFormaMaterial() {
	$('#bloco_item_pedido #formaMaterial').change(function() {
		var forma = $(this).val();
		habilitarPreenchimentoPeca('PC' == forma);
		habilitarPreenchimentoMedidaInterna('CH' == forma || 'TB' == forma);
	});
};

function habilitarPreenchimentoPeca(isPeca) {
	habilitar('#bloco_item_pedido #descricao', isPeca);
	habilitar('#bloco_item_pedido #medidaExterna', !isPeca);
	habilitar('#bloco_item_pedido #medidaInterna', !isPeca);
	habilitar('#bloco_item_pedido #comprimento', !isPeca);
};

function habilitarPreenchimentoMedidaInterna(temMedidaInterna) {
	habilitar('#bloco_item_pedido #medidaInterna', temMedidaInterna);
};

function editarItemPedido(botaoEdicao) {
	tabelaItemHandler.editar(botaoEdicao);
};

function removerItemPedido(botaoRemocao) {
	tabelaItemHandler.removerRegistro(botaoRemocao);
};


function inserirPedido(itemPedidoAcionado, urlInclusaoPedido,
		urlInclusaoItemPedido) {
	toUpperCaseInput();
	toLowerCaseInput();
	
	var parametros = $('#formPedido').serialize();
	parametros += recuperarParametrosBlocoContato();

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

		if (!contemErro) {
			var pedidoJson = response.pedido;
			/*
			 * Temos que ter esse campo oculto pois o campo Numero do Pedido na
			 * tela sera desabilitado e nao sera enviado no request.
			 */
			$('#numeroPedido').val(pedidoJson.id);
			$('#numeroPedidoPesquisa').val(pedidoJson.id);
			$('#formEnvioPedido #idPedido').val(pedidoJson.id);

			// preenchendo esse campo caso o usuario queira cancelar o pedido
			$('#idPedidoCancelamento').val(pedidoJson.id);
			$('#dataInclusao').val(pedidoJson.dataInclusaoFormatada);
			$('#vendedor').val(
					pedidoJson.vendedor.nome + ' - '
							+ pedidoJson.vendedor.email);
			$('#idVendedor').val(pedidoJson.vendedor.id);
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

		} else {
			gerarListaMensagemErro(erros);
		}
	});

	request.always(function(response) {
		// se nao contem erro e foi clicao o botao de inclusao de item de
		// pedidos
		if (itemPedidoAcionado && response.erros == undefined) {
			inserirItemPedido(response.pedido.id, urlInclusaoItemPedido);
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
}

function inicializarAutomcompleteCliente(url) {
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
					$('#idCliente').val(clienteJson.id);
					$('#formPesquisa #idClientePesquisa').val(clienteJson.id);
					$('#site').val(clienteJson.site);
					$('#email').val(clienteJson.email);
					$('#cnpj').val(clienteJson.cnpj);
					$('#cpf').val(clienteJson.cpf);
					$('#nomeCliente').val(clienteJson.nomeCompleto);
					$('#idVendedor').val(clienteJson.vendedor.id);
					$('#vendedor').val(clienteJson.vendedor.nome + ' - '+ clienteJson.vendedor.email);

					limparComboBox('listaTransportadora');
					limparComboBox('listaRedespacho');

					var comboTransportadora = document.getElementById('listaTransportadora');
					var comboRedespacho = document.getElementById('listaRedespacho');

					preencherComboTransportadora(comboTransportadora, clienteJson.listaTransportadora);
					preencherComboTransportadora(comboRedespacho, clienteJson.listaRedespacho);

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

function inicializarAutomcompleteMaterial(url) {
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

function inserirItemPedido(numeroPedido, urlInclusaoItemPedido) {
	if (!isEmpty(numeroPedido)) {
		
		var parametros = $('#bloco_item_pedido').serialize();
		parametros += '&numeroPedido=' + numeroPedido;
		var request = $.ajax({
			type : 'post',
			url : urlInclusaoItemPedido,
			data : parametros
		});

		request.done(function(response) {
			var erros = response.erros;
			var contemErro = erros != undefined;
			// Ocultando no caso de que o usuario envie um novo request com a
			// area de mensagem renderizada
			$('#bloco_mensagem').fadeOut();

			// Verificando se existe algum erro no processo de inclusao
			if (!contemErro) {
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
			} else {
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
	}
}

function inicializarFiltro() {
	$("#filtro_nomeFantasia").val($("#nomeFantasia").val());
	$("#filtro_cnpj").val($("#cnpj").val());
	$("#filtro_cpf").val($("#cpf").val());
	$("#filtro_email").val($("#email").val());
};

function contactarCliente(idCliente) {
	$('#formContactarCliente #idClienteContactado').val(idCliente);
	$('#formContactarCliente').submit();
}

function preencherComboTransportadora(combo, listaTransportadora) {
	var TOTAL_TRANSPORTADORAS = listaTransportadora.length;
	for (var i = 0; i < TOTAL_TRANSPORTADORAS; i++) {
		combo.add(new Option(listaTransportadora[i].nomeFantasia,
				listaTransportadora[i].id), null);
	}
}

function habilitarIPI(idRepresentada) {
	var request = $.ajax({
		type : 'get',
		url : '/vendas/pedido/representada/' + idRepresentada + '/aliquotaIPI/'
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
}