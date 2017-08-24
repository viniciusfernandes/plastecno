function gerarTabelaItemPedido(urlTela) {
	
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
						$('#peso').val(itemPedidoJson.peso);
						
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
	
	return tabelaItemHandler;
};


function editarItemPedido(botaoEdicao) {
	tabelaItemHandler.editar(botaoEdicao);
};

function removerItemPedido(botaoRemocao) {
	tabelaItemHandler.removerRegistro(botaoRemocao);
};