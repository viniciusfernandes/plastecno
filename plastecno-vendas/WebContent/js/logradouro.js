function inicializarBlocoLogradouro(urlTela) {
	
	var tabelaLogradouroHandler = new BlocoTabelaHandler(urlTela, 'Logradouro', 'tabelaLogradouro', 'bloco_logradouro');
	tabelaLogradouroHandler.setTotalColunas(11);
	
	tabelaLogradouroHandler.validarInclusaoRegistro(function (){
		if(isEmpty(document.getElementById('cep').value)) {
			throw "O CEP n�o pode estar em branco";
		}
	});
	
	tabelaLogradouroHandler.editarRegistro(function (linha){
		var doc = document;
		for ( var i = 0; i < this.TOTAL_COLUNAS; i++) {
			
			switch (i) {
				case 0:
					doc.getElementById('idLogradouro').value = linha.cells[i].innerHTML;
					break;
				case 1:
					doc.getElementById('tipoLogradouro').value = linha.cells[i].innerHTML;
					break;
				case 2:
					doc.getElementById('cep').value = linha.cells[i].innerHTML;
					break;
				case 3:
					doc.getElementById('endereco').value = linha.cells[i].innerHTML;
					break;
				case 4:
					doc.getElementById('numero').value = linha.cells[i].innerHTML;
					break;
				case 5:
					doc.getElementById('complemento').value = linha.cells[i].innerHTML;
					break;
				case 6:
					doc.getElementById('bairro').value = linha.cells[i].innerHTML;
					break;
				case 7:
					doc.getElementById('cidade').value = linha.cells[i].innerHTML;
					break;
				case 8:
					doc.getElementById('uf').value = linha.cells[i].innerHTML;
					break;
				case 9:
					doc.getElementById('pais').value = linha.cells[i].innerHTML;
					break;
				case 10:
					doc.getElementById('codificado').checked = 'false'== linha.cells[i].innerHTML ? false : true;
					break;
				default:
					break;
			}
		};
	});
	
	tabelaLogradouroHandler.incluirRegistro(function (ehEdicao, linha){
		var doc = document;
		var celula = null;
		for ( var i = 0; i < this.TOTAL_COLUNAS; i++) {
			celula = ehEdicao ? linha.cells[i] : linha.insertCell(i); 
			
			switch (i) {
				case 0:
					celula.style.display = 'none';
					break;
				case 1:
					celula.innerHTML = doc.getElementById('tipoLogradouro').value;
					break;
				case 2:
					celula.innerHTML = doc.getElementById('cep').value;
					break;
				case 3:
					celula.innerHTML = doc.getElementById('endereco').value;
					break;
				case 4:
					celula.innerHTML = doc.getElementById('numero').value;
					break;
				case 5:
					celula.innerHTML = doc.getElementById('complemento').value;
					break;
				case 6:
					celula.innerHTML = doc.getElementById('bairro').value;
					break;
				case 7:
					celula.innerHTML = doc.getElementById('cidade').value;
					break;
				case 8:
					celula.innerHTML = doc.getElementById('uf').value;
					break;
				case 9:
					celula.innerHTML = doc.getElementById('pais').value;
					break;
				case 10:
					celula.innerHTML = doc.getElementById('codificado').checked;
					celula.style.display = 'none';
					break;
				default:
					break;
			}
		}
	});
	
	tabelaLogradouroHandler.gerarParametros(function (indiceLinha, linha, nomeLista) {
		var parametros = '';
		var celulas = linha.cells;
		var TOTAL_CELULAS = celulas.length;
		for ( var j = 0; j < TOTAL_CELULAS; j++) {
			if (!isEmpty(celulas[j].innerHTML)) {
				switch (j) {
					case 0:
						parametros += '&'+nomeLista+'['+indiceLinha+'].id='+celulas[j].innerHTML;
						break;
					case 1:
						parametros += '&'+nomeLista+'['+indiceLinha+'].tipoLogradouro='+celulas[j].innerHTML;
						break;
					case 2:
						parametros += '&'+nomeLista+'['+indiceLinha+'].cep='+celulas[j].innerHTML;
						break;
					case 3:
						parametros += '&'+nomeLista+'['+indiceLinha+'].endereco='+celulas[j].innerHTML;
						break;
					case 4:
						parametros += '&'+nomeLista+'['+indiceLinha+'].numero='+celulas[j].innerHTML;
						break;
					case 5:
						parametros += '&'+nomeLista+'['+indiceLinha+'].complemento='+celulas[j].innerHTML;
						break;
					case 6:
						parametros += '&'+nomeLista+'['+indiceLinha+'].bairro='+celulas[j].innerHTML;
						break;
					case 7:
						parametros += '&'+nomeLista+'['+indiceLinha+'].cidade='+celulas[j].innerHTML;
						break;
					case 8:
						parametros += '&'+nomeLista+'['+indiceLinha+'].uf='+celulas[j].innerHTML;
						break;
					case 9:
						parametros += '&'+nomeLista+'['+indiceLinha+'].pais='+celulas[j].innerHTML;
						break;
					case 10:
						parametros += '&'+nomeLista+'['+indiceLinha+'].codificado='+celulas[j].innerHTML;
						break;
					default:
						break;
				}
				
			}
		}
		return parametros;
	});
	
	return tabelaLogradouroHandler;
};

function editarLogradouro(botao) {
	tabelaLogradouroHandler.editar(botao);
};

function removerLogradouro(botao) {
	tabelaLogradouroHandler.removerRegistro(botao);
};