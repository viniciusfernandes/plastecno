var autocompletar = function(configuracao) {
	var url = configuracao.url;
	var idCampoPesquisavel = '#' + configuracao.campoPesquisavel;
	var parametro = configuracao.parametro;
	var idContainerResultados = '#' + configuracao.containerResultados;
	var selecionarItem = configuracao.selecionarItem;
	var gerarVinculo = configuracao.gerarVinculo;
	var count = 0;
	var TOTAL_REGISTROS = 0;
	
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
						TOTAL_REGISTROS = resultado.length;
						count = 0;
						
						if (TOTAL_REGISTROS > 0) {
							var conteudo = '<ul>';
							for (var x = 0; x < TOTAL_REGISTROS; x++) {
								conteudo += '<li class="conbgn" id="suggestion'
										+ (x+1) + '">'
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
		var selecionar = function(){
			for(var i = 1; i <= TOTAL_REGISTROS; i++){
				if(i==count){
					document.getElementById('suggestion'+i).style.color='white';
					document.getElementById('suggestion'+i).style.background='black';
				} 
				else {
					document.getElementById('suggestion'+i).style.color='black';
					document.getElementById('suggestion'+i).style.background='white';
				}
			}
		};
		
		if(e.keyCode == 40){
			if(++count > TOTAL_REGISTROS){
				count = TOTAL_REGISTROS;
			}
			
			selecionar();
		} 
		else if(e.keyCode == 38){
			
			if(--count < 1){
				count = 1;
			}
			
			selecionar();
		}
		else if (e.keyCode == 13){
			$('#suggestion'+count).click();
			$(idContainerResultados).hide();
		}
		else {
			pesquisar();
		}
		
	});
};
