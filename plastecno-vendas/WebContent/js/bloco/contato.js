function gerarArrayTelefone(telefoneFormatado) {
	// removendo a formatacao do telefone contido na celuala da tabela
	var dddTelefoneRamalFax = telefoneFormatado.replace(/\s\/\s/g, ' ').replace(')', '').replace('(', '');
	// criando um array contendo os ddi, ddd, tel, ramal e fax
	return dddTelefoneRamalFax.split(' ');
}

function recuperarParamentrosIdContatoRemocao(listaId) {
	var parametros = "";
	for ( var i = 0; i < listaId.length; i++) {
		parametros += "&listaContatoRemocao["+i+"]="+listaId[i];
	}
	return parametros;
}

function formatarCamposTelefone (ddi, ddd, telefone, ramal, fax) {
	if (isEmpty(ddi) && isEmpty(ddd) && isEmpty(telefone) && isEmpty(ramal) && isEmpty(fax)) {
		return '';
	}
	var dddTelefoneRamalFax = null;
	dddTelefoneRamalFax = "("+ ddi +" / "+ ddd +") ";
	dddTelefoneRamalFax += telefone +" / ";
	dddTelefoneRamalFax += ramal +" / ";
	dddTelefoneRamalFax += fax;
	return dddTelefoneRamalFax;
}

function gerarTelefone(isSecundario) {
	var secundario = isSecundario ? 'Secundario' : '';
	var doc = document;
	
	var ddi = doc.getElementById('contato_ddi'+secundario).value;
	var ddd = doc.getElementById('contato_ddd'+secundario).value;
	var telefone = doc.getElementById('contato_telefone'+secundario).value;
	var ramal = doc.getElementById('contato_ramal'+secundario).value;
	var fax = doc.getElementById('contato_fax'+secundario).value;
	
	return formatarCamposTelefone(ddi, ddd, telefone, ramal, fax);
}

function preencherCamposTelefone(telefoneFormatado, isSecundario) {
	var dddTelefoneRamalFax = gerarArrayTelefone(telefoneFormatado);
	
	var doc = document;
	
	var secundario = isSecundario ? 'Secundario' : '';
	var ddi = doc.getElementById('contato_ddi'+secundario);
	var ddd = doc.getElementById('contato_ddd'+secundario);
	var telefone = doc.getElementById('contato_telefone'+secundario);
	var ramal = doc.getElementById('contato_ramal'+secundario);
	var fax = doc.getElementById('contato_fax'+secundario);
	
	ddi.value = dddTelefoneRamalFax[0];
	ddd.value = dddTelefoneRamalFax[1];
	telefone.value = dddTelefoneRamalFax[2];
	ramal.value = dddTelefoneRamalFax[3];
	fax.value = dddTelefoneRamalFax[4];
}

function inserirMascaraCamposContato() {
	inserirMascaraNumerica('contato_ddi', '999');
	inserirMascaraNumerica('contato_ddd', '999');
	inserirMascaraNumerica('contato_telefone', '999999999');
	inserirMascaraNumerica('contato_ramal', '99999');
	inserirMascaraNumerica('contato_fax', '99999999');
	
	inserirMascaraNumerica('contato_ddiSecundario', '999');
	inserirMascaraNumerica('contato_dddSecundario', '999');
	inserirMascaraNumerica('contato_telefoneSecundario', '999999999');
	inserirMascaraNumerica('contato_ramalSecundario', '99999');
	inserirMascaraNumerica('contato_faxSecundario', '99999999');
};

function gerarParametroTelefone(indiceLinha, nomeLista, telefoneFormatado, isSecundario) {
	var dddTelefoneRamalFax = gerarArrayTelefone(telefoneFormatado);
	var secundario = isSecundario ? 'Secundario' : '';
	
	var parametros = '';
	parametros += '&'+nomeLista+'['+indiceLinha+'].ddi'+secundario+'='+dddTelefoneRamalFax[0];
	parametros += '&'+nomeLista+'['+indiceLinha+'].ddd'+secundario+'='+dddTelefoneRamalFax[1];
	parametros += '&'+nomeLista+'['+indiceLinha+'].telefone'+secundario+'='+dddTelefoneRamalFax[2];
	parametros += '&'+nomeLista+'['+indiceLinha+'].ramal'+secundario+'='+dddTelefoneRamalFax[3];
	parametros += '&'+nomeLista+'['+indiceLinha+'].fax'+secundario+'='+dddTelefoneRamalFax[4];
	
	return parametros;
};

function inicializarBlocoContato(urlTela) {

	var tabelaContatoHandler = new BlocoTabelaHandler(urlTela, 'Contato', 'tabelaContatos', 'bloco_contato');
	tabelaContatoHandler.setTotalColunas(5);
	
	inserirMascaraCamposContato();
	
	tabelaContatoHandler.validarInclusaoRegistro(function (){
		var nome = document.getElementById('contato_nome').value.toUpperCase();
		if(isEmpty(nome)) {
			throw "O nome do contato não pode estar em branco";
		}			
	});
	
	tabelaContatoHandler.gerarParametros(function (indiceLinha, linha, nomeLista) {
		var parametros = '';
		
		var celulas = linha.cells;
		var TOTAL_CELULAS = celulas.length -1;
		for (var j = 0; j < TOTAL_CELULAS; j++) {
			if (!isEmpty(celulas[j].innerHTML)) {
				
				switch (j) {
					case 0:
						parametros += '&'+nomeLista+'['+indiceLinha+'].'+'id'+'='+celulas[j].innerHTML;
						break;
					case 1:
						parametros += '&'+nomeLista+'['+indiceLinha+'].'+'nome'+'='+celulas[j].innerHTML;
						break;
					case 2:
						parametros += '&'+nomeLista+'['+indiceLinha+'].'+'email'+'='+celulas[j].innerHTML;
						break;
					case 3:
						parametros += gerarParametroTelefone(indiceLinha, nomeLista, celulas[j].innerHTML, false);
						break;
					case 4:
						parametros += gerarParametroTelefone(indiceLinha, nomeLista, celulas[j].innerHTML, true);
						break;
				}
			}
		}
		return parametros; 
	});
	
	tabelaContatoHandler.incluirRegistro(function (ehEdicao, linha){
		var celula = null;
		var doc = document;
		
		for (var i = 0; i < this.TOTAL_COLUNAS; i++) {
			celula = ehEdicao ? linha.cells[i] : linha.insertCell(i); 
			switch (i) {
				case 0:
					celula.style.display = 'none';
					break;
				case 1:
					celula.innerHTML = doc.getElementById('contato_nome').value.toUpperCase();
					break;
				case 2:
					celula.innerHTML = doc.getElementById('contato_email').value.toLowerCase();
					break;
				case 3:
					celula.innerHTML = gerarTelefone(false);
					break;
				case 4:
					celula.innerHTML = gerarTelefone(true);
					break;
			}
		}
	});
	
	tabelaContatoHandler.editarRegistro(function(linha){
		
		var celula = null;
		var doc = document;
		
		for (var i = 0; i < this.TOTAL_COLUNAS; i++) {
			celula =  linha.cells[i];
			
			if (!isEmpty(celula.innerHTML)) {
				switch (i) {
					case 0:
						doc.getElementById('contato_idContato').value = celula.innerHTML;
						break;
					case 1:
						doc.getElementById('contato_nome').value = celula.innerHTML;
						break;
					case 2:
						doc.getElementById('contato_email').value = celula.innerHTML;
						break;
					case 3:
						// preencendo o telefone primario
						preencherCamposTelefone(celula.innerHTML, false);
						break;
					case 4:
						// preencendo o telefone secundario
						preencherCamposTelefone(celula.innerHTML, true);
						break;
				}
			}
		}
	});
	
	tabelaContatoHandler.resetBlocoInput(function() {
		var doc = document;
		doc.getElementById('contato_ddi').value = '55';
		doc.getElementById('contato_ddiSecundario').value = '55';
	});
		
	return tabelaContatoHandler;
}
function editarContato(botao) {
	tabelaContatoHandler.editar(botao);
};

function removerContato (botao) {
	tabelaContatoHandler.removerRegistro(botao);
};