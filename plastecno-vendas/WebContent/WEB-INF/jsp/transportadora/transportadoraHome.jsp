<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">


<title>Plastecno - Cadastro de Transportadoras</title>

<jsp:include page="/bloco/bloco_css.jsp" />

<style type="text/css">
body {
	height: 100%;
}
</style>

<script type="text/javascript"
	src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/tabela_handler.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/logradouro.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/bloco/contato.js"/>"></script>


<script type="text/javascript">

var tabelaContatoHandler = null;

$(document).ready(function() {
	scrollTo('${ancora}');
	
	$("#botaoPesquisarTransportadora").click(function() {
		toUpperCaseInput();
		var listaId = ['cnpj', 'inscricaoEstadual'];
		removerNaoDigitos(listaId);
		inicializarFiltro();
		$('#formPesquisa').submit();
	});
	
	
	$("#botaoInserirTransportadora").click(function() {
		toUpperCaseInput();
		toLowerCaseInput();
		
		var listaId = ['cnpj', 'inscricaoEstadual'];
		removerNaoDigitos(listaId);
				
		var parametros = tabelaContatoHandler.gerarListaParametro('listaContato');
		
		$("#formTransportadora").attr("action","<c:url value="/transportadora/inclusao"/>?"+parametros);
		$('#formTransportadora').submit();		
	});

	$("#botaoLimpar").click(function() {
		$('#formVazio').submit();
	});

	inserirMascaraCNPJ('cnpj');
	inserirMascaraInscricaoEstadual('inscricaoEstadual');
	
	tabelaContatoHandler = inicializarBlocoContato('<c:url value="/transportadora"/>');

	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
			
});

function inicializarFiltro() {
	$("#filtroNomeFantasia").val($("#nomeFantasia").val());
	$("#filtroCnpj").val($("#cnpj").val());
	$("#filtroRazaoSocial").val($("#razaoSocial").val());
}

</script>

</head>
<body>
	<input type="hidden" name="transportadora.id"
		value="${transportadora.id}">
	<input type="hidden" name="logradouro.id" value="${logradouro.id}">
	<input type="hidden" name="contato.id" value="${contato.id}">

	<jsp:include page="/bloco/bloco_mensagem.jsp" />

	<form id="formVazio" action="transportadora" method="get"></form>

	<form id="formPesquisa" action="<c:url value="/transportadora/listagem"/>" method="get">
		<input type="hidden" id="filtroNomeFantasia" name="filtro.nomeFantasia" /> 
		<input type="hidden" id="filtroCnpj" name="filtro.cnpj" /> 
		<input type="hidden" id="filtroRazaoSocial" name="filtro.razaoSocial" />
	</form>

	<form id="formTransportadora"
		action="<c:url value="/transportadora/inclusao"/>" method="post">
		<input type="hidden" id="codigo" name="transportadora.id"
			value="${transportadora.id}">

		<fieldset>
			<legend>::: Dados de Transportadora :::</legend>

			<div class="label">Ativo:</div>
			<div class="input" style="width: 80%">
				<input type="checkbox" id="ativo" name="transportadora.ativo"
					<c:if test="${empty transportadora or transportadora.ativo}">checked="checked"</c:if>
					class="checkbox" />
			</div>
			<div class="label obrigatorio">Nome Fantasia:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="nomeFantasia"
					name="transportadora.nomeFantasia"
					value="${transportadora.nomeFantasia}" class="pesquisavel"
					style="width: 60%" />
			</div>

			<div class="label obrigatorio">Razão Social:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="razaoSocial"
					name="transportadora.razaoSocial"
					value="${transportadora.razaoSocial}" class="pesquisavel"
					style="width: 60%" />
			</div>

			<div class="label">CNPJ:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="cnpj" name="transportadora.cnpj"
					value="${transportadora.cnpj}" style="width: 80%"
					class="pesquisavel" />
			</div>

			<div class="label" style="width: 11%">Insc. Estadual:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="inscricaoEstadual"
					name="transportadora.inscricaoEstadual"
					value="${transportadora.inscricaoEstadual}"
					style="width: 40%; text-align: right;" />
			</div>

			<div class="label">Site:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="site" name="transportadora.site"
					value="${transportadora.site}" style="width: 60%"
					class="apenasLowerCase uppercaseBloqueado lowerCase" />
			</div>

			<div class="label">Região de Atuação:</div>
			<div class="input areatexto" style="width: 80%">
				<textarea id="areaAtuacao" name="transportadora.areaAtuacao"
					style="width: 60%">${transportadora.areaAtuacao}</textarea>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<a id="botaoPesquisarTransportadora"
				title="Pesquisar Dados da Transportadora" class="botaoPesquisar"></a>
			<a id="botaoLimpar" title="Limpar Dados Da Transportadora"
				class="botaoLimpar"></a>
		</div>

		<jsp:include page="/bloco/bloco_logradouro.jsp" />
		<jsp:include page="/bloco/bloco_contato.jsp" />

		<div class="bloco_botoes">
			<c:if test="${acessoCadastroBasicoPermitido}">
				<a id="botaoInserirTransportadora"
					title="Incluir Dados da Transportadora" class="botaoInserir"></a>
			</c:if>
		</div>

	</form>
	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Transportadoras :::</legend>
		<div id="paginador"></div>
		<div>
			<table id="tabela" class="listrada">
				<thead>
					<tr>
						<th style="width: 5%">Ativo</th>
						<th style="width: 35%">Nome</th>
						<th style="width: 35%">Razão Social</th>
						<th>CNPJ</th>
						<th>Ações</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="transportadora" items="${listaTransportadora}">
						<tr>
							<c:choose>
								<c:when test="${transportadora.ativo}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>
							<td>${transportadora.nomeFantasia}</td>
							<td>${transportadora.razaoSocial}</td>
							<td>${transportadora.cnpj}</td>
							<td>
								<div class="coluna_acoes_listagem">
									<form
										action="<c:url value="/transportadora/${transportadora.id}"/>"
										method="get">
										<input type="submit" title="Enviar Dados do(a) Transportadora"
											value="" class="botaoEditar" />
									</form>
									<c:if test="${acessoCadastroBasicoPermitido}">
										<form action="<c:url value="/transportadora/desativacao"/>"
											method="post">
											<input type="hidden" name="idTransportadora"
												value="${transportadora.id}"> <input type="submit"
												title="Remover Dados do(a) Transportadora" value=""
												class="botaoRemover"
												onclick="javascript: return confirm('Voce tem certeza de que deseja desativar a TRANSPORTADORA?');" />
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
