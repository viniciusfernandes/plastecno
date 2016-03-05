function BlocoTabelaHandler (urlTela, nomeBloco, idTabela, idBlocoInput) {
	
	var doc = document;
	
	this.urlTela = urlTela;
	this.nomeBloco = nomeBloco;
	this.tabela = doc.getElementById(idTabela);
	this.idBlocoInput = idBlocoInput;
	this.linhaEditada = null;
	this.listaIdInputsDoBloco = null;
	this.TOTAL_COLUNAS = null;
	this.numeroLinhasCopiadas = 0;
	
	this.ajustarLinhasCopiadas = undefined;
	
	function ajustarLinhasCopiadasCallback (funcaoCallback){
		this.ajustarLinhasCopiadas = funcaoCallback;
	};
	
	this.setNumeroLinhasCopiadas= function (numeroLinhasCopiadas){
		return this.numeroLinhasCopiadas = numeroLinhasCopiadas;
	};
	
	this.removerRegistroCallback = function (funcaoCallback){
		this.removerRegistroCallback = funcaoCallback;
	};  
	
	this.editarRegistro = function (funcaoEdicaoRegistro){
		this.editarRegistro = funcaoEdicaoRegistro;
	};
	
	this.resetBlocoInput = function (funcao){
		this.resetBlocoInput = funcao;
	};
	
	this.gerarParametroInputsBloco = function () {
		return '&'+$('#bloco_logradouro').serialize();
	};
	
	this.setListaIdInputsDoBloco= function (listaIdInputsDoBloco) {
		this.listaIdInputsDoBloco = listaIdInputsDoBloco;		
	};

	this.setTotalColunas = function (totalColunas) {
		this.TOTAL_COLUNAS = totalColunas;
	};
	
	this.validarInclusaoRegistro = function(funcaoValidacao){
		this.validarInclusaoRegistro = funcaoValidacao;
	};
	
	this.incluirRegistro = function (funcaoInclusaoRegistro){
		this.incluirRegistro= funcaoInclusaoRegistro;
	};
	
	this.adicionar = function () {
		var ehAdicaoValida = true;
		
		// verificando se foi definida uma validacao para a inclusao de registro
		if (this.validarInclusaoRegistro != undefined) {
			try {
				this.validarInclusaoRegistro();
			} catch (err) {
				ehAdicaoValida = false;
			}			
		}
		
		if (ehAdicaoValida) {
			var ehEdicao = this.linhaEditada != null;
			var linha = null;
			if (ehEdicao) {
				linha = this.linhaEditada;
			} else {
				/* 
				 * Eh muito importante inserir as linha no tbody da tabela pois todo o CSS
				 * foi feito apontando para a hierarquia "tbody tr td"
				 */
				linha = this.tabela.tBodies[0].insertRow(0);
			}
			
			this.incluirRegistro(ehEdicao, linha);
			
			if (!ehEdicao) {
				var botoesAcoes = '<input type="button" title="Editar Dados do '+this.nomeBloco+ '" value="" class="botaoEditar" onclick="editar'+this.nomeBloco+'(this);"/>';
				botoesAcoes += '<input type="button" title="Remover Dados do '+this.nomeBloco+ '" value="" class="botaoRemover" onclick="remover'+this.nomeBloco+'(this);"/>';
				linha.insertCell(this.TOTAL_COLUNAS).innerHTML = botoesAcoes;
				
				// Essa condicao surgiu para incluir um mesmo logradouro de uma unica vez, e depois o usuario edita, proporcionando agilidade no cadastro.
				var linhas = new Array();
				linhas[0] = linha;
				
				var clone = null;
				for (var i = 1; i < this.numeroLinhasCopiadas; i++) {
					clone = linha.cloneNode(true);
					this.tabela.appendChild(clone);
					linhas[i] = clone;
				}
				
				if(this.ajustarLinhasCopiadas != undefined){
					this.ajustarLinhasCopiadas(linhas);
				}
			}	
			
			this.limparBlocoInput();	
			
			if (this.resetBlocoInput != undefined) {
				this.resetBlocoInput();	
			}	
		}
	};
	
	this.removerRegistro = function (botao) {
		var handler = this;
		inicializarModalConfirmacao({
			mensagem: 'Essa acao nao podera ser desfeita. Voce tem certeza de que deseja REMOVER esse item?',
			confirmar: function(){
				var linha = $(botao).closest("tr")[0];
				var idRegistro = linha.cells[0].innerHTML;			
				var registroRemovido = true;
				var resposta = null;
				
				// Removendo apenas os contatos que existem na base, ou seja, possuem um ID.
				if (!isEmpty(idRegistro)) {
						var request = $.ajax({
							async:   false,
							type: 'post',
							url: handler.urlTela+'/'+handler.nomeBloco.toLowerCase()+'/remocao/'+idRegistro
						});
						
						request.done(function (response){
							resposta = response; 
						});
						
						request.fail(function(request, status) {
							registroRemovido = false;
							alert('Falha na remocao do registro => Status da requisicao: '+status);
						});
				}
				
				if (registroRemovido) {
					handler.tabela.deleteRow(linha.rowIndex);
					handler.limparBlocoInput();
					
					if (handler.removerRegistroCallback != undefined) {
						handler.removerRegistroCallback(resposta);
					}
				}
			}
		});
	};
	
	this.gerarParametros = function (funcaoGeracaoParametros) {
		this.gerarParametros = funcaoGeracaoParametros;
	};
	
	this.editar = function (botaoEdicao) {
		this.linhaEditada = $(botaoEdicao).closest("tr")[0];
		this.editarRegistro(this.linhaEditada);
	};
	
	this.gerarListaParametro = function (nomeLista){
		
		var parametros = '';
		if(this.tabela == null){
			return parametros;
		}
		var linhas = this.tabela.rows;
		
		// Vamo iniciar de i=1 pois devemos pular o header da tabela
		var NUMERO_REGISTROS = linhas.length;
		for ( var i = 1; i < NUMERO_REGISTROS; i++) {
			// Estamos subtraindo 1 do indice da lista pois desconsideramos o header da tabela
			parametros += this.gerarParametros(i-1, linhas[i], nomeLista);
		}
		return parametros;
	};
	
	this.limparBlocoInput = function () {
		$('#'+this.idBlocoInput+' input:text').val('');
		$('#'+this.idBlocoInput+' input:hidden').val('');
		$('#'+this.idBlocoInput+' select').val('');
		this.linhaEditada = null;
	};
	
	this.inicializarBotoes = function (){
		var tabelaHandler = this;
		var botaoIncluir = doc.getElementById('botaoIncluir'+this.nomeBloco);
		if (botaoIncluir != null) {
			botaoIncluir.onclick = function() { tabelaHandler.adicionar(); };	
		}
		
		var botaoLimpar = doc.getElementById('botaoLimpar'+this.nomeBloco);
		if (botaoLimpar != null) {
			botaoLimpar.onclick = function() { tabelaHandler.limparBlocoInput(); };	
		}
	};
	this.inicializarBotoes();
};