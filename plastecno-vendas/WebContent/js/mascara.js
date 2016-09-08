function limpar(string) {
	var minha_er = /\d/g;
	var string_retorno = string.replace( minha_er, '0');
	return string_retorno;
}

function inserirMascaraCEP(idCampo) {
	$('#'+idCampo).mask('99999999');
}


function inserirMascaraCNPJ(idCampo) {
	$('#'+idCampo).mask('99.999.999/9999-99');
}

function removerCaracteres(valor) {
	return valor.replace(/\D/g, "");
}

function inserirMascaraInscricaoEstadual(idCampo) {
	$('#'+idCampo).mask('999.999.999.999.999');
}

function inserirMascaraCPF(idCampo) {
	$('#'+idCampo).mask('999.999.999-99');
}

function inserirMascaraNumerica(idCampo, mascara) {
	$('#'+idCampo).mask(mascara);
};

function inserirMascaraMonetaria(idCampo, digitos) {
	// Aqui adicionamos um caracter referente ao ponto da casa decimal
	$('#'+idCampo).attr('maxlength', digitos + 1);
	// A separacao entre os milhares nao pode ter simbolos de sepadas casas
	$('#'+idCampo).maskMoney({thousands:'', decimal:'.'});
}

function mascaraData(idCampo, mascara){
	$("#"+idCampo).datepicker({
		dateFormat: mascara,
		dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado','Domingo'],
		dayNamesMin: ['D','S','T','Q','Q','S','S','D'],
		dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb','Dom'],
		monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
		monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'],
		nextText: 'Próximo',
		prevText: 'Anterior' 
	});
}

function inserirMascaraData(idCampo) {
	mascaraData(idCampo, 'dd/mm/yy');
};

function inserirMascaraDataAmericano(idCampo) {
	mascaraData(idCampo, 'yy-mm-dd');
};

function inserirMascaraNCM(idCampo) {
	$('#'+idCampo).mask('99.99.99.99');
}