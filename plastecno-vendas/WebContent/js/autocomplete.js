var autocompletar = function(configuracao) {
	var url = configuracao.url;
	var idCampoPesquisavel = '#' + configuracao.campoPesquisavel;
	var parametro = configuracao.parametro;
	var idContainerResultados = '#' + configuracao.containerResultados;
	var selecionarItem = configuracao.selecionarItem;
	var gerarVinculo = configuracao.gerarVinculo;
	
	var pesquisar = function() {
		
		var valorPesquisa = $(idCampoPesquisavel).val();
		var vinculo = gerarVinculo != undefined ? '&' + gerarVinculo() : '';
		
		if (valorPesquisa == null || valorPesquisa.length == 0) {
			$(idContainerResultados).hide();
		} else {

			var request = $.ajax({
				type : "get",
				url : url,
				data : parametro + '=' + valorPesquisa.toUpperCase() + vinculo
			});

			request.done(function(response) {
						var resultado = response.lista;
						var TOTAL_REGISTROS = resultado.length;

						if (TOTAL_REGISTROS > 0) {
							var conteudo = '<ul >';
							for (var x = 0; x < TOTAL_REGISTROS; x++) {
								conteudo += '<li class="conbgn" id="'
										+ resultado[x].valor + '">'
										+ resultado[x].label + '</li>';
							}

							conteudo += '</ul>';

							conteudo += '<div style="background-color: #BECEBE; text-align: center;" ">Lista de "'+ TOTAL_REGISTROS+ '" resultados resultados para "'+ valorPesquisa.toUpperCase()+ '" </br></div>';
							$(idContainerResultados).html(conteudo);

							$(idContainerResultados + ' ul li ').click(
									function() {
										var preencherCampo = function(itemSelecionado) {
											$(idCampoPesquisavel).val(itemSelecionado.innerHTML);
											$(idContainerResultados).hide();
											selecionarItem(itemSelecionado);
										};

										preencherCampo(this);
									});
							
							
							$(idContainerResultados + ' ul li ').keydown(function (e){
								if(e.keyCode == 40){
									$(this).next().focus();
								} else if (e.keyCode == 38){
									$(this).prev().focus();
								} else if (e.keyCode == 30) {
									preencherCampo(this);
								}
								alert($(this).val());
							});
							
							$(idContainerResultados).show();

						} else {
							$(idContainerResultados).hide();
						};
					});

			request.fail(function(request, status, excecao) {
				var mensagem = 'Falha no AUTOCOMPLETE do campo: '+ idCampoPesquisavel;
				mensagem += ' para a URL ' + url;
				mensagem += ' contendo o valor de requisicao ' + parametro;
				mensagem += ' => Excecao: ' + excecao;
				gerarListaMensagemErro(new Array(mensagem));
			});
		}
		;
	};

	$(idCampoPesquisavel).keydown(function (e){
		
		if(e.keyCode == 40){
			$(idContainerResultados+ ' ul li:first').css('background-color', 'black').css('color', 'white');
			$(idContainerResultados + ' ul li:first').focus();
		} else{
			pesquisar();
		}
	});
};
