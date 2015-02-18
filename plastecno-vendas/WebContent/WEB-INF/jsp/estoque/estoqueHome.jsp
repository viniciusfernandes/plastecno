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
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>

<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');
	
	<c:if test="${not empty itemPedido.id}">
	habilitarCamposEdicaoItem(false);
	</c:if>
	
	
	$('#botaoLimpar').click(function () {
		$('#formVazio').submit();
	});
	
	$('#botaoLimparItemPedido').click(function () {
		$('#bloco_item_pedido input:text').val('');
		$('#formaMaterial').val('');
		$('#idItemPedido').val('');
		habilitarCamposEdicaoItem(true);
	});
	
	$('#botaoInserirItemPedido').click(function () {
		/*
		if(isEmpty($('#idItemPedido').val())){
			return;
		}
		*/
		var parametros = $('#bloco_item_pedido').serialize();
		parametros = $('#bloco_item_pedido').serialize();
		parametros += '&'+$('#formPesquisa').serialize();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/estoque/item/edicao"/>?'+parametros);
		$(form).submit();
	});
	
	$('#botaoRefazerItemPedido').click(function () {
		if(isEmpty($('#idItemPedido').val())){
			return;
		}
		inicializarModalConfirmacao({
			mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja REDEFINIR esse item, pois o estoque terá seu valor alterado?',
			confirmar: function(){
				var parametros = $('#bloco_item_pedido').serialize();
				parametros = $('#bloco_item_pedido').serialize();
				parametros += '&'+$('#formPesquisa').serialize();
				var form = $('#formVazio');
				$(form).attr('method', 'post');
				$(form).attr('action', '<c:url value="/estoque/item/redefinicao"/>?'+parametros);
				$(form).submit();	
			}
		});
	});
	
	inicializarAutocompleteMaterial();
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
});

function habilitarCamposEdicaoItem(habilitado){
	habilitar('#bloco_item_pedido #formaMaterial', habilitado);
	habilitar('#bloco_item_pedido #descricao', habilitado);
	habilitar('#bloco_item_pedido #material', habilitado);
	habilitar('#bloco_item_pedido #medidaExterna', habilitado);
	habilitar('#bloco_item_pedido #medidaInterna', habilitado);
	habilitar('#bloco_item_pedido #comprimento', habilitado);
};

function inicializarAutocompleteMaterial() {
	autocompletar({
		url : '<c:url value="/estoque/material/listagem"/>',
		campoPesquisavel : 'material',
		parametro : 'sigla',
		containerResultados : 'containerPesquisaMaterial',
		selecionarItem : function(itemLista) {
			$('#idMaterial').val(itemLista.id);
		}
	});
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

	<form id="formVazio" action="<c:url value="/estoque"/>">
	</form>


	<form id="formPesquisa" action="<c:url value="/estoque/item/listagem"/>" method="get">
		<fieldset>
			<legend>::: Pesquisa de Itens do Estoque :::</legend>
			<div class="label" style="width: 30%">Forma Material:</div>
			<div class="input" style="width: 60%">
				<select name="formaMaterial" style="width: 20%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="forma" items="${listaFormaMaterial}">
						<option value="${forma}"
							<c:if test="${forma eq formaSelecionada}">selected</c:if>>${forma.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label" style="width: 30%">Material:</div>
			<div class="input" style="width: 60%">
				<input type="hidden" id="idMaterial" name="idMaterial" value="${idMaterial}"/>
				<input type="text" id="material" name="descricaoMaterial" value="${descricaoMaterial}" style="width: 60%" />
				<div class="suggestionsBox" id="containerPesquisaMaterial" style="display: none; width: 50%"></div>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados do Item de Estoque" class="botaoLimpar" />
		</div>
	</form>
	
	<jsp:include page="/bloco/bloco_edicao_item_compra.jsp"/>
		
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
						<th style="width: 70%">Descrição</th>
						<th style="width: 10%">Valor (R$)</th>
						<th style="width: 5%">Ações</th>
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
									<form action="<c:url value="/estoque/item/${item.id}"/>">
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