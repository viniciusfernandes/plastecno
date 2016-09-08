function editarTabela(configJson) {
	var config = configJson;
	var linha = null;
	var idLinha = '';

	function gerarIdLinhaSequencial(linha) {
		// Gerando o id da linha que sera utilizado para gerar os inputs que
		// serao enviados para o servidor
		if (isEmpty(linha.id)) {
			var linhas = document.getElementById(config.idTabela).rows;
			var ids = new Array();
			for (var i = 0; i < linhas.length; i++) {
				ids[i] = linhas[i].id;
			}
			ids.sort();
			var id = ids.length > 0 ? ids[ids.length - 1] : 0;
			// Verificando condicao de existencia de algum id
			if (isEmpty(id)) {
				linha.id = 0;
			} else {
				linha.id = parseInt(id) + 1;
			}
		}
	}
	;

	document.getElementById(config.idBotaoInserir).onclick = function() {

		if (config.onValidar != undefined && config.onValidar != null
				&& !config.onValidar()) {
			return;
		}

		var isEdicao = linha != null;
		var tabela = document.getElementById(config.idTabela);
		linha = isEdicao ? linha : tabela.tBodies[0].insertRow(-1);
		linha.id = idLinha;

		var campos = config.campos;
		var campo = null;
		var cel = null;
		var max = campos.length;
		for (var i = 0; i <= max; i++) {
			cel = isEdicao ? linha.cells[i] : linha.insertCell(i);
			// Bloco de inclusao dos valores das celulas
			if (i < max) {
				campo = document.getElementById(campos[i]);
				cel.innerHTML = campo.value;
				campo.value = '';
			} else if (i == max && !isEdicao) { // Bloco de inclusao dos botoes de acoes editar e remover
				var btRemove = document.createElement('input');
				btRemove.type = 'button';
				btRemove.title = 'Remover Registro';
				btRemove.classList.add('botaoRemover');
				btRemove.onclick = function() {
					var l = btRemove.parentNode.parentNode;
					if (linha != null && linha.rowIndex == l.rowIndex) {
						linha = null;
						idLinha = '';
					}
					tabela.deleteRow(l.rowIndex);

					if (config.onRemover != undefined) {
						config.onRemover(l);
					}
				};

				var btEdit = document.createElement('input');
				btEdit.type = 'button';
				btEdit.title = 'Editar Registro';
				btEdit.classList.add('botaoEditar');
				btEdit.onclick = function() {
					linha = btEdit.parentNode.parentNode;
					idLinha = linha.id;
					var celulas = linha.cells;
					var campos = config.campos;
					var campo = null;
					for (var i = 0; i <= campos.length; i++) {
						campo = document.getElementById(campos[i]);
						if (campo != null && campo != undefined) {
							campo.value = celulas[i].innerHTML;
						}
					}

					if (config.onEditar != undefined) {
						config.onEditar(linha);
					}
				};
				cel.appendChild(btRemove);
				cel.appendChild(btEdit);
			}
		}
		// A geracao dos ids deve preceder onInserir para que o linha ja conteha o id
		if (config.idLinhaSequencial != undefined && config.idLinhaSequencial) {
			gerarIdLinhaSequencial(linha);
		}
		
		if (config.onInserir != undefined) {
			var indiceLinha = config.onInserir(linha);
		}
		linha = null;
		idLinha = '';
	};

	this.inserirLinha = function(id) {
		idLinha = id;
		document.getElementById(config.idBotaoInserir).click();
	};
};