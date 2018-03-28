function inicializarModal(config) {
	var confirmar = config.confirmar;
	var mensagem = config.mensagem;
	var modal = $('#modal');
	if(modal == undefined || modal == null){
		alert('Nao existe um div de modal para reenderizar as mensagens.');
	}
	modal = $(modal).dialog({
		autoOpen : false,
		modal : true,
		resizable : false,
		closeText : 'Fechar',
		buttons : config.botoes,
		position: { my: "center", at: "center", of: window}
	});
	
	$(modal).html(mensagem);
	$(modal).dialog('open');
};

function inicializarModalConfirmacao(config) {
	config.botoes = {
			"Confirmar" : function() {
				if (config.confirmar != undefined) {
					config.confirmar();
				}
				$(this).dialog("close");
			}
		};
	inicializarModal(config);
};
