function editarTabela(configJson){
	var config = configJson;
	var linha = null;

	function init(){
		var botaoInserir = document.getElementById(config.idBotaoInserir);
		botaoInserir.onclick = function(){
			var isEdicao = linha != null;
			var tabela = document.getElementById(config.idTabela);
			linha = isEdicao ? linha : tabela.tBodies[0].insertRow(0);
			var campos = config.campos;
			var campo = null;
			var cel = null;
			var max = campos.length;
			for (var i = 0; i <= max; i++) {
				cel = isEdicao ? linha.cells[i] : linha.insertCell(i);
				if(i < max){
					campo = document.getElementById(campos[i]);
					cel.innerHTML = campo.value;
					campo.value = '';
				} else if(i == max && !isEdicao){
					var btRemove = document.createElement('input');
					btRemove.type = 'button';
					btRemove.title = 'Remover Registro';
					btRemove.classList.add('botaoRemover');
					btRemove.onclick = function(){
						var l = btRemove.parentNode.parentNode;
						if(linha != null && linha.rowIndex == l.rowIndex){
							linha = null;
						}
						tabela.deleteRow(l.rowIndex);
					};
					
					var btEdit = document.createElement('input');
					btEdit.type = 'button';
					btEdit.title = 'Editar Registro';
					btEdit.classList.add('botaoEditar');
					btEdit.onclick = function(){
						linha = btEdit.parentNode.parentNode;
						var celulas = linha.cells;
						var campos = config.campos;
						for (var i = 0; i <= campos.length; i++) {
							document.getElementById(campos[i]).value = celulas[i].innerHTML;
						}
					};
					cel.appendChild(btRemove);
					cel.appendChild(btEdit);
				}
			}
			linha = null;
		};
	};
	
	init();
};