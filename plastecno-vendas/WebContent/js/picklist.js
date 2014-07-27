// PickList script- By Sean Geraty (http://www.freewebs.com/sean_geraty/)
// Visit JavaScript Kit (http://www.javascriptkit.com) for this JavaScript and 100s more
// Please keep this notice intact

// Control flags for list selection and sort sequence
// Sequence is on option value (first 2 chars - can be stripped off in form processing)
// It is assumed that the select list is in sort sequence initially
var singleSelect = true;  // Allows an item to be selected once only
var sortSelect = true;  // Only effective if above flag set to true
var sortPick = true;  // Will order the picklist in sort sequence

// Initialise - invoked on load
function initIt() {
  var selectList = document.getElementById("SelectList");
  var selectOptions = selectList.options;
  if(selectOptions.length > 0){
	  var selectIndex = selectList.selectedIndex;
	  var pickList = document.getElementById("PickList");
	  var pickOptions = pickList.options;
	  pickOptions[0] = null;  // Remove initial entry from picklist (was only used to set default width)
	  if (!(selectIndex > -1)) {
	    selectOptions[0].selected = true;  // Set first selected on load
	    selectOptions[0].defaultSelected = true;  // In case of reset/reload
	  }
	  selectList.focus();  // Set focus on the selectlist
  }
  
}

// Adds a selected item into the picklist
function addIt() {
  var selectList = document.getElementById("SelectList");
  var selectIndex = selectList.selectedIndex;
  var selectOptions = selectList.options;
  var pickList = document.getElementById("PickList");
  var pickOptions = pickList.options;
  var pickOLength = pickOptions.length;
  // An item must be selected
  while (selectIndex > -1) {
    pickOptions[pickOLength] = new Option(selectList[selectIndex].text);
    pickOptions[pickOLength].value = selectList[selectIndex].value;
    // If single selection, remove the item from the select list
    if (singleSelect) {
      selectOptions[selectIndex] = null;
    }
    if (sortPick) {
      var tempText;
      var tempValue;
      // Sort the pick list
      while (pickOLength > 0 && pickOptions[pickOLength].value < pickOptions[pickOLength-1].value) {
        tempText = pickOptions[pickOLength-1].text;
        tempValue = pickOptions[pickOLength-1].value;
        pickOptions[pickOLength-1].text = pickOptions[pickOLength].text;
        pickOptions[pickOLength-1].value = pickOptions[pickOLength].value;
        pickOptions[pickOLength].text = tempText;
        pickOptions[pickOLength].value = tempValue;
        pickOLength = pickOLength - 1;
      }
    }
    selectIndex = selectList.selectedIndex;
    pickOLength = pickOptions.length;
  }
  selectOptions[0].selected = true;
}

// Deletes an item from the picklist
function delIt() {
  var selectList = document.getElementById("SelectList");
  var selectOptions = selectList.options;
  var selectOLength = selectOptions.length;
  var pickList = document.getElementById("PickList");
  var pickIndex = pickList.selectedIndex;
  var pickOptions = pickList.options;
  while (pickIndex > -1) {
    // If single selection, replace the item in the select list
    if (singleSelect) {
      selectOptions[selectOLength] = new Option(pickList[pickIndex].text);
      selectOptions[selectOLength].value = pickList[pickIndex].value;
    }
    pickOptions[pickIndex] = null;
    if (singleSelect && sortSelect) {
      var tempText;
      var tempValue;
      // Re-sort the select list
      while (selectOLength > 0 && selectOptions[selectOLength].value < selectOptions[selectOLength-1].value) {
        tempText = selectOptions[selectOLength-1].text;
        tempValue = selectOptions[selectOLength-1].value;
        selectOptions[selectOLength-1].text = selectOptions[selectOLength].text;
        selectOptions[selectOLength-1].value = selectOptions[selectOLength].value;
        selectOptions[selectOLength].text = tempText;
        selectOptions[selectOLength].value = tempValue;
        selectOLength = selectOLength - 1;
      }
    }
    pickIndex = pickList.selectedIndex;
    selectOLength = selectOptions.length;
  }
}

// Selection - invoked on submit
function selIt(btn) {
  var pickList = document.getElementById("PickList");
  var pickOptions = pickList.options;
  var pickOLength = pickOptions.length;
  if (pickOLength < 1) {
    alert("No Selections in the Picklist\nPlease Select using the [->] button");
    return false;
  }
  for (var i = 0; i < pickOLength; i++) {
    pickOptions[i].selected = true;
  }
  return true;
}

function pickListToParameter (nomeLista) {
	var options = document.getElementById('PickList').options;
	var parameter = '';
	for ( var i = 0; i < options.length; i++) {
		parameter += '&'+nomeLista+'[]='+options[i].value;
	}
	return parameter;
};

function nonPickListToParameter (nomeLista) {
	var options = document.getElementById('SelectList').options;
	var parameter = '';
	for ( var i = 0; i < options.length; i++) {
		parameter += '&'+nomeLista+'['+i+'].id='+options[i].value;
	}
	return parameter;
};

function inicializarCampoPesquisaPicklist(config) {
	var url = config.url;
	var mensagem = config.mensagemEspera;
	var parametro = config.parametro;

	$('#campoPesquisaPicklist').attr('maxlength', 8);

	$('#botaoPesquisaPicklist').click(function () {
		var valor = $('#campoPesquisaPicklist').val(); 
		/*
		* Vamos disparar a pesquisa apenas quando o CEP tiver mais de 1 digitos
		* pois com 1 digitos cobrimos um numero de cidades muito grande
		*/
		if (valor == undefined || valor == null || valor.trim().length < 1) {
			return;
		}

		/*
		* Vamos limpar o select e inserir uma informacao de que a requisicao estaem andamento
		* pois em alguns casos o resultado da consulta eh demorado
		*/
		var selectlist = document.getElementById('SelectList');
		selectlist.innerHTML = '';
		selectlist.add(new Option(mensagem, -1, false, false));
		var request = $.ajax({
							type: "get",
							url: url,
							data: parametro+'='+valor.toUpperCase(),
						});
		request.done(function(response) {
			selectlist.innerHTML = '';
			var lista = response.listaResultado;
			var total = lista.length;
			var elemento = null;
			for(var i =0; i < total; i++) {
				elemento = lista[i];	
				selectlist.add(new Option(elemento.label, elemento.valor, false, false));
			}
		});

		request.fail(function(request, status) {
			gerarListaMensagemErro(new Array('Falha na busca dos registros dos valores para preencher picklist: => Status da requisicao: '+status));
		});
	});
};