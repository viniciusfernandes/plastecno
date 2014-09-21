<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">

<jsp:include page="/bloco/bloco_css.jsp" />

<style type="text/css">
.bloco_input {
	height: 110px;
}

.coluna {
	position: relative;
	left: 30%;
	width: 50%;	
}
</style>
<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>

<script type="text/javascript">

$(document).ready(function() {

	$("#botaoInserirRamo").click(function() {
		toUpperCaseInput();
		$('#formRamo').submit();
				
	});
	
	$("#botaoPesquisarRamo").click(function() {
		toUpperCaseInput();
		inicializarFiltro();
		$('#formPesquisa').submit();
	});
	
	inicializarPaginador(
			'<c:out value="${not empty paginaSelecionada ? paginaSelecionada : 0}"/>', 
			'<c:out value="${not empty totalPaginas ? totalPaginas : 1}"/>');
});

function inicializarFiltro() {
	$("#filtroSigla").val($("#sigla").val());
	$("#filtroDescricao").val($("#descricao").val());	
}

</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
		
	<form id="formVazio" action="ramo" method="get">
	</form>
	
	<form id="formPesquisa" action="<c:url value="/ramo/listagem"/>" method="get">
		<input type="hidden" id="filtroSigla" name="filtro.sigla" />
		<input type="hidden" id="filtroDescricao" name="filtro.descricao"/>		
	</form>
	<form id="formRamo" action="<c:url value="/ramo/inclusao"/>" method="post">
		<input type="hidden" id="codigo" name="ramoAtividade.id" value="${ramoAtividade.id}">

		<fieldset>
			<legend>::: Dados do Ramo de Atividades :::</legend>
					<div class="label" >Ativo:</div>
					<div class="input" style="width: 80%">
						<input type="checkbox" id="ativo" name="ramoAtividade.ativo" <c:if test="${empty ramoAtividade or ramoAtividade.ativo}">checked="checked"</c:if> class="checkbox"/>
					</div>		
					<div class="label obrigatorio">Sigla:</div>
					<div class="input" style="width: 80%">
						<input type="text" id="sigla" name="ramoAtividade.sigla" value="${ramoAtividade.sigla}" maxlength="10" class="pesquisavel" style="width: 30%"/>
					</div>
		
					<div class="label obrigatorio">Descrição:</div>
					<div class="input" style="width: 80%">
						<input type="text" id="descricao" name="ramoAtividade.descricao" value="${ramoAtividade.descricao}" maxlength="100" class="pesquisavel" style="width: 30%"/>
					</div>
		</fieldset>
		<div class="bloco_botoes">
			<c:if test="${acessoCadastroBasicoPermitido}">
				<a id="botaoInserirRamo" title="Incluir Dados do Ramo de Atividades" class="botaoInserir"></a>
			</c:if>
			<a id="botaoPesquisarRamo" title="Pesquisar Dados do Ramo de Atividades" class="botaoPesquisar"></a>
			
			<a id="botaoLimpar" title="Limpar Dados do Ramo de Atividades" onclick="limparFormulario();" class="botaoLimpar"></a>
		</div>
	</form>

	<fieldset>
		<legend>::: Resultado da Pesquisa de Ramos :::</legend>
		<div id="paginador"></div>
		<div>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 5%">Ativo</th>
						<th style="width: 20%">Sigla</th>
						<th>Descrição</th>
						<th>Ações</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="ramo" items="${listaRamoAtividade}">
						<tr>
							<c:choose>
								<c:when test="${ramo.ativo}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>
							<td>${ramo.sigla}</td>
							<td>${ramo.descricao}</td>
							<td>
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/ramo/${ramo.id}"/>" method="get">
										<input type="submit" id="botaoEditarUsuario" title="Editar Dados do Usuario" value="" class="botaoEditar" />
									</form>
									<c:if test="${acessoCadastroBasicoPermitido}">
									<form action="<c:url value="/ramo/desativacao"/>" method="post">
										
											<input type="hidden" name="id" value="${ramo.id}">
											<input type="submit" title="Desativar Ramo de Atividades" value="" class="botaoRemover"
												onclick="javascript: return confirm('Voce deseja mesmo desativar o RAMO?');"/>
										
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
