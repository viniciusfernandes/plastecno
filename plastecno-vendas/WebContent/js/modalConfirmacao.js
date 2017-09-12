function inicializarModalConfirmacao(config) {
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
		buttons : {
			"Confirmar" : function() {
				if (confirmar != undefined) {
					confirmar();
				}
				$(this).dialog("close");
			},
			"Cancelar" : function() {
				$(this).dialog("close");
			}
		}
	});

	$(modal).html(mensagem);
	$(modal).dialog('open');
};