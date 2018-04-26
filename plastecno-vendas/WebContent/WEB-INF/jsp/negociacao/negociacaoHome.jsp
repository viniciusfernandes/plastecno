<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">
<jsp:include page="/bloco/bloco_css.jsp"></jsp:include>
<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${versaoCache}"/>"></script>


<style type="text/css">
.coluna {
	float: left;
	margin-right: 0;
	margin-top: 0;
	width: 15%;
	
}
.header  {
	border: 1px solid black;
	float: left;
	margin-bottom: 0;
	text-align: center;
	width: 15%;
}
.coluna {
	height: 100%;
	padding: 0;
	margin-bottom: 0;
	min-height: 99vh;
}
.block {
	background: #F4F4F4;
	margin-top: 1px;
}
#cab2, #cab3, #cab4, #cab5, #cab6, #col2, #col3, #col4, #col5, #col6  {
	border-left: 0;
	margin-left: 0;
}
div.block {
	padding: 0px 0 90px 0;
}

div.block > a {
	color: black;
	float: left;
	font-family: 'Lato', Arial, Helvetica, sans-serif;
	font-size: 12px;
}

div.block > a > span {
	padding-left: 5px;
}

fieldset.coluna > legend {
	width: 90%;
}

a.front {
	float: left;
	text-decoration: none;
	width: 100%; 
}


</style>
<script type="text/javascript">
var dialog =null;
var idNeg = null;
$(document).ready(function() {
	configurarAlturaColunas();
	dialog = $( "#dialog-form" ).dialog({
		autoOpen: false,
        height: 200,
        width: 350,
        modal: true,
        buttons: {
    		Inserir: function(){
    			var req = $.ajax({
    					type : "post",
    					url : '<c:url value="/negociacao/observacao/inclusao"/>',
    					data : {'idNegociacao': idNeg, 'observacao':$('#observacao').val()}
    			});
    			req.done(function(response){
    				var erros = response.erros;
    				if(erros!=undefined && erros!=null){
    					gerarListaMensagemErroById('bloco_mensagem_modal', erros);
    				} else {
    					$('#observacao').val('');
    					dialog.dialog( "close" );
    				}
    			});
    		}     
        }
	});
});
function configurarAlturaColunas(){
	var tamanhos = new Array();
	var max = 0;
	var i =0;
	$('fieldset.coluna').each(function(){
		tamanhos[i++] = parseFloat($(this).css('height').replace('px', ''));
	});
	for (i = 0; i < tamanhos.length; i++) {
		if(max < tamanhos[i]){
			max = tamanhos[i];
		}
	}
	$('fieldset.coluna').each(function(){
		$(this).css('height', max);
	});
};

function drop(ev) {
	ev.preventDefault();
	var coluna = recuperarColunaTarget(ev);
	
	var categoriaFinal = coluna.id;
	var idNegociacao = ev.dataTransfer.getData("idNegociacao");
	var categoriaInicial = ev.dataTransfer.getData("categoriaInicial");
	
	if(!isFieldset(coluna)){
		var colInicial = document.getElementById(categoriaInicial);
		var neg = document.getElementById(idNegociacao);
		if(neg!= undefined && neg != null){
			colInicial.appendChild(neg);
		}
		return;
	}
	var valCategFinal = document.getElementById('totVal'+categoriaFinal);
	var valCategInicial = document.getElementById('totVal'+categoriaInicial);
	
	var request = $.ajax({
		type : "post",
		url : '<c:url value="/negociacao/alteracaocategoria/"/>'+idNegociacao,
		data: {'categoriaFinal': categoriaFinal, 'categoriaInicial': categoriaInicial}
	});
	request.done(function(response) {
		var valores = response.valores;
		coluna.appendChild(document.getElementById(idNegociacao));
		valCategFinal.innerHTML = valores.valorCategoriaFinal;
		valCategInicial.innerHTML = valores.valorCategoriaInicial; 
	});
	
	request.fail(function(request, status) {
		alert('Falha alteracao da categoria da negociacao => Status da requisicao: ' + status);
	});
	
	request.always(function(response){
		removerDecoracaoColuna(coluna);
	});
};

function dragleave(ev) {
    ev.preventDefault();
    var col = recuperarColunaTarget(ev);
    removerDecoracaoColuna(col)
};

function dragover(ev) {
    ev.preventDefault();
    var col = recuperarColunaTarget(ev);
    decorarColuna(col);
};

function drag(ev) {
    ev.dataTransfer.setData("idNegociacao", ev.target.id);
    ev.dataTransfer.setData("categoriaInicial", ev.target.parentNode.id);
};

function recuperarColunaTarget(ev){
	if(ev.target.nodeName == 'DIV'){
		return ev.target.parentNode;
	}
	if(ev.target.nodeName == 'A'){
		return ev.target.parentNode.parentNode;
	}
	return ev.target;	
};

function isFieldset(tag){
	return tag.nodeName =='FIELDSET';
}

function decorarColuna(coluna){
	if(isFieldset(coluna)){
		$(coluna).css('border', '1px solid #ffd700');
		$('#legend'+coluna.id).css('background', '#CCAA04').css('border-color', '#CCAA04');
	}
};

function removerDecoracaoColuna(coluna){
	if(isFieldset(coluna)){
		$(coluna).css('border', '1px solid #8AB66B');
		$('#legend'+coluna.id).css('background', '#8AB66B').css('border-color', '#8AB66B');
	}
};

function cancelarNegociacao(idNegociacao){
	var cancelar = function(motivo){
		adicionarInputHiddenFormulario('formVazio', 'idNegociacao', idNegociacao);
		adicionarInputHiddenFormulario('formVazio', 'motivo', motivo);
		
		var f = document.getElementById('formVazio');
		f.action = '<c:url value="negociacao/cancelamento/"/>'+idNegociacao;
		f.submit();
	};
	inicializarModal({
		mensagem: 'Qual é o motivo do CANCELAMENTO?',
		botoes: {
				"Preço" : function() {
					cancelar('${motivoPreco}');
					$(this).dialog("close");
				},
				"Frete" : function() {
					cancelar('${motivoFrete}');
					$(this).dialog("close");
				},
				"Pagamento" : function() {
					cancelar('${motivoPagamento}');
					$(this).dialog("close");
				},
				"Entrega" : function() {
					cancelar('${motivoEntrega}');
					$(this).dialog("close");
				},
				"Outros" : function() {
					cancelar('${motivoOutros}');
					$(this).dialog("close");
				}
			}
		});
};

function editarNegociacao(idNegociacao){
	idNeg=idNegociacao;
	var req = $.ajax({
		type : 'get',
		url : '<c:url value="/negociacao/observacao/"/>'+idNegociacao,
	});
	req.done(function(response){
		$('#observacao').val(response.observacao);
	});
	dialog.dialog('open');
};

function aceitarNegociacao(idNegociacao){
	inicializarModalConfirmacao({
		mensagem: 'Você tem certeza de que deseja ACEITAR esse item?',
		confirmar: function(){
			adicionarInputHiddenFormulario('formVazio', 'idNegociacao', idNegociacao);
			var f = document.getElementById('formVazio');
			f.action = '<c:url value="negociacao/aceite/"/>'+idNegociacao;
			f.submit();
		}
	});
};


</script>
</head>
<body>

<jsp:include page="/bloco/bloco_mensagem.jsp" />
<div id="modal"></div>

<div id="dialog-form" title="Edição da Negociação">
	<div id="bloco_mensagem_modal" class="areaMensagem" style="display: none;">
		<ul></ul>
	</div>
 	<form>
		<div class="input areatexto" style="width: 100%; text-align: left;">
			<textarea id="observacao" name="observacao" style="width: 100%"></textarea>
		</div>
  </form>
</div>
 

<form id="formVazio" method="post"></form>
<c:forEach items="${relatorio.listaGrupo}" var="g">
<fieldset id="${g.id}" class="coluna" ondrop="drop(event)" ondragover="dragover(event)" ondragleave="dragleave(event)">
	<legend id="legend${g.id}">
		<span style="width: 100%; float: left;"><strong>${g.id.descricao}: </strong> </span>  
		<span style="width: 100%; float: left;">R$
			<span id="totVal${g.id}" >${g.propriedades['valorTotal']}</span>
		</span>  
	</legend>
	<c:forEach items="${g.listaElemento}" var="neg">
		<c:if test="${not empty neg}">
		<div id="${neg.id}" class="block" draggable="true" ondragstart="drag(event)" ondragover="dragover(event)">
			<a style="width: 60%; float: left;" href="orcamento/${neg.idOrcamento}" draggable="false">
				<span style="width: 100%; float: left;" draggable="false"><strong>Orç. Nº ${neg.idOrcamento}</strong></span>
			</a>
			<a style="width: 10%; float: left;" class="botaoVerificacaoFalhaPequeno" 
				onclick="cancelarNegociacao(${neg.id})" title="Cancelar Negociação"></a>
			<a style="width: 10%; float: left; margin-left: 2%" class="botaoVerificacaoEfetuadaPequeno" 
				onclick="aceitarNegociacao(${neg.id})" title="Aceitar Negociação"></a>
			<a style="width: 10%; float: left;" class="botaoEditar" 
				onclick="editarNegociacao(${neg.id})" title="Editar Negociação"></a>
			
			<a style="width: 100%; float: left;" draggable="false">
				<span style="float: left;" draggable="false">R$</span>
				<span style="float: left;" draggable="false">${neg.valor}</span>
			</a>
			<a class="front" href="javascript: void(0);" draggable="false">
				<span>${neg.nomeCliente}</span>
			</a>
			<a class="front" href="javascript: void(0);" draggable="false">
				<span title="Indice de conversão de valores">Val: ${neg.indiceConversaoValor} %</span>
				<span title="Indice de conversão de quantidades">Qtde: ${neg.indiceConversaoQuantidade} %</span>
			</a>
			<a class="front" href="javascript: void(0);" draggable="false">
				<span>${neg.nomeContato}</span>
			</a>
			<a class="front" href="javascript: void(0);" draggable="false">
				<span>${neg.telefoneContato}</span>
			</a>
		</div>
		</c:if>
	</c:forEach>
</fieldset>
</c:forEach>

</body>
</html>