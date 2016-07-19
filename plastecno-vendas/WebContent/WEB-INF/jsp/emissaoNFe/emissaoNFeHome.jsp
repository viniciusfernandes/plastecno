<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>

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
	

	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
});

function inicializarFiltro() {
	$("#filtroSigla").val($("#sigla").val());
	$("#filtroDescricao").val($("#descricao").val());	
}

function inicializarModalCancelamento(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja DESATIVAR esse ramo de atividade?',
		confirmar: function(){
			$(botao).closest('form').submit();	
		}
	});
}
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
	<form id="formVazio" action="ramo" method="get"></form>

	<form id="formPesquisa" action="<c:url value="/ramo/listagem"/>"
		method="get">
		<input type="hidden" id="filtroSigla" name="filtro.sigla" /> <input
			type="hidden" id="filtroDescricao" name="filtro.descricao" />
	</form>
	<form id="formRamo" action="<c:url value="/ramo/inclusao"/>"
		method="post">
		<input type="hidden" id="codigo" name="ramoAtividade.id"
			value="${ramoAtividade.id}">

		<fieldset>
			<legend>::: Dados da NF-e :::</legend>
			<div class="label obrigatorio">Tipo Documento:</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" name="nf.identificacaoNFe"
					style="width: 13%" class="semprehabilitado">
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Forma Pagamento:</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" name="nf.identificacaoNFe.indicadorFormaPagamento"
					style="width: 13%" class="semprehabilitado">
					<c:forEach var="formaPagamento" items="${listaTipoFormaPagamento}">
						<option value="${formaPagamento.codigo}" <c:if test="${formaPagamento eq formaPagamentoPadrao}">selected</c:if>>${formaPagamento.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Forma Emissão:</div>
			<div class="input" style="width: 80%">
				<select name="nf.identificacaoNFe.tipoEmissao"
					style="width: 13%" class="semprehabilitado">
					<c:forEach var="tipoEmissao" items="${listaTipoEmissao}">
						<option value="${tipoEmissao.codigo}" <c:if test="${tipoEmissao eq tipoEmissaoPadrao}">selected</c:if>>${tipoEmissao.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Finalidade Emissão:</div>
			<div class="input" style="width: 80%">
				<select name="nf.finalidadeEmissao"
					style="width: 13%" class="semprehabilitado">
					<c:forEach var="finalidade" items="${listaTipoFinalidadeEmissao}">
						<option value="${finalidade.codigo}" <c:if test="${finalidade eq finalidadeEmissaoPadrao}">selected</c:if>>${finalidade.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Tipo Impressão:</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" name="idPedidoAssociado"
					style="width: 13%" class="semprehabilitado">
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Consumidor Final:</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" name="idPedidoAssociado"
					style="width: 13%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Destino Operação:</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" name="idPedidoAssociado"
					style="width: 13%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Tipo Atendimento:</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" name="idPedidoAssociado"
					style="width: 13%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Natureza Operação:</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" name="idPedidoAssociado"
					style="width: 13%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<c:if test="${acessoCadastroBasicoPermitido}">
				<a id="botaoInserirRamo" title="Incluir Dados do Ramo de Atividades"
					class="botaoInserir"></a>
			</c:if>
			<a id="botaoPesquisarRamo"
				title="Pesquisar Dados do Ramo de Atividades" class="botaoPesquisar"></a>

			<a id="botaoLimpar" title="Limpar Dados do Ramo de Atividades"
				onclick="limparFormulario();" class="botaoLimpar"></a>
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
										<input type="submit" id="botaoEditarUsuario"
											title="Editar Dados do Usuario" value="" class="botaoEditar" />
									</form>
									<c:if test="${acessoCadastroBasicoPermitido}">
										<form action="<c:url value="/ramo/desativacao"/>"
											method="post">

											<input type="hidden"  name="id" value="${ramo.id}"> 
											<input type="button" title="Desativar Ramo de Atividades" value=""
												class="botaoRemover" onclick="inicializarModalCancelamento(this);" />

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
