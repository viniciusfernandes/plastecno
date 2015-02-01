<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Itens do Estoque</title>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>

<script type="text/javascript">

$(document).ready(function() {
	$('#botaoLimpar').click(function () {
		$('#formVazio').submit();
	});
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
});


function removerItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja REMOVER esse item do pedido de compra?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};


function recepcionarItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja RECEPCIONAR esse item do pedido de compra?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};

function submeterForm(botao){
	var parametros = $('#formPesquisa').serialize();
	var form = $(botao).closest('form');
	var action = $(form).attr('action')+'?'+parametros;
	$(form).attr('action', action).submit();
};

function inicializarFiltro() {
	$("#filtro_nomeFantasia").val($("#nomeFantasia").val());
	$("#filtro_cnpj").val($("#cnpj").val());
	$("#filtro_cpf").val($("#cpf").val());
	$("#filtro_email").val($("#email").val());
};
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" action="<c:url value="/compra/recepcao"/>">
	</form>


	<form id="formPesquisa" action="<c:url value="/estoque/item/listagem"/>" method="get">
		<fieldset>
			<legend>::: Itens do Estoque :::</legend>
			<div class="label" style="width: 30%">Forma Material:</div>
			<div class="input" style="width: 60%">
				<select name="idRepresentada" style="width: 20%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="forma" items="${listaFormaMaterial}">
						<option value="${forma}"
							<c:if test="${forma eq formaSelecionada}">selected</c:if>>${forma.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label" style="width: 30%">Material:</div>
			<div class="input" style="width: 60%">
				<input type="text" id="material" name="material.id" style="width: 60%" />
				<div class="suggestionsBox" id="containerPesquisaMaterial"
					style="display: none; width: 50%"></div>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados do Item de Estoque" class="botaoLimpar" />
		</div>
	</form>
	
	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Itens de Estoque :::</legend>
		<div id="paginador"></div>
		<div>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 5%">Cód.</th>
						<th style="width: 10%">Qtde.</th>
						<th style="width: 50%">Descrição</th>
						<th style="width: 10%">Valor (R$)</th>
						<th>Ações</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="item" items="${listaItemEstoque}">
						<tr>
							
							<td>${item.id}</td>
							<td>${item.quantidade}</td>
							<td>${item.descricao}</td>
							<td>${item.precoMedioFormatado}</td>
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

</body>
</html>