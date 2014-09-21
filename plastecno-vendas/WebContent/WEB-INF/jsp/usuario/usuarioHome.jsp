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
<script type="text/javascript" src="<c:url value="/js/tabela_handler.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/logradouro.js"/>"></script>


<script type="text/javascript">

var tabelaContatoHandler = null;
var tabelaLogradouroHandler = new BlocoTabelaHandler('Logradouro', null, 'bloco_logradouro');
$(document).ready(function() {

	scrollTo('${ancora}');
	
	
	$("#botaoInserirUsuario").click(function() {
		var isSenhaEmBranco = isEmpty($('#senha').val()) || isEmpty($('#senhaConfirmada').val());
		if (($('#isAlteracaoSenha').prop('checked') || isEmpty($('#id').val())) && isSenhaEmBranco) {
			gerarListaMensagemErro(new Array('As senhas não podem estar em branco'));
			return;
		} 
		
		if ($('#isAlteracaoSenha').prop('checked') && $('#senha').val() != $('#senhaConfirmada').val()) {
			gerarListaMensagemErro(new Array('As senhas não conferem, favor digitar novamente'));
			return;
		}
		
		toUpperCaseInput();
		toLowerCaseInput();
		
		var listaId = ['cpf'];
		removerNaoDigitos(listaId);

		var parametros = pickListToParameter('listaIdPerfilAssociado');
		parametros += tabelaContatoHandler.gerarListaParametro('listaContato');
		parametros += tabelaLogradouroHandler.gerarParametroInputsBloco(); 
		
		$('#formUsuario').attr("action",$('#formUsuario').attr('action')+'?'+ parametros);
		$('#formUsuario').submit();
						
	});
	
	$("#botaoPesquisarUsuario").click(function() {
		var listaId = ['cpf'];
		removerNaoDigitos(listaId);
		toUpperCaseInput();
		inicializarFiltro();					
		$('#formPesquisa').submit();
	});

	$("#botaoLimpar").click(function() {
		$('#formVazio').submit();
	});
	
	inserirMascaraCPF('cpf');
	
	inicializarPaginador(
			'<c:out value="${not empty paginaSelecionada ? paginaSelecionada : 0}"/>', 
			'<c:out value="${not empty totalPaginas ? totalPaginas : 1}"/>');
	tabelaContatoHandler = inicializarBlocoContato('<c:url value="/usuario"/>');
	
	new PickList().initPickList();
	
});

function inicializarFiltro () {
	$("#filtro_nome").val($("#nome").val());
	$("#filtro_sobrenome").val($("#sobrenome").val());
	$("#filtro_cpf").val($("#cpf").val());
	$("#filtro_email").val($("#email").val());	
};

</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />


	<form id="formPesquisa" action="usuario/listagem" method="get">
		<input type="hidden" id="filtro_nome" name="filtro.nome" /> 
		<input type="hidden" id="filtro_sobrenome" name="filtro.sobrenome" /> 
		<input type="hidden" id="filtro_email" name="filtro.email" /> 
		<input type="hidden" id="filtro_cpf" name="filtro.cpf" />
	</form>
	<form id="formVazio" action="usuario" method="get">
	</form>
	
		<fieldset>
		<legend>::: Dados do Usuario :::</legend>
		<form id="formUsuario" action="usuario/inclusao" method="post">
			<input type="hidden" id="id" name="usuario.id" value="${usuario.id}" />
			
			<div class="label" >Ativo:</div>
			<div class="input" style="width: 80%">
				<input type="checkbox" id="ativo" name="usuario.ativo" <c:if test="${usuario.ativo}">checked</c:if> class="checkbox"/>
			</div>
			<div class="label" >Vendedor:</div>
			<div class="input" style="width: 80%">
				<input type="checkbox" id="vendedorAtivo" name="usuario.vendedorAtivo" <c:if test="${usuario.vendedorAtivo}">checked</c:if> class="checkbox" />
			</div>
			<div class="label obrigatorio" >Nome:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="nome" name="usuario.nome"
					value="${usuario.nome}" class="pesquisavel" />
			</div>
			<div class="label obrigatorio">Sobrenome:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="sobrenome" name="usuario.sobrenome"
					value="${usuario.sobrenome}" class="pesquisavel" style="width: 80%" />
			</div>
			<div class="label">CPF:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="cpf" name="usuario.cpf"
					value="${usuario.cpf}" class="pesquisavel" />
			</div>
			<div class="label obrigatorio" >Email:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="email" name="usuario.email" value="${usuario.email}" style="width: 80%" 
					class="apenasLowerCase uppercaseBloqueado pesquisavel lowerCase" />
			</div>
			<div class="label" >Cadastrar senha:</div>
			<div class="input" style="width: 80%">
				<input type="checkbox" id="isAlteracaoSenha" name="isAlteracaoSenha" class="checkbox"></input>
			</div>
			<div class="label obrigatorio" >Senha:</div>
			<div class="input" style="width: 80%">
				<input type="password" id="senha" name="usuario.senha" style="width: 20%" class="uppercaseBloqueado"/>
			</div>
			<div class="label obrigatorio" >Confirmacao Senha:</div>
			<div class="input" style="width: 80%">
				<input type="password" id="senhaConfirmada" style="width: 20%" class="upperCaseBloqueado"/>
			</div>
		</form>
		</fieldset>
	<div class="bloco_botoes">
		<a id="botaoPesquisarUsuario" title="Pesquisar Dados do Usuario" class="botaoPesquisar"></a>
		<a id="botaoLimpar" title="Limpar Dados do Usuario" class="botaoLimpar"></a>	
	</div>
	
	<jsp:include page="/bloco/bloco_picklist.jsp" />
	<jsp:include page="/bloco/bloco_logradouro.jsp" />
	<jsp:include page="/bloco/bloco_contato.jsp" />

	<div class="bloco_botoes">
		<c:if test="${acessoCadastroBasicoPermitido}">
			<a id="botaoInserirUsuario" title="Incluir Dados do Usuario" class="botaoInserir"></a>
		</c:if>
	</div>

	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Usuarios :::</legend>
		<div id="paginador"></div>
		<div>
			<form id="formUsuarioRemocao"></form>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 2%">Vendedor</th>
						<th style="width: 2%">Ativo</th>
						<th style="width: 25%">Nome</th>
						<th style="width: 20%">Sobrenome</th>
						<th style="width: 15%">CPF</th>
						<th style="width: 25%">Email</th>
						<th>Ações</th>
					</tr>
				</thead>
	
				<tbody>
					<c:forEach var="usuario" items="${listaUsuario}">
						<tr>
							<c:choose>
								<c:when test="${usuario.vendedorAtivo}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>					
							<c:choose>
								<c:when test="${usuario.ativo}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>
	
							<td>${usuario.nome}</td>
							<td>${usuario.sobrenome}</td>
							<td>${usuario.cpf}</td>
							<td>${usuario.email}</td>
							<td>
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/usuario/edicao"/>" method="get">
										<input type="submit" title="Editar Dados do Usuario" value="" class="botaoEditar" />
										<input type="hidden" name="id" value="${usuario.id}" /> 
									</form>
									<c:if test="${acessoCadastroBasicoPermitido}">
										<form action="<c:url value="/usuario/desativacao"/>" method="post">
											<input type="hidden" name="idUsuario" value="${usuario.id}">
											<input type="submit" title="Desativar Usuário" value="" class="botaoRemover"
												onclick="javascript: return confirm('Voce deseja mesmo desativar o USUARIO?');"/>
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
