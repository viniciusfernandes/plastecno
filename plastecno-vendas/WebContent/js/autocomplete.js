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
							
							var idSelect = idCampoPesquisavel+'Autocomplete';
							
							$(idSelect).remove();
							
							var conteudo = '<select size="'+TOTAL_REGISTROS+'" id="'+idSelect.replace("#", '')+'">'
							var array = new Array(); 
							for (var i = 0; i < TOTAL_REGISTROS; i++) {
								conteudo += '<option class="conbgn" value="'+resultado[i].valor+'">'+resultado[i].label+'</option>';
							}
							
							conteudo += "</select>"
							$(idCampoPesquisavel).after(conteudo);
							
							var posicao = $(idCampoPesquisavel).position();
							 $(idSelect).css({
							        position: "absolute",
							        top:  "30%",
							        left: "20%",
							        width: $(idCampoPesquisavel).css('width')
							 });
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

	$(idCampoPesquisavel).keyup(pesquisar);
};
