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
	border: 1px solid black;
	margin-right: 0;
	margin-top: 0;
	background: #e5e5e5;
}
.header  {
	width: 15%;
	float: left;
	border: 1px solid black;
	margin-bottom: 0;
	text-align: center;
}
.coluna {
	height: 80vh;
	padding: 0;
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
<div id="cab1" class="coluna header" style="height: 5vh">
	Primeiro contato
</div>
<div id="cab2" class="coluna header" style="height: 5vh">
	Primeiro contato
</div>
<div id="cab3" class="coluna header" style="height: 5vh">
	Primeiro contato
</div>
<div id="cab4" class="coluna header" style="height: 5vh">
	Primeiro contato
</div>
<div id="cab5" class="coluna header" style="height: 5vh">
	Primeiro contato
</div>
<div id="cab6" class="coluna header" style="height: 5vh">
	Primeiro contato
</div>
<div id="col1" class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<div id="xxx" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</div>
<div id="col2" class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<div id="xxx" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</div>
<div id="col3" class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<div id="xxx" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</div>
<div id="col4" class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<div id="xxx" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</div>
<div id="col5" class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<div id="zzz" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</div>
<div id="col6" class="coluna" ondrop="drop(event)" ondragover="allowDrop(event)">
	<div id="ooo" class="block" draggable="true" ondragstart="drag(event)">
		<a class="front" href="/deal/1" >
			<span>Orçamento Nº 22425</span>
			<span>R$13.428,36</span>
		</a>
		<a class="front" href="/deal/1" >
			<span>Vitoria Industrial</span>
		</a>
	</div>
</div>
</body>
</html>