<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">

<title>Plastecno - Cadastro de Representada</title>

<jsp:include page="/bloco/bloco_css.jsp" />

<style type="text/css">
body {
	height: 100%;
}
</style>

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/tabela_handler.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/logradouro.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bloco/contato.js"/>"></script>


<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>

<script type="text/javascript">
var paginaSelecionada = '<c:out value="${not empty paginaSelecionada ? paginaSelecionada : 0}"/>';
var totalPaginas = '<c:out value="${not empty totalPaginas ? totalPaginas : 1}"/>';
var tabelaContatoHandler = null;
var tabelaLogradouroHandler = null;

$(document).ready(function() {

	scrollTo('${ancora}');
	
	$("#botaoPesquisarRepresentada").click(function() {
		var listaId = ['cnpj', 'inscricaoEstadual'];
		removerNaoDigitos(listaId);	
		toUpperCaseInput();
		inicializarFiltro();
		$('#formPesquisa').submit();
	});
	
	
	$("#botaoInserirRepresentada").click(function() {
		toUpperCaseInput();
		toLowerCaseInput();
		
		var listaId = ['cnpj', 'inscricaoEstadual'];
		removerNaoDigitos(listaId);

		var parametros = tabelaContatoHandler.gerarListaParametro('listaContato');
		
		$("#formRepresentada").attr("action", "<c:url value="/representada/inclusao"/>?"+parametros);
		$('#formRepresentada').submit();
	});

	$("#botaoLimpar").click(function() {
		$('#formVazio').submit();
	});
	
	tabelaContatoHandler = inicializarBlocoContato('<c:url value="/representada"/>');
	tabelaLogradouroHandler = new BlocoTabelaHandler('Logradouro', null, 'bloco_logradouro'); 
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
	inserirMascaraCNPJ('cnpj');
	inserirMascaraInscricaoEstadual('inscricaoEstadual');
	inserirMascaraNumerica('comissao', '99');
});

function inicializarFiltro() {
	$("#filtroNomeFantasia").val($("#nomeFantasia").val());
	$("#filtroCnpj").val($("#cnpj").val());
	$("#filtroRazaoSocial").val($("#razaoSocial").val());
}

function inicializarModalCancelamento(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja DESATIVAR essa representada?',
		confirmar: function(){
			$(botao).closest('form').submit();	
		}
	});
}
</script>

</head>
<body>
	<input type="hidden" name="representada.id" value="${representada.id}">
	<input type="hidden" name="logradouro.id" value="${logradouro.id}">
	<input type="hidden" name="contato.id" value="${contato.id}">

	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
	<form id="formVazio" action="representada" method="get"></form>

	<form id="formPesquisa"
		action="<c:url value="/representada/listagem"/>" method="get">
		<input type="hidden" id="filtroNomeFantasia"
			name="filtro.nomeFantasia" /> <input type="hidden" id="filtroCnpj"
			name="filtro.cnpj" /> <input type="hidden" id="filtroRazaoSocial"
			name="filtro.razaoSocial" />
	</form>

	<form id="formRepresentada"
		action="<c:url value="/representada/inclusao"/>" method="post">
		<input type="hidden" id="codigo" name="representada.id"
			value="${representada.id}">


		<fieldset>
			<legend>::: Dados de Representadas :::</legend>
			<div class="label">Ativo:</div>
			<div class="input" style="width: 80%">
				<input type="checkbox" id="ativo" name="representada.ativo"
					<c:if test="${empty representada or representada.ativo}">checked="checked"</c:if>
					class="checkbox" />
			</div>
			
			<div class="label obrigatorio">Tipo de Relacionamento:</div>
			<div class="input">
				<input type="radio" name="representada.tipoRelacionamento" 
					value="REVENDA" <c:if test="${not empty representada and representada.tipoRelacionamento == 'REVENDA'}">checked</c:if>/>
			</div>
			<div class="label label_radio_button" style="width: 5%">Revenda</div>
			<div class="input">
				<input type="radio" name="representada.tipoRelacionamento"
					value="REPRESENTACAO" <c:if test="${not empty representada and representada.tipoRelacionamento == 'REPRESENTACAO'}">checked</c:if> />
			</div>
			<div class="label label_radio_button" style="width: 8%">Representação</div>
			<div class="input">
				<input type="radio" name="representada.tipoRelacionamento" 
					value="FORNECIMENTO" <c:if test="${not empty representada and representada.tipoRelacionamento == 'FORNECIMENTO'}">checked</c:if>/>
			</div>
			<div class="label label_radio_button" style="width: 8%">Fornecimento</div>
			<div class="input">
				<input type="radio" name="representada.tipoRelacionamento" 
					value="REPRESENTACAO_FORNECIMENTO" <c:if test="${not empty representada and representada.tipoRelacionamento == 'REPRESENTACAO_FORNECIMENTO'}">checked</c:if>/>
			</div>
			<div class="label label_radio_button" style="width: 40%">Represent. e Fornec.</div>
					
			<div class="label obrigatorio">Apresent. IPI:</div>
			<div class="input" style="width: 20%">
				<select id="tipoApresentacaoIPI"
					name="representada.tipoApresentacaoIPI" style="width: 90%">
					<c:forEach var="tipoApresentacaoIPI"
						items="${listaTipoApresentacaoIPI}">
						<option value="${tipoApresentacaoIPI}"
							<c:if test="${tipoApresentacaoIPI eq tipoApresentacaoIPISelecionada}">selected</c:if>>${tipoApresentacaoIPI}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio" style="width: 10%">Comissão (%):</div>
			<div class="input" style="width: 50%">
				<input type="text" id="comissao" name="representada.comissao"
					value="${representada.comissaoPercentual}" style="width: 10%" />
			</div>
			<div class="label obrigatorio">Nome Fantasia:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="nomeFantasia"
					name="representada.nomeFantasia"
					value="${representada.nomeFantasia}" class="pesquisavel"
					style="width: 60%" />
			</div>

			<div class="label obrigatorio">Razão Social:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="razaoSocial" name="representada.razaoSocial"
					value="${representada.razaoSocial}" class="pesquisavel"
					style="width: 60%" />
			</div>

			<div class="label">CNPJ:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="cnpj" name="representada.cnpj"
					value="${representada.cnpj}" style="width: 80%" class="pesquisavel" />
			</div>

			<div class="label" style="width: 11%">Insc. Estadual:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="inscricaoEstadual"
					name="representada.inscricaoEstadual"
					value="${representada.inscricaoEstadual}"
					style="width: 40%; text-align: right;" />
			</div>

			<div class="label">Site:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="site" name="representada.site"
					value="${representada.site}" style="width: 60%"
					class="apenasLowerCase uppercaseBloqueado lowerCase" />
			</div>

			<div class="label obrigatorio">Email Pedidos:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="email" name="representada.email"
					value="${representada.email}" style="width: 60%"
					class="apenasLowerCase uppercaseBloqueado lowerCase" />
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<a id="botaoPesquisarRepresentada"
				title="Pesquisar Dados da Representada" class="botaoPesquisar"></a>
			<a id="botaoLimpar" title="Limpar Dados do Cliente"
				class="botaoLimpar"></a>
		</div>
		<jsp:include page="/bloco/bloco_logradouro.jsp" />
		<jsp:include page="/bloco/bloco_contato.jsp" />

		<div class="bloco_botoes">
			<c:if test="${acessoCadastroBasicoPermitido}">
				<a id="botaoInserirRepresentada"
					title="Incluir Dados da Representada" class="botaoInserir"></a>
			</c:if>
		</div>

	</form>
	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Representadas :::</legend>
		<div id="paginador"></div>
		<div>
			<table id="tabela" class="listrada">
				<thead>
					<tr>
						<th style="width: 5%">Ativo</th>
						<th style="width: 12%">Relação</th>
						<th style="width: 15%">Nome</th>
						<th style="width: 35%">Razão Social</th>
						<th>CNPJ</th>
						<th>Ações</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="representada" items="${listaRepresentada}">
						<tr>
							<c:choose>
								<c:when test="${representada.ativo}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>
							<td>${representada.tipoRelacionamento.descricao}</td>
							<td>${representada.nomeFantasia}</td>
							<td>${representada.razaoSocial}</td>
							<td>${representada.cnpj}</td>
							<td>
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/representada/edicao"/>"
										method="get">
										<input type="submit" title="Editar Dados da Representada"
											value="" class="botaoEditar" /> <input type="hidden"
											name="id" value="${representada.id}">
									</form>
									<c:if test="${acessoCadastroBasicoPermitido}">
										<form action="<c:url value="/representada/desativacao"/>"
											method="post">
											<input type="hidden" name="idRepresentada"
												value="${representada.id}"> <input type="button"
												title="Desativar Representada" value="" class="botaoRemover"
												onclick="inicializarModalCancelamento(this);"/>
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
