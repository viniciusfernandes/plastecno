<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">
<jsp:include page="/bloco/bloco_css.jsp"></jsp:include>
<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
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
	padding: 0px 0 60px 0;
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
	var data = ev.dataTransfer.getData("text");
	ev.target.appendChild(document.getElementById(data));
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
<fieldset class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<legend>Propostas Clientes</legend>
	<div id="o1" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o11" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o12" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o13" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o14" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o15" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o16" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o17" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o18" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o19" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
	<div id="o181" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</fieldset>
<fieldset class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<legend>Primeiro contato</legend>
	<div id="o2" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</fieldset>
<fieldset class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<legend>Negócios Potenciais</legend>
	<div id="o3" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</fieldset>
<fieldset class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<legend>Projetos</legend>
	<div id="o4" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</fieldset>
<fieldset class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<legend>Prováveis</legend>
	<div id="o5" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</fieldset>
<fieldset class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<legend>Começando</legend>
	<div id="o6" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</fieldset>
</body>
</html>