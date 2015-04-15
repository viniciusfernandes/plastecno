<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Itens do Estoque</title>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/pedido/pedido.js"/>"></script>

<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');
	
	<c:if test="${not empty itemPedido.id}">
	habilitarCamposEdicaoItem(false);
	</c:if>
	
	
	$('#botaoLimpar').click(function () {
		$('#formVazio').submit();
	});
	
	$('#botaoInserirItemPedido').click(function () {

		if(isEmpty($('#idItemPedido').val())){
			inicializarModalConfirmacao({
				mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja ADICIONAR esse item, pois o estoque terá seu valor alterado?',
				confirmar: function(){
					$('#bloco_item_pedido #descricao').val($('#bloco_item_pedido #descricao').val().toUpperCase());
					var parametros = serializarFormPesquisa();
					parametros += '&'+serializarForm('bloco_item_pedido');
					
					var form = $('#formVazio');
					$(form).attr('method', 'post');
					$(form).attr('action', '<c:url value="/estoque/item/inclusao"/>?'+parametros);
					$(form).submit();
				}
			});
		} else {
			inicializarModalConfirmacao({
				mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja REDEFINIR esse item, pois o estoque terá seu valor alterado?',
				confirmar: function(){
					$('#bloco_item_pedido #descricao').val($('#bloco_item_pedido #descricao').val().toUpperCase());
					var parametros = serializarFormPesquisa();
					parametros += '&idItem='+$('#bloco_item_pedido #idItemPedido').val();
					parametros += '&quantidade='+$('#bloco_item_pedido #quantidade').val();
					parametros += '&aliquotaIPI='+$('#bloco_item_pedido #aliquotaIPI').val();
					parametros += '&aliquotaICMS='+$('#bloco_item_pedido #aliquotaICMS').val();
					parametros += '&preco='+$('#bloco_item_pedido #preco').val();
					
					var form = $('#formVazio');
					$(form).attr('method', 'post');
					$(form).attr('action', '<c:url value="/estoque/item/edicao"/>?'+parametros);
					$(form).submit();
				}
			});
		}
	});
	
	$('#botaoValorEstoque').click(function (){
		var parametros = serializarFormPesquisa();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/estoque/valor"/>?'+parametros);
		$(form).submit();
	});
	
	$('#botaoEscassezEstoque').click(function (){
		var parametros = serializarFormPesquisa();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/estoque/escassez"/>?'+parametros);
		$(form).submit();
	});
	
	inicializarAutocompleteMaterial();
	inicializarSelectFormaMaterial();
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
});

function inicializarAutocompleteMaterial() {
	// Esse eh o autocomplete da area de filtros de pesquisa
	autocompletar({
		url : '<c:url value="/estoque/material/listagem"/>',
		campoPesquisavel : 'formPesquisa #material',
		parametro : 'sigla',
		containerResultados : 'formPesquisa #containerPesquisaMaterial',
		selecionarItem : function(itemLista) {
			$('#formPesquisa #idMaterial').val(itemLista.id);
		}
	});
	
	// Esse eh o autocomplete da campo de materiais no bloco de edicao dos itens
	autocompletar({
		url : '<c:url value="/estoque/material/listagem"/>',
		campoPesquisavel : 'bloco_item_pedido #material',
		parametro : 'sigla',
		containerResultados : 'bloco_item_pedido #containerPesquisaMaterial',
		selecionarItem : function(itemLista) {
			$('#bloco_item_pedido #idMaterial').val(itemLista.id);
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
			<div class="label condicional" style="width: 30%">Forma Material:</div>
			<div class="input" style="width: 60%">
				<select name="formaMaterial" style="width: 20%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="forma" items="${listaFormaMaterial}">
						<option value="${forma}"
							<c:if test="${forma eq formaSelecionada}">selected</c:if>>${forma.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label condicional" style="width: 30%">Material:</div>
			<div class="input" style="width: 60%">
				<input type="hidden" id="idMaterial" name="material.id" value="${material.id}"/>
				<input type="text" id="material" name="material.descricao" value="${material.descricao}" style="width: 60%" />
				<div class="suggestionsBox" id="containerPesquisaMaterial" style="display: none; width: 50%"></div>
			</div>
			<div class="label" style="width: 30%">Valor total em estoque:</div>
			<div class="input" style="width: 60%">
				<input type="text" value="R$ ${empty valorEstoque ? '0,00' : valorEstoque}" disabled="disabled" 
				class="desabilitado" style="width: 60%"/>
			</div>
			
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar" />
			<input id="botaoEscassezEstoque" type="button" value="" class="botaoPesquisarEstatistica" title="Pesquisar Itens Esgotados Estoque"/>
			<c:if test="${acessoValorEstoquePermitido}">
				<input id="botaoValorEstoque" type="button" value="" class="botaoDinheiro" title="Pesquisar Valor dos Itens no Estoque"/> 
			</c:if>
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados do Item de Estoque" class="botaoLimpar" />
		</div>
	</form>
	
	<c:if test="${acessoManutencaoEstoquePermitido}">
		<jsp:include page="/bloco/bloco_edicao_item.jsp"/>
	</c:if>
	
	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Itens de Estoque :::</legend>
		<div id="paginador"></div>
		<div>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 10%">Qtde.</th>
						<th style="width: 70%">Descrição</th>
						<th style="width: 10%">Valor Unid. (R$)</th>
						<th style="width: 5%">Ações</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="item" items="${listaItemEstoque}">
						<tr>
							
							<td>${item.quantidade}</td>
							<td>${item.descricao}</td>
							<td>${item.precoMedioFormatado}</td>
							<td>
								<c:if test="${acessoManutencaoEstoquePermitido}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/estoque/item/${item.id}"/>" method="get">
										<input type="hidden" name="material.id" value="${material.id}"/>
										<input type="hidden" name="formaMaterial" value="${formaSelecionada}"/>
										<input type="submit" id="botaoEditarCliente" title="Editar Dados do Cliente" value="" class="botaoEditar"/>
									</form>
								</div>
								</c:if>
							</td>
						</tr>

					</c:forEach>
				</tbody>

			</table>
		</div>
	</fieldset>

</body>
</html>