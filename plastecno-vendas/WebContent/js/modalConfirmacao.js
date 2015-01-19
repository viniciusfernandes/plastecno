function inicializarModalConfirmacao (config){
	var idModal = '#'+config.idModal;
	var idButton = '#'+config.idButton;
	var confirmar = config.confirmar;
	var mensagem = config.mensagem;
	
	var modal = $(idModal).dialog({
		autoOpen: false,
		modal: true,
		resizable: false,
		closeText: 'Fechar',
		position: { my: 'left+40% top+30%', of: 'button' },
		buttons: {
			"Confirmar": function() {
				if(confirmar != undefined){
					confirmar();
				}
				$(this).dialog("close");
			},
			"Cancelar": function() {
				$(this).dialog("close");
			}
		}
	});
	
	$(modal).html(mensagem);
	$(idButton).click(function () {
		$(modal).dialog('open');
	});
	
};