<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>

<jsp:include page="/bloco/bloco_css.jsp"></jsp:include>

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/picklist.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/tabela_handler.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/logradouro.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bloco/contato.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>

<script type="text/javascript">


var tabelaLogradouroHandler = null;
var tabelaContatoHandler = null; 

$(document).ready(function() {
	scrollTo('${ancora}');
	
	$("#botaoInserirCliente").click(function() {
		toUpperCaseInput();
		toLowerCaseInput();
		
		var listaId = ['cnpj', 'cpf', 'inscricaoEstadual'];
		removerNaoDigitos(listaId);
		var parametros = tabelaLogradouroHandler.gerarListaParametro('listaLogradouro');
		parametros += tabelaContatoHandler.gerarListaParametro('listaContato');
		parametros += pickListToParameter('listaIdTransportadoraAssociada');
		
		$('#formCliente').attr("action",$('#formCliente').attr("action")+'?'+ parametros);
		$('#formCliente').submit();
						
	});	
	
	$("#botaoPesquisarCliente").click(function() {
		var listaId = ['cnpj', 'cpf'];
		removerNaoDigitos(listaId);
		toUpperCaseInput();
		inicializarFiltro();		
		$('#formPesquisa').submit();
	});

	$("#botaoLimpar").click(function() {
		<c:if test="${isRevendedor}">
		$('#formVazio').attr('action', '<c:url value="/revendedor"/>');
		</c:if>		
		$('#formVazio').submit();
	});
	
	$("#botaoIncluirComentario").click(function () {
		if(!isEmpty($("#comentario").val())) {
			$(this).closest('form').submit();			
		}
	});
	
	$("#botaoLimparComentario").click(function () {
		$("#comentario").val("");	
	});
	
	inserirMascaraCNPJ('cnpj');
	inserirMascaraCPF('cpf');
	inserirMascaraInscricaoEstadual('inscricaoEstadual');
	inicializarCampoPesquisaPicklist({
		url: '<c:url value="/cliente/transportadora"/>', 
		mensagemEspera: 'CARREGANDO AS TRANSPORTADORAS ...',
		parametro: 'nomeFantasia'
	});
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
	var urlTela = '<c:url value="/cliente"/>'; 
	tabelaLogradouroHandler = inicializarBlocoLogradouro(urlTela);
	tabelaContatoHandler = inicializarBlocoContato(urlTela);
	
	new PickList().initPickList();
	var urlCliente  = '';
	autocompletar({
		url : '<c:url value="/cliente/listagem/nome"/>',
		campoPesquisavel : 'nomeFantasia',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaCliente',
		selecionarItem: function(itemLista) {
			var formVazio = document.getElementById('formVazio');
			formVazio.action = '<c:url value="/cliente/"/>'+itemLista.id;
			formVazio.submit();
		}
	});

});

function inicializarFiltro() {
	$("#filtro_nomeFantasia").val($("#nomeFantasia").val());
	$("#filtro_cnpj").val($("#cnpj").val());
	$("#filtro_cpf").val($("#cpf").val());
	$("#filtro_email").val($("#email").val());
}

function contactarCliente (idCliente) {
	$('#formContactarCliente #idClienteContactado').val(idCliente);
	$('#formContactarCliente').submit();
}


function remover(codigo, nome) {
	var remocaoConfirmada = confirm("Voce tem certeza de que deseja desabilitar o cliente \""+nome+"\"?");
	if (remocaoConfirmada) {
		$("#formClienteRemocao").attr("action","<c:url value="/cliente/remocao"/>?id="+codigo);
		$("#formClienteRemocao").attr("method","post");
		$('#formClienteRemocao').submit();
	}	
};

</script>

</head>
<body>

	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formPesquisa" action="cliente/listagem" method="get">
		<input type="hidden" id="filtro_nomeFantasia" name="filtro.nomeFantasia" /> 
		<input type="hidden" id="filtro_email" name="filtro.email" /> 
		<input type="hidden" id="filtro_cnpj" name="filtro.cnpj" /> 
		<input type="hidden" id="filtro_cpf" name="filtro.cpf" />
	</form>


	<form id="formContactarCliente" action="cliente/contactar" method="post">
		<input id="idClienteContactado" name="idClienteContactado" type="hidden" />
	</form>

	<form id="formVazio" action="cliente" method="get">
		<input type="hidden" id="isRevendedor" name="isRevendedor" value="${isRevendedor}" />
	</form>

	<fieldset>
		<legend>::: Dados do ${isRevendedor ? 'Revendedor' : 'Cliente'} :::</legend>

		<form id="formCliente" action="<c:url value="/cliente/inclusao"/>"
			method="post">
			<input type="hidden" id="id" name="cliente.id" value="${cliente.id}" />
			<input type="hidden" id="idVendedor" name="cliente.vendedor.id" value="${cliente.vendedor.id}" />
			<input type="hidden" id="isRevendedor" name="isRevendedor" value="${isRevendedor}" />

			<div class="label">Último Contato:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="ultimoContato" value="${ultimoContato}"
					readonly="readonly" class="desabilitado" />
			</div>
			<div class="label">Vendedor:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="vendedor"
					value="${cliente.vendedor.nomeCompleto} - ${cliente.vendedor.email}" disabled="disabled" class="uppercaseBloqueado desabilitado"
					style="width: 80%" />
			</div>

			<div class="label obrigatorio">Ramo Atividade:</div>
			<div class="input" style="width: 80%">
				<select id="ramoAtividade" name="cliente.ramoAtividade.id"
					style="width: 25%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="ramoAtividade" items="${listaRamoAtividade}">
						<option value="${ramoAtividade.id}"
							<c:if test="${ramoAtividade.id eq ramoAtividadeSelecionado}">selected</c:if>>${ramoAtividade.sigla}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Nome:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="nomeFantasia" name="cliente.nomeFantasia" value="${cliente.nomeFantasia}" class="pesquisavel" />
				<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
			</div>
			<div class="label obrigatorio">Razão Social:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="razaoSocial" name="cliente.razaoSocial"
					value="${cliente.razaoSocial}" style="width: 80%" />
			</div>
			<div class="label">CNPJ:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="cnpj" name="cliente.cnpj"
					value="${cliente.cnpj}" class="pesquisavel" />
			</div>
			<div class="label">Insc. Estadual:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="inscricaoEstadual"
					name="cliente.inscricaoEstadual"
					value="${cliente.inscricaoEstadual}"
					style="width: 40%; text-align: right;" />
			</div>
			<div class="label">CPF:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="cpf" name="cliente.cpf"
					value="${cliente.cpf}" class="pesquisavel" style="width: 25%" />
			</div>
			<div class="label obrigatorio">Email Envio NFe:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="email" name="cliente.email"
					value="${cliente.email}"
					class="apenasLowerCase uppercaseBloqueado lowerCase pesquisavel" />
			</div>

			<div class="label">Site:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="site" name="cliente.site"
					value="${cliente.site}"
					class="apenasLowerCase uppercaseBloqueado lowerCase"
					style="width: 80%" />
			</div>
		</form>
	</fieldset>
	<div class="bloco_botoes">
		<c:if test="${not isRevendedor}">
		<a id="botaoPesquisarCliente" title="Pesquisar Dados do Cliente" class="botaoPesquisar"></a> 
		</c:if>
		<a id="botaoLimpar" title="Limpar Dados do Cliente" class="botaoLimpar"></a>
		<c:if test="${not empty cliente.id}">
			<a id="botaoContactarCliente" title="Cliente Contactado"
				onclick="contactarCliente(${cliente.id});" class="botaoContactar"></a>
		</c:if>
	</div>
	
	<c:if test="${not isRevendedor}">
	<jsp:include page="/bloco/bloco_contato.jsp" />
	<fieldset id="bloco_comentario">
		<legend>::: Comentários :::</legend>
		<form action="<c:url value="/cliente/inclusao/comentario?isRevendedor=${isRevendedor}"/>"
			method="post">
			<input type="hidden" value="${cliente.id}" name="idCliente" />
			<div class="label condicional">Comentário:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="comentario" name="comentario"
					value="${comentario}" style="width: 100%" />
			</div>

			<div class="bloco_botoes">
				<c:if test="${acessoInclusaoPermitido}">
					<a id="botaoIncluirComentario"
						title="Adicionar Dados do Comentario" class="botaoAdicionar"></a>
					<a id="botaoLimparComentario" title="Limpar Dados do Comentario"
						class="botaoLimpar"></a>
				</c:if>
			</div>

		</form>
		<div class="label condicional">Histórico:</div>
		<div class="input areatexto" style="width: 80%">
			<textarea style="width: 100%;" disabled="disabled">
				${comentarios}
				</textarea>
		</div>
	</fieldset>
	</c:if>
	<jsp:include page="/bloco/bloco_logradouro.jsp" />
	<jsp:include page="/bloco/bloco_picklist.jsp" />
	

	<div class="bloco_botoes">
		<c:if test="${acessoInclusaoPermitido}">
			<a id="botaoInserirCliente" title="${isRevendedor ? 'Definir como Revendedor' :'Incluir Dados do Cliente'}" class="botaoInserir"></a>
		</c:if>
	</div>

	<c:if test="${not isRevendedor}">
	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Clientes :::</legend>
		<div id="paginador"></div>
		<div>
			<form id="formClienteRemocao"></form>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 25%">Nome</th>
						<th style="width: 20%">Email</th>
						<th style="width: 10%">CNPJ</th>
						<th style="width: 10%">CPF</th>
						<th style="width: 20%">Vendedor</th>
						<th>Ações</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="cliente" items="${listaCliente}">
						<tr>
							<td>${cliente.nomeFantasia}</td>
							<td>${cliente.email}</td>
							<td>${cliente.cnpj}</td>
							<td>${cliente.cpf}</td>
							<td>${cliente.vendedor.nomeCompleto}</td>
							<td>
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/cliente/${cliente.id}"/>"
										method="get">
										<input type="submit" id="botaoEditarCliente"
											title="Editar Dados do Cliente" value="" class="botaoEditar" />
									</form>
								</div>
							</td>
						</tr>

					</c:forEach>
				</tbody>

			</table>
		</div>
	</fieldset>
	</c:if>
</body>
</html>

