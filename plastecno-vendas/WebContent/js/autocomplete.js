var autocompletar = function(configuracao) {
	var url = configuracao.url;
	var idCampoPesquisavel = '#' + configuracao.campoPesquisavel;
	var parametro = configuracao.parametro;
	var idContainerResultados = '#' + configuracao.containerResultados;
	var selecionarItem = configuracao.selecionarItem;
	var gerarVinculo = configuracao.gerarVinculo;
	var idSelect = idCampoPesquisavel+'Autocomplete';
	
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
						$(idSelect).remove();						
						
						if (TOTAL_REGISTROS > 0) {
							
							var conteudo = '<select size="'+TOTAL_REGISTROS+'" id="'+idSelect.replace("#", '')+'">'
							var array = new Array(); 
							for (var i = 0; i < TOTAL_REGISTROS; i++) {
								conteudo += '<option class="conbgn" value="'+resultado[i].valor+'">'+resultado[i].label+'</option>';
							}
							
							conteudo += "</select>"
							$(idCampoPesquisavel).after(conteudo);
							$(idCampoPesquisavel).css('position', 'absolute');
							
							var top = $(idCampoPesquisavel).position().top+$(idCampoPesquisavel).height() + 1;
							 $(idSelect).css({
							        position: "absolute",
							        top:  top+'px',
							        zIndex: 1
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
	
	$(idCampoPesquisavel).keydown(function(e){
		if(e.keyCode == 40){
			$(idSelect).focus();
			$(idSelect).select();
		}
	});
};
