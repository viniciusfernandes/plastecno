<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">
<jsp:include page="/bloco/bloco_css.jsp"></jsp:include>
<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>

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
	padding: 0px 0 90px 0;
}

div.block > a, div.block > a > span {
	width: 100%;
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
</style>
<script type="text/javascript">
function drop(ev) {
	ev.preventDefault();
	var idNegociacao = ev.dataTransfer.getData("text");
	var categ = ev.target.id;
	var leg = document.getElementById('leg'+categ);
	var val = removerCaracteresNaoDigitos(document.getElementById('val'+idNegociacao).innerHTML);
	if(isEmpty(val)){
		val = 0;
	}
	var tot = removerCaracteresNaoDigitos(leg.innerHTML);
	if(isEmpty(tot)){
		tot = 0;
	}
	tot = parseFloat(val)+parseFloat(tot);
	var request = $.ajax({
		type : "post",
		url : '<c:url value="/negociacao/alteracaocategoria/"/>'+idNegociacao,
		data: {'categoria': categ}
	});
	request.done(function(response) {
		ev.target.appendChild(document.getElementById(idNegociacao));
		leg.innerHTML = leg.innerHTML.split(':')[0]+ ': R$' +tot; 
	});
	
	request.fail(function(request, status) {
		alert('Falha alteracao da categoria da negociacao => Status da requisicao: ' + status);
	});
};

function allowDrop(ev) {
    ev.preventDefault();
};

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
};
</script>
</head>
<body>
<c:forEach items="${relatorio.listaGrupo}" var="g">
<fieldset id="${g.id}" class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<legend id="leg${g.id}">${g.id.descricao}: R$ ${g.propriedades['valorTotal']}</legend>
	<c:forEach items="${g.listaElemento}" var="neg">
		<c:if test="${not empty neg}">
		<div id="${neg.id}" class="block" draggable="true" ondragstart="drag(event)">
			<a class="front" href="/deal/1" >
				<span><strong>Orçamento Nº ${neg.idOrcamento}</strong></span>
				<span id="val${neg.id}">R$ ${neg.valor}</span>
			</a>
			<a class="front" href="/deal/1" >
				<span>${neg.nomeCliente}</span>
			</a>
			<a class="front" href="/deal/1" >
				<span>${neg.nomeContato}</span>
			</a>
			<a class="front" href="/deal/1" >
				<span>${neg.telefoneContato}</span>
			</a>
		</div>
		</c:if>
	</c:forEach>
</fieldset>
</c:forEach>

</body>
</html>