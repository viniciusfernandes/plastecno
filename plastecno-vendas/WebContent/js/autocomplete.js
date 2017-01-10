var autocompletar = function(configuracao) {
	var url = configuracao.url;
	var idCampoPesquisavel = '#' + configuracao.campoPesquisavel;
	var parametro = configuracao.parametro;
	var idContainerResultados = '#' + configuracao.containerResultados;
	var selecionarItem = configuracao.selecionarItem;
	var gerarVinculo = configuracao.gerarVinculo;
	var count = -1;
	
	$(idCampoPesquisavel).attr('autocomplete', 'off');
	
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
					var erros = response.erros;
					var contemErros = erros != undefined && erros !=null;
					var contemLista = resultado != undefined && resultado !=null;
					if(!contemErros && contemLista){
						var TOTAL_REGISTROS = resultado.length;
						// Devemos concecar no -1 pois o primeiro elemento a ser selecionado sera o de indice zero da lista.
						count = -1;
						
						if (TOTAL_REGISTROS > 0) {
							var conteudo = '<ul id="'+idContainerResultados+'UL">';
							for (var x = 0; x < TOTAL_REGISTROS; x++) {
								conteudo += '<li class="conbgn" id="'
										+ resultado[x].valor + '">'
										+ resultado[x].label + '</li>';
							}

							conteudo += '</ul>';

							conteudo += '<div style="background-color: #BECEBE; text-align: center;" ">Lista de "'+ TOTAL_REGISTROS+ '" resultados resultados para "'+ valorPesquisa.toUpperCase()+ '" </br></div>';
							$(idContainerResultados).html(conteudo);

							$(idContainerResultados + ' ul li ').click(function() {
								$(idCampoPesquisavel).val($(this).html());
								$(idContainerResultados).hide();
								selecionarItem(this);
							});
							
							$(idContainerResultados).show();

						} else {
							$(idContainerResultados).hide();
						}
					} else if(!contemErros && !contemLista){
						gerarListaMensagemAlerta(['Usuario pode nao estar logado no sistema']);
					} else if(contemErros){
						gerarListaMensagemErro(erros);
					}
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
	
	var selecionar = function(keyCode){
		
		var list = document.getElementById(idContainerResultados+'UL').getElementsByTagName('li');
		
		if(keyCode == 40 && ++count >= list.length){
			count = list.length - 1;
		} 
		else if(keyCode == 38 && --count <= 0){
			count = 0;
		}
		
		for(var i = 0; i < list.length; i++){
			if(keyCode == 13 && i == count){
				list[i].click();
				return;
			}
			
			if(i == count){
				list[i].style.color='white';
				list[i].style.background='#DCDCDC';
			} 
			else {
				list[i].style.color='';
				list[i].style.background='';
			}
		}
	};

	$(idCampoPesquisavel).keyup(function (e){
		
		if(e.keyCode != 40 && e.keyCode != 38 && e.keyCode != 13){
			pesquisar();
		} else {
			selecionar(e.keyCode);
		}
		
	});
	
	$(idCampoPesquisavel).click(function (e){
		$(idContainerResultados).hide();
	});
};
