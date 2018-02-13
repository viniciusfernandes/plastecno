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
	width: 15%;
	float: left;
	margin-right: 0;
	margin-top: 0;
}
.header  {
	width: 15%;
	float: left;
	border: 1px solid black;
	margin-bottom: 0;
	text-align: center;
}
.coluna {
	height: 100%;
	padding: 0;
	min-height: 99vh;
}
.block {
	margin-top: 1px;
	background: #F4F4F4;
}
#cab2, #cab3, #cab4, #cab5, #cab6, #col2, #col3, #col4, #col5, #col6  {
	border-left: 0;
	margin-left: 0;
}
div.block {
	padding: 0px 0 80px 0;
}

div.block > a {
	float: left;
	color: black;
	font-size: 12px;
	font-family: 'Lato', Arial, Helvetica, sans-serif;
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
function drop(ev) {
	ev.preventDefault();
	var coluna = recuperarColunaTarget(ev);
	if(!isFieldset(coluna)){
		return;
	}
	var categoriaFinal = coluna.id;
	var idNegociacao = ev.dataTransfer.getData("idNegociacao");
	var categoriaInicial = ev.dataTransfer.getData("categoriaInicial");
	
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
		coluna.style.border = '1px solid #ffd700';
	}
};

function removerDecoracaoColuna(coluna){
	if(isFieldset(coluna)){
		coluna.style.border = '1px solid #8AB66B';
	}
};

function cancelarNegociacao(idNegociacao){
	inicializarModalConfirmacao({
		mensagem: 'Você tem certeza de que deseja CANCELAR esse item?',
		confirmar: function(){
			adicionarInputHiddenFormulario('formVazio', 'idNegociacao', idNegociacao);
			var f = document.getElementById('formVazio');
			f.action = '<c:url value="negociacao/cancelamento/"/>'+idNegociacao;
			f.submit();
		}
	});
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
<form id="formVazio" method="post"></form>
<c:forEach items="${relatorio.listaGrupo}" var="g">
<fieldset id="${g.id}" class="coluna" ondrop="drop(event)" ondragover="dragover(event)" ondragleave="dragleave(event)">
	<legend id="leg${g.id}">
		<span style="width: 100%; float: left;"><strong>${g.id.descricao}: </strong> </span>  
		<span style="width: 100%; float: left;">R$
			<span id="totVal${g.id}" >${g.propriedades['valorTotal']}</span>
		</span>  
	</legend>
	<c:forEach items="${g.listaElemento}" var="neg">
		<c:if test="${not empty neg}">
		<div id="${neg.id}" class="block" draggable="true" ondragstart="drag(event)" ondragover="dragover(event)">
			<a style="width: 75%; float: left;" href="orcamento/${neg.idOrcamento}" draggable="false">
				<span style="width: 100%; float: left;" draggable="false"><strong>Orç. Nº ${neg.idOrcamento}</strong></span>
				<span style="float: left;" draggable="false">R$</span>
				<span style="float: left;" draggable="false">${neg.valor}</span>
			</a>
			<a style="width: 10%; float: left;" class="botaoVerificacaoFalhaPequeno" 
				onclick="cancelarNegociacao(${neg.id})" title="Cancelar Negociação"></a>
			<a style="width: 10%; float: left; margin-left: 2%" class="botaoVerificacaoEfetuadaPequeno" 
				onclick="aceitarNegociacao(${neg.id})" title="Aceitar Negociação"></a>
			<a class="front" href="javascript: void(0);" draggable="false">
				<span>${neg.nomeCliente}</span>
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