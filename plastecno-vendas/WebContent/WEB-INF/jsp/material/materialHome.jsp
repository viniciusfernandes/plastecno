<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript"
	src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/picklist.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery.maskMoney.js"/>"></script>

<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');

	$("#botaoInserirMaterial").click(function() {
		toUpperCaseInput();
		var parametros = '?'+pickListToParameter('listaIdRepresentadaAssociada').replace('&', '');
		$('#formMaterial').attr("action","<c:url value="/material/inclusao"/>"+parametros);
		$('#formMaterial').attr("method","post");
		$('#formMaterial').submit();
						
	});
	
	$("#botaoPesquisarMaterial").click(function() {
		toUpperCaseInput();
		inicializarFiltro();
		$('#formPesquisa').submit();
	});

	inserirMascaraMonetaria('pesoEspecifico', 4);
	inicializarPaginador(
			'<c:out value="${not empty paginaSelecionada ? paginaSelecionada : 0}"/>', 
			'<c:out value="${not empty totalPaginas ? totalPaginas : 1}"/>');
	
	new PickList().initPickList();

});

function inicializarFiltro() {
	$("#filtroSigla").val($("#sigla").val());
	$("#filtroDescricao").val($("#descricao").val());
}

function remover(codigo, sigla) {
	var remocaoConfirmada = confirm("Voce tem certeza de que deseja remover o material \""+sigla+"\"?");
	if (remocaoConfirmada) {
		$("#formMaterialRemocao").attr("action","<c:url value="/material/remocao"/>?id="+codigo);
		$("#formMaterialRemocao").attr("method","post");
		$('#formMaterialRemocao').submit();
	}	
}

</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/material"/>" method="get"></form>

	<form id="formPesquisa" action="<c:url value="/material/listagem"/>"
		method="get">
		<input type="hidden" id="filtroSigla" name="filtro.sigla" /> <input
			type="hidden" id="filtroDescricao" name="filtro.descricao" />
	</form>


	<form id="formMaterial" action="<c:url value="/material/inclusao"/>"
		method="post">
		<input type="hidden" id="codigo" name="material.id"
			value="${material.id}">

		<fieldset>
			<legend>::: Dados do Material :::</legend>
			<div class="label">Ativo:</div>
			<div class="input" style="width: 80%">
				<input type="checkbox" id="ativo" name="material.ativo"
					<c:if test="${empty material or material.ativo}">checked="checked"</c:if>
					class="checkbox" />
			</div>
			<div class="label">Importado:</div>
			<div class="input" style="width: 80%">
				<input type="checkbox" id="ativo" name="material.importado"
					<c:if test="${not empty material and material.importado}">checked="checked"</c:if>
					class="checkbox" />
			</div>
			<div class="label obrigatorio">Sigla:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="sigla" name="material.sigla"
					value="${material.sigla}" class="pesquisavel" />
			</div>

			<div class="label">Descrição:</div>
			<div class="input" style="width: 50%">
				<input type="text" id="descricao" name="material.descricao"
					value="${material.descricao}" class="pesquisavel"
					style="width: 70%" />
			</div>
			<div class="label obrigatorio">Peso Específ. (g/cm³):</div>
			<div class="input" style="width: 10%">
				<input type="text" id="pesoEspecifico"
					name="material.pesoEspecifico" value="${material.pesoEspecifico}" />
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<a id="botaoPesquisarMaterial" title="Pesquisar Dados do Material"
				class="botaoPesquisar"></a> <a id="botaoLimpar"
				title="Limpar Dados do Material" onclick="limparFormulario();"
				class="botaoLimpar"></a>
		</div>

		<jsp:include page="/bloco/bloco_picklist.jsp" />

		<div class="bloco_botoes">
			<c:if test="${acessoCadastroBasicoPermitido}">
				<a id="botaoInserirMaterial" title="Incluir Dados do Material"
					class="botaoInserir"></a>
			</c:if>
		</div>
	</form>

	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Materiais :::</legend>
		<div id="paginador"></div>
		<div>
			<form id="formMaterialRemocao"></form>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 5%">Ativo</th>
						<th style="width: 5%">Import.</th>
						<th style="width: 20%">Sigla</th>
						<th style="width: 40%">Descrição</th>
						<th style="width: 10%">Peso Específico</th>
						<th>Ações</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="material" items="${listaMaterial}">
						<tr>
							<c:choose>
								<c:when test="${material.ativo}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${material.importado}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>

							<td>${material.sigla}</td>
							<td>${material.descricao}</td>
							<td>${material.pesoEspecifico}</td>
							<td>
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/material/edicao"/>" method="post">
										<input type="submit" title="Editar Dados do Material" value=""
											class="botaoEditar" /> <input type="hidden" name="id"
											value="${material.id}" />
									</form>
									<c:if test="${acessoCadastroBasicoPermitido}">
										<form action="<c:url value="/material/desativacao"/>"
											method="post">
											<input type="hidden" name="id" value="${material.id}">
											<input type="submit" id="botaoRemoverRamo"
												title="Desativar Material" value="" class="botaoRemover"
												onclick="javascript: return confirm('Voce deseja mesmo desativar o MATERIAL?');" />
										</form>
									</c:if>
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
