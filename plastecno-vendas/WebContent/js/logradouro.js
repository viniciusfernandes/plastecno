function inicializarBlocoLogradouro(urlTela) {
	
	var tabelaLogradouroHandler = new BlocoTabelaHandler(urlTela, 'Logradouro', 'tabelaLogradouro', 'bloco_logradouro');
	tabelaLogradouroHandler.setTotalColunas(11);
	
	tabelaLogradouroHandler.validarInclusaoRegistro(function (){
		if(isEmpty(document.getElementById('cep').value)) {
			throw "O CEP nao pode estar em branco";
		}
	});
	
	tabelaLogradouroHandler.editarRegistro(function (linha){
		var doc = document;
		doc.getElementById('idLogradouro').value = linha.cells[0].innerHTML;
		doc.getElementById('tipoLogradouro').value = linha.cells[1].innerHTML;
		doc.getElementById('cep').value = linha.cells[2].innerHTML;
		doc.getElementById('endereco').value = linha.cells[3].innerHTML;
		doc.getElementById('numero').value = linha.cells[4].innerHTML;
		doc.getElementById('complemento').value = linha.cells[5].innerHTML;
		doc.getElementById('bairro').value = linha.cells[6].innerHTML;
		doc.getElementById('cidade').value = linha.cells[7].innerHTML;
		doc.getElementById('uf').value = linha.cells[8].innerHTML;
		doc.getElementById('pais').value = linha.cells[9].innerHTML;
		doc.getElementById('codigoMunicipio').value = linha.cells[10].innerHTML;
		//doc.getElementById('codificado').checked = 'false'== linha.cells[11].innerHTML ? false : true;
	});
	
	tabelaLogradouroHandler.incluirRegistro(function (ehEdicao, linha){
		var doc = document;
		var celula = null;
			
		celula = ehEdicao ? linha.cells[0] : linha.insertCell(0); 
		celula.style.display = 'none';

		celula = ehEdicao ? linha.cells[1] : linha.insertCell(1); 
		celula.innerHTML = doc.getElementById('tipoLogradouro').value;

		celula = ehEdicao ? linha.cells[2] : linha.insertCell(2); 
		celula.innerHTML = doc.getElementById('cep').value;
					
		celula = ehEdicao ? linha.cells[3] : linha.insertCell(3); 
		celula.innerHTML = doc.getElementById('endereco').value;
					
		celula = ehEdicao ? linha.cells[4] : linha.insertCell(4); 
		celula.innerHTML = doc.getElementById('numero').value;

		celula = ehEdicao ? linha.cells[5] : linha.insertCell(5); 
		celula.innerHTML = doc.getElementById('complemento').value;
					
		celula = ehEdicao ? linha.cells[6] : linha.insertCell(6); 
		celula.innerHTML = doc.getElementById('bairro').value;

		celula = ehEdicao ? linha.cells[7] : linha.insertCell(7); 
		celula.innerHTML = doc.getElementById('cidade').value;

		celula = ehEdicao ? linha.cells[8] : linha.insertCell(8); 
		celula.innerHTML = doc.getElementById('uf').value;

		celula = ehEdicao ? linha.cells[9] : linha.insertCell(9); 
		celula.innerHTML = doc.getElementById('pais').value;
		
		celula = ehEdicao ? linha.cells[10] : linha.insertCell(10); 
		celula.innerHTML = doc.getElementById('codigoMunicipio').value;
					
		//celula = ehEdicao ? linha.cells[11] : linha.insertCell(11); 
		//celula.innerHTML = doc.getElementById('codificado').checked;
		//celula.style.display = 'none';
	});
	
	tabelaLogradouroHandler.gerarParametros(function (indiceLinha, linha, nomeLista) {
		var parametros = '';
		var celulas = linha.cells;
		parametros += '&'+nomeLista+'['+indiceLinha+'].id='+(!isEmpty(celulas[0].innerHTML)?celulas[0].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].tipoLogradouro='+(!isEmpty(celulas[1].innerHTML)?celulas[1].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].cep='+(!isEmpty(celulas[2].innerHTML)?celulas[2].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].endereco='+(!isEmpty(celulas[3].innerHTML)?celulas[3].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].numero='+(!isEmpty(celulas[4].innerHTML)?celulas[4].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].complemento='+(!isEmpty(celulas[5].innerHTML)?celulas[5].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].bairro='+(!isEmpty(celulas[6].innerHTML)?celulas[6].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].cidade='+(!isEmpty(celulas[7].innerHTML)?celulas[7].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].uf='+(!isEmpty(celulas[8].innerHTML)?celulas[8].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].pais='+(!isEmpty(celulas[9].innerHTML)?celulas[9].innerHTML:'');
		parametros += '&'+nomeLista+'['+indiceLinha+'].codigoMunicipio='+(!isEmpty(celulas[10].innerHTML)?celulas[10].innerHTML:'');
		//parametros += '&'+nomeLista+'['+indiceLinha+'].codificado='+(!isEmpty(celulas[11].innerHTML)?celulas[11].innerHTML:'');
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