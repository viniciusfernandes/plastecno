function isAnterior(dataInicial, dataFinal) {
	return !isEmpty(dataInicial) && !isEmpty(dataFinal) 
		&& gerarData(dataInicial) <= gerarData(dataFinal);
} 

function removerNaoDigitos(listaId) {
	var totalElementos = listaId.length;
	var campo = null;
	for (var i = 0; i < totalElementos; i++) {
		campo = $('#'+listaId[i]);
		campo.val(campo.val().replace(/\D/g, ''));
	}
}

function toUpperCaseInput (){
	$("input, textArea").each(function(){
		if(!$(this).hasClass('uppercaseBloqueado')){
			$(this).val($(this).val().toUpperCase());
		}
	});
}

function toLowerCaseInput (){
	$("input, textArea").each(function(){
		if($(this).hasClass('apenasLowerCase')){
			$(this).val($(this).val().toLowerCase());
		}
	});
}

function inicializarPaginador(inicializarFiltro, paginaSelecionada, totalPaginas) {
	$("#paginador").paginate({
		count 		: totalPaginas,
		start 		: paginaSelecionada,
		display     : 5,
		border					: true,
		border_color			: '#DCEAE7',
		text_color  			: '#9EAA9E',
		background_color    	: '#E1E8E5',	
		border_hover_color		: '#9EAA9E',
		text_hover_color  		: 'black',
		background_hover_color	: '#C0E0D8', 
		rotate      : true,
		images		: false,
		mouse		: 'press'
	});
	
	$("#paginador a").click(function() {
		var pagina = -1;
		if ($(this).hasClass('jPag-first')) {
			pagina = 1;
		} else if ($(this).hasClass('jPag-last')) {
			pagina = totalPaginas;
		} else {
			pagina = $(this).html();
		}
		
		toUpperCaseInput();
		inicializarFiltro();
		var input = $("<input>").attr("type", "hidden").attr("name", "paginaSelecionada").val(pagina);
		$('#formPesquisa').append($(input));
		$('#formPesquisa').submit();
	});
}

function inicializarPesquisaCEP(contexto) {
	$('#cep').attr('maxlength', 8);
	
	$('#cep').blur(function () {
		var cep = $('#cep').val(); 
		if (cep == undefined || cep == null || cep.trim().length == 0) {
			$('#endereco').val('');
			$('#bairro').val('');
			$('#cidade').val('');
			$('#uf').val('');
			$('#pais').val('');
			//desabilitarCamposEndereco(true);
			return;
		}
		
		var request = $.ajax({
							type: "get",
							url: contexto+"cep/endereco",
							data: 'cep='+$('#cep').val(),
						});
		request.done(function(response) {
			var endereco = response.endereco;
			$('#endereco').val(endereco.descricao);
			$('#bairro').val(endereco.bairro.descricao);
			$('#cidade').val(endereco.cidade.descricao);
			$('#uf').val(endereco.cidade.uf);
			$('#pais').val(endereco.cidade.pais.descricao);
			
			//var isEnderecoExistente = endereco.cidade.id != null;
			//desabilitarCamposEndereco(isEnderecoExistente);
		});
		
		request.fail(function(request, status) {
			alert('Falha na busca do CEP: ' + $('#cep').val()+' => Status da requisicao: '+status);
		});
	});
}

function habilitar(seletorCampo, habilitado) {
	if (habilitado) {
		$(seletorCampo).removeClass('desabilitado');
		$(seletorCampo).attr("disabled", false);
	} else {
		$(seletorCampo).addClass('desabilitado');
		$(seletorCampo).attr("disabled", true);	
	}
}

function isEmpty (string) {
	return string == null || /^\s*$/.test(string);
}


function Repositorio () {
	this.chaves = new Array();
	this.valores = new Array();

	this.put = function (chave, valor) {
		var contem = false;
		var totalElementos = this.chaves.length;
		for (var i = 0; i< totalElementos; i++) {
			if (this.chaves[i] === chave) {
				this.valores[i] = valor;
				contem = true;
				break;
			}
		}

		if (!contem) {
			this.chaves[this.chaves.length] = chave;
			this.valores[this.valores.length] = valor;
		}
	};

	this.clear = function () {
		this.valores = new Array();
		this.chaves = new Array();
	};

	this.size = function () {
		return this.chaves.length;
	};

	this.remove = function (chave) {
		var totalElementos = this.chaves.length;
		if (totalElementos > 0){
			for (var i = 0; i< totalElementos; i++) {
				if (this.chaves[i] == chave) {
					this.valores.splice(i, 1);
					this.chaves.splice(i, 1);
					break;
				}
			}
		} 
		
	};
	
	this.get = function (chave) {
		var totalElementos = this.chaves.length; 
		if (totalElementos == 0){
			return null;
		} 
		
		for (var i = 0; i< totalElementos; i++) {
			if (this.chaves[i] == chave) {
				return this.valores[i]; 
			}
		}
		
		return null;
	}; 
	
	this.getValores = function () {
		return this.valores;
	};	
	
	this.getListParameter = function(){
		var parametros = '';
		var totalElementos = this.valores.length;
		for (var i = 0; i < totalElementos; i++) {
			try {
				parametros += this.valores[i].toListParameter(i);	
			} catch (e) {
				alert("Os elementos do repositorio devem implementar o metodo \"toListParameter(int)\" que deve retornar uma string. Por exemplo: \"listaContato[int]=valor\"");
			} 
			
		}
		
		return parametros
	;};
};

function serializar(listaCampos) {
	var parametros = '';
	if (listaCampos != null || listaCampos.length > 0) {
		for ( var i = 0; i < listaCampos.length; i++) {
			parametros += '&'+listaCampos[i].name+"="+listaCampos[i].value;
		}
	} 
	return parametros.replace('&', '');
};

function serializarBloco(idBloco) {
	return serializarBlocoDisabled(idBloco, true);
};

function serializarBlocoDisabled(idBloco, verificarDisabled) {
	var parametros = '';
	idBloco = '#'+idBloco;
	var inputs = $(idBloco+' :input[type=text], '+idBloco+' select, '+ idBloco+' :input[type=radio], '+ idBloco+' :input[type=checkbox], '+ idBloco+' :input[type=hidden]');
	$(inputs).each(function () {
		
		if(verificarDisabled && !isEmpty(this.value) && !$(this).attr('disabled')){
			parametros += '&'+this.name+"="+this.value;
		}
		else if(!isEmpty(this.value)){
			parametros += '&'+this.name+"="+this.value;
		}
	});
	return parametros;
};

function inicializarCheckbox () {
	$(':checkbox').click(function() {
		$(this).val($(this).prop('checked'));
	});
};

function limparBlocoInput(blocoId) {
	$('#'+blocoId+' :input').each(function() {
		$(this).val('');
	});
}

function scrollTo(idAncora) {
	var ancora = document.getElementById(idAncora);
	if (ancora != null) {
			ancora.scrollIntoView(true);	
	}		
};

function inicializarLimpezaFormulario(nomeTela) {
	$('#botaoLimpar').click(function() {
		$('#'+nomeFormulario+' :input').each(function() {
			$(this).val('');
		});
	});
}

function desativarRegistro (nomeExibicao, descricaoRegistro, idRegistro) {
	var remocaoConfirmada = confirm("Voce tem certeza de que deseja desativar o "+nomeExibicao+" \""+descricaoRegistro+"\"?");
	if (remocaoConfirmada) {
		var form = $(this).closest('form').
		form.attr("action","id="+idRegistro);
		form.submit();
	}
}

function limparFormulario () {
	$('#formVazio').submit();
}

function limparComboBox(idCombo) {
	var combo = $('#'+idCombo);
	combo.empty();
	combo.append($('<option value="">&lt&lt SELECIONE &gt&gt</option>'));
}

function gerarListaMensagemErro (listaMensagem) {
	gerarListaMensagem(listaMensagem, 'mensagemErro');
};

function gerarListaMensagemAlerta (listaMensagem) {
	gerarListaMensagem(listaMensagem, 'mensagemAlerta');
};

function gerarListaMensagemSucesso (listaMensagem) {
	gerarListaMensagem(listaMensagem, 'mensagemSucesso');
};

function gerarListaMensagem (listaMensagem, cssMensagem) {
	
	if (listaMensagem == undefined || listaMensagem == null) {
		return;
	}
	
	var li = '';
	var TOTAL_MENSAGENS = listaMensagem.length;
	for(var i = 0; i < TOTAL_MENSAGENS; i++) {
		li += '<li>'+listaMensagem[i]+'</li>';
	}
	
	// temos que remover as classes anteriores para exibir apenas a classe desejada
	$('#bloco_mensagem').removeClass('mensagemErro mensagemAlerta mensagemSucesso');
	$('#bloco_mensagem').addClass(cssMensagem);
	$('#bloco_mensagem ul').html(li);
	$('#bloco_mensagem').show();
	scrollTo('topo');
};

function gerarData(data) {
	if (isEmpty(data)) {
		return new Date();
	}
	var array = data.split('/');
	return new Date(array[2], array[1], array[0], 0, 0, 0, 0);
};

function gerarListaParametroId (listaId, nomeLista){
	
	var parametros = '';
	
	// Vamo iniciar de i=1 pois devemos pular o header da tabela
	var NUMERO_REGISTROS = listaId.length;
	var index = NUMERO_REGISTROS - 1;
	for (var i = 0; i < NUMERO_REGISTROS; i++) {
		// Estamos subtraindo 1 do indice da lista pois desconsideramos o header da tabela
		parametros += nomeLista+"[]="+listaId[i];
		if(i < index){
			parametros += '&';	
		}
	}
	return parametros;
};

function submeterForm(botao){
	var parametros = $('#formPesquisa').serialize();
	var form = $(botao).closest('form');
	var action = $(form).attr('action')+'?'+parametros;
	$(form).attr('action', action).submit();
};

function serializarForm(idForm){
	return $("#"+idForm+" :input[value!='']").serialize();
}

function serializarFormPesquisa(){
	return serializarBloco('formPesquisa');
}