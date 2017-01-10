var tabelaItemHandler = null;

function inicializarBlocoItemPedido(urlTela) {
	
	var TOTAL_COLUNAS_ITEM_PEDIDO = 11;
	
	tabelaItemHandler = new BlocoTabelaHandler(urlTela, 'ItemPedido',
			'tabelaItemPedido', 'bloco_item_pedido');
	tabelaItemHandler.setTotalColunas(TOTAL_COLUNAS_ITEM_PEDIDO);

	// Estamos passando uma funcao de callback para a tabela_handler popular o
	// valor do pedido atualizado
	tabelaItemHandler.removerRegistroCallback(function(response) {
		var pedido = response.pedido;
		if(pedido == undefined || pedido == null){
			return;
		}
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
					
					case 10:
						celula.innerHTML = $("#bloco_item_pedido #prazoEntregaItem")
								.val();
						break;
					
					}

				}
			});

	tabelaItemHandler
			.editarRegistro(function(linha) {

				var request = $.ajax({
					type : 'get',
					url : tabelaItemHandler.urlTela + '/item/' + linha.cells[0].innerHTML
				});

				request.done(function(response) {
					var itemPedidoJson = response.itemPedido;
					var erros = response.erros;
					var contemErros = erros != undefined && erros !=null;
					var contemItem = itemPedidoJson != undefined && itemPedidoJson !=null;
					if(!contemErros && contemItem){
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
						$('#prazoEntregaItem').val(itemPedidoJson.prazoEntrega);
						$('#aliquotaComissao').val(itemPedidoJson.aliquotaComissao);
						$('#ncm').val(itemPedidoJson.ncm);
						$('#cst').val(itemPedidoJson.tipoCST);
						
						habilitarPreenchimentoPeca(itemPedidoJson.peca);
					} else if(!contemErros && !contemItem){
						gerarListaMensagemAlerta(['Usuario pode nao estar logado no sistema']);
					} else if(contemErros){
						gerarListaMensagemErro(erros);
					}
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
		if (itemPedidoAcionado && response.erros == undefined && pedido != undefined && pedido != null) {
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
					if(clienteJson==undefined || clienteJson==null){
						return;
					}
					$('#idCliente').val(clienteJson.id);
					$('#formPesquisa #idClientePesquisa').val(clienteJson.id);
					$('#site').val(clienteJson.site);
					$('#email').val(clienteJson.email);
					$('#cnpj').val(clienteJson.cnpj);
					$('#cpf').val(clienteJson.cpf);
					$('#nomeCliente').val(clienteJson.nomeCompleto);
					$('#idVendedor').val(clienteJson.vendedor.id);
					$('#proprietario').val(clienteJson.vendedor.nome + ' - '+ clienteJson.vendedor.email);

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
	if (!isEmpty(numeroPedido)) {
		
		var parametros = serializarBloco('bloco_item_pedido');
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

function habilitarIPI(urlTela, idRepresentada) {
	if(isEmpty(idRepresentada)){
		return;
	}
	var request = $.ajax({
		type : 'get',
		url : urlTela + '/representada/' + idRepresentada + '/aliquotaIPI/'
	});

	request.done(function(response) {
		habilitar('#bloco_item_pedido #aliquotaIPI',
				response.representada.ipiHabilitado);
	});

	request
			.fail(function(request, status) {
				alert('Falha na verifica��o se � poss�vel o c�lculo do IPI pela representada => '
						+ request.responseText);
			});
}