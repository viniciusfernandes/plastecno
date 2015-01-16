<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/picklist.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bloco/contato.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>


<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');

	$("#botaoAssociarCliente").click(function() {
		var parametros = pickListToParameter('listaIdClienteAssociado');
		$('#formVendedor').attr("action",$('#formVendedor').attr('action')+'?'+ parametros);
		$('#formVendedor').submit();
	});

	$("#botaoPesquisarVendedor").click(function() {
		var listaId = ['cpf'];
		removerNaoDigitos(listaId);
		toUpperCaseInput();
		inicializarFiltro();
		$('#formPesquisa').submit();
	});

	$("#botaoLimpar").click(function() {
		$('#formVazio').submit();
	});
	
	var picklistCliente = new PickList();
	picklistCliente.initPickList();
	
	picklistCliente.onAddItem = function(listaId) {
		if(listaId != undefined && listaId.length > 0) {
			var parametros = gerarListaParametroId(listaId, 'listaIdClienteAssociado');
			$('#formVendedor').attr("action",action='vendedor/associacaocliente?'+ parametros);
			$('#formVendedor').submit();
		}
	};
	
	picklistCliente.onDelItem = function(listaId) {
		if(listaId != undefined && listaId.length > 0) {
			var parametros = gerarListaParametroId(listaId, 'listaIdClienteDesassociado');
			$('#formVendedor').attr("action",'vendedor/desassociacaocliente?'+ parametros);
			$('#formVendedor').submit();	
		}
	};
	
	inserirMascaraCPF('cpf');
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
	autocompletar({
		url : '<c:url value="/vendedor/listagem/nome"/>',
		campoPesquisavel : 'nome',
		parametro : 'nome',
		containerResultados : 'containerPesquisaVendedor',
		selecionarItem: function(itemLista) {
			var formVazio = document.getElementById('formVazio');
			formVazio.action = '<c:url value="/vendedor/edicao"/>';
			formVazio.elements['idVendedor'].value = itemLista.id;
			formVazio.submit();
		}
	});
});

function inicializarFiltro() {
	$("#filtro_nome").val($("#nome").val());
	$("#filtro_cpf").val($("#cpf").val());
	$("#filtro_email").val($("#email").val());
	$("#filtro_sobrenome").val($("#sobrenome").val());
}

function remover(codigo, sigla) {
	var remocaoConfirmada = confirm("Voce tem certeza de que deseja desabilitar o vendedor \""+sigla+"\"?");
	if (remocaoConfirmada) {
		$("#formVendedorRemocao").attr("action","<c:url value="/vendedor/remocao"/>?id="+codigo);
		$("#formVendedorRemocao").attr("method","post");
		$('#formVendedorRemocao').submit();
	}	
}
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />


	<form id="formPesquisa" action="vendedor/listagem" method="get">
		<input type="hidden" id="filtro_nome" name="filtro.nome" /> 
		<input type="hidden" id="filtro_sobrenome" name="filtro.sobrenome" /> 
		<input type="hidden" id="filtro_email" name="filtro.email" /> 
		<input type="hidden" id="filtro_cpf" name="filtro.cpf" />
	</form>

	<form id="formVendedor" action="vendedor/associacao/cliente"
		method="post">
		<input type="hidden" id="id" name="vendedor.id" value="${vendedor.id}" />
	</form>

	<form id="formVazio" action="vendedor" method="get">
		<input type="hidden" id="idVendedor" name="idVendedor" />
	</form>

	<fieldset>
		<legend>::: Dados do Vendedor :::</legend>


		<div class="label">Ativo:</div>
		<div class="input" style="width: 80%">
			<input type="checkbox" id="ativo" name="vendedor.ativo"
				<c:if test="${vendedor.ativo}">checked</c:if>
				class="checkbox <c:if test="${not empty vendedor.id}">desabilitado</c:if>"
				<c:if test="${not empty vendedor.id}">disabled='disabled'</c:if> />
		</div>
		<div class="label">Nome:</div>
		<div class="input" style="width: 20%">
			<input type="text" id="nome" name="vendedor.nome"
				value="${vendedor.nome}"
				class="pesquisavel <c:if test="${not empty vendedor.id}">desabilitado</c:if>"
				<c:if test="${not empty vendedor.id}">disabled='disabled'</c:if> />
			<div class="suggestionsBox" id="containerPesquisaVendedor" style="display: none; width: 50%"></div>
		</div>
		<div class="label">Sobrenome:</div>
		<div class="input" style="width: 40%">
			<input type="text" id="sobrenome" name="vendedor.sobrenome"
				value="${vendedor.sobrenome}"
				class="pesquisavel <c:if test="${not empty vendedor.id}">desabilitado</c:if>"
				style="width: 50%"
				<c:if test="${not empty vendedor.id}">disabled='disabled'</c:if> />
		</div>
		<div class="label">CPF:</div>
		<div class="input" style="width: 20%">
			<input type="text" id="cpf" name="vendedor.cpf"
				value="${vendedor.cpf}"
				class="pesquisavel <c:if test="${not empty vendedor.id}">desabilitado</c:if>"
				style="width: 50%"
				<c:if test="${not empty vendedor.id}">disabled='disabled'</c:if> />
		</div>
		<div class="label">Email:</div>
		<div class="input" style="width: 40%">
			<input type="text" id="email" name="vendedor.email"
				value="${vendedor.email}" style="width: 50%"
				class="uppercaseBloqueado pesquisavel <c:if test="${not empty vendedor.id}">desabilitado</c:if>"
				<c:if test="${not empty vendedor.id}">disabled='disabled'</c:if> />
		</div>
	</fieldset>
	<div class="bloco_botoes">
		<a id="botaoPesquisarVendedor" title="Pesquisar Dados do Vendedor"
			class="botaoPesquisar"></a> <a id="botaoLimpar"
			title="Limpar Dados do Vendedor" class="botaoLimpar"></a>
	</div>

	<jsp:include page="/bloco/bloco_picklist.jsp" />

	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Vendedores :::</legend>
		<div id="paginador"></div>
		<div>
			<form id="formVendedorRemocao"></form>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 5%">Ativo</th>
						<th style="width: 30%">Nome</th>
						<th style="width: 35%">Sobrenome</th>
						<th style="width: 20%">Email</th>
						<th>Ações</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="vendedor" items="${listaVendedor}">
						<tr>
							<c:choose>
								<c:when test="${vendedor.ativo}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>
							<td>${vendedor.nome}</td>
							<td>${vendedor.sobrenome}</td>
							<td>${vendedor.email}</td>
							<td>
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/vendedor/edicao"/>" method="get">
										<input type="submit" id="botaoEditarVendedor"
											title="Editar Dados do Vendedor" value="" class="botaoEditar" />
										<input type="hidden" name="idVendedor" value="${vendedor.id}" />
									</form>
								</div>
							</td>
						</tr>

					</c:forEach>
				</tbody>

			</table>
		</div>
	</fieldset>
</body>
</html>