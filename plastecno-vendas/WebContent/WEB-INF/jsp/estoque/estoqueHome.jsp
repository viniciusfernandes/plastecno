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
					var parametros = serializarPesquisa();
					parametros += serializarBloco('bloco_item_pedido');
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
					var parametros = serializarPesquisa();
					parametros += '&itemEstoque.id='+$('#bloco_item_pedido #idItemPedido').val();
					parametros += '&itemEstoque.formaMaterial='+$('#bloco_item_pedido #formaMaterial').val();
					parametros += '&itemEstoque.material.id='+$('#bloco_item_pedido #idMaterial').val();
					parametros += '&itemEstoque.quantidade='+$('#bloco_item_pedido #quantidade').val();
					parametros += '&itemEstoque.aliquotaIPI='+$('#bloco_item_pedido #aliquotaIPI').val();
					parametros += '&itemEstoque.aliquotaICMS='+$('#bloco_item_pedido #aliquotaICMS').val();
					parametros += '&itemEstoque.precoMedio='+$('#bloco_item_pedido #precoMedio').val();
					parametros += '&itemEstoque.quantidadeMinima='+$('#bloco_item_pedido #quantidadeMinima').val();
					parametros += '&itemEstoque.margemMinimaLucro='+$('#bloco_item_pedido #margemMinimaLucro').val();
					
					var form = $('#formVazio');
					$(form).attr('method', 'post');
					$(form).attr('action', '<c:url value="/estoque/item/edicao"/>?'+parametros);
					$(form).submit();
				}
			});
		}
	});
	
	$('#botaoValorEstoque').click(function (){
		var parametros = serializarPesquisa();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/estoque/valor"/>?'+parametros);
		$(form).submit();
	});
	
	$('#botaoEscassezEstoque').click(function (){
		var parametros = serializarPesquisa();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/estoque/escassez"/>?'+parametros);
		$(form).submit();
	});
	
	$('#botaoPesquisar').click(function (){
		var parametros = serializarPesquisa();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/estoque/item/listagem"/>?'+parametros);
		$(form).submit();
	});
	
	inicializarAutocompleteMaterial();
	inicializarSelectFormaMaterial();
	
});

function serializarPesquisa(){
	var parametros = '';
	var val = $('#bloco_pesquisa #idMaterial').val();
	
	if(!isEmpty(val)){
		parametros = 'material.id='+val;
	}
	
	val = $('#bloco_pesquisa #formaMaterial').val();
	if(!isEmpty(val)){
		parametros += '&formaMaterial='+val;
	}
	return parametros;
};

function inicializarAutocompleteMaterial() {
	// Esse eh o autocomplete da area de filtros de pesquisa
	autocompletar({
		url : '<c:url value="/estoque/material/listagem"/>',
		campoPesquisavel : 'bloco_pesquisa #material',
		parametro : 'sigla',
		containerResultados : 'bloco_pesquisa #containerPesquisaMaterial',
		selecionarItem : function(itemLista) {
			$('#bloco_pesquisa #idMaterial').val(itemLista.id);
			$('#botaoPesquisar').click();
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
	
	// Esse eh o autocomplete da campo de materiais no bloco de limite minimo de estoque
	autocompletar({
		url : '<c:url value="/estoque/material/listagem"/>',
		campoPesquisavel : 'bloco_limite_minimo #material',
		parametro : 'sigla',
		containerResultados : 'bloco_limite_minimo #containerPesquisaMaterial',
		selecionarItem : function(itemLista) {
			$('#bloco_limite_minimo #idMaterial').val(itemLista.id);
		}
	});
};

function inicializarAutocompleteDescricaoPeca() {
	autocompletar({
		url : '<c:url value="/estoque/descricaopeca"/>',
		campoPesquisavel : 'bloco_limite_minimo #descricao',
		parametro : 'descricao',
		containerResultados : 'bloco_limite_minimo #containerPesquisaDescricaoPeca',
		selecionarItem : function(itemLista) {
			$('#bloco_limite_minimo #idLimiteMinimo').val(itemLista.id);
		}
	});
	
	autocompletar({
		url : '<c:url value="/estoque/descricaopeca"/>',
		campoPesquisavel : 'bloco_item_pedido #descricao',
		parametro : 'descricao',
		containerResultados : 'bloco_item_pedido #containerPesquisaDescricaoPeca',
		selecionarItem : function(itemLista) {
			$('#bloco_item_pedido #idItemPedido').val(itemLista.id);
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

		<fieldset id="bloco_pesquisa">
			<legend>::: Pesquisa de Itens do Estoque :::</legend>
			<div class="label condicional" style="width: 30%">Forma Material:</div>
			<div class="input" style="width: 60%">
				<select id="formaMaterial" name="formaMaterial" style="width: 20%">
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
				<input type="text" id="material" name="material.descricao" value="${material.descricaoFormatada}" style="width: 60%" />
				<div class="suggestionsBox" id="containerPesquisaMaterial" style="display: none; width: 50%"></div>
			</div>
			<div class="label" style="width: 30%">Valor total em estoque (R$):</div>
			<div class="input" style="width: 60%">
				<input type="text" name="valorEstoque" value="${empty valorEstoque ? '0,00' : valorEstoque}" disabled="disabled" 
				class="desabilitado" style="width: 60%"/>
			</div>
			
		</fieldset>
		<div class="bloco_botoes">
			<input type="button" id="botaoPesquisar" value="" class="botaoPesquisar" title="Pesquisar Itens Estoque"/>
			<c:if test="${acessoManutencaoEstoquePermitido}">
				<input id="botaoEscassezEstoque" type="button" value="" class="botaoPesquisarEstatistica" title="Pesquisar Itens Escassos Estoque"/>
			</c:if>
			<c:if test="${acessoValorEstoquePermitido}">
				<input id="botaoValorEstoque" type="button" value="" class="botaoDinheiro" title="Pesquisar Valor dos Itens no Estoque"/> 
			</c:if>
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados do Item de Estoque" class="botaoLimpar" />
		</div>
	
	<c:if test="${acessoManutencaoEstoquePermitido}">
		<jsp:include page="/bloco/bloco_edicao_item.jsp"/>
	</c:if>
	
	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Itens de Estoque :::</legend>
		<div>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 20%">Material</th>
						<th style="width: 5%">Qtde.</th>
					<c:if test="${acessoManutencaoEstoquePermitido}">
						<th style="width: 5%">Qtde Min.</th>
					</c:if>
						<th style="width: 10%">Med. Externa</th>
						<th style="width: 10%">Med. Interna</th>
						<th style="width: 10%">Comprimento</th>
					<c:if test="${acessoManutencaoEstoquePermitido}">
						<th style="width: 10%">Valor Unid. (R$)</th>
						<th style="width: 10%">Marg. Min. (%)</th>
					</c:if>
						<th style="width: 10%">Preç. Min. (R$)</th>
						<th style="width: 5%">Ações</th>
					</tr>
				</thead>

				<tbody>
				
				<c:forEach items="${relatorio.listaGrupo}" var="grupo" varStatus="iGrupo">
					<c:forEach items="${grupo.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento}">${grupo.id}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
						
						<c:if test="${acessoManutencaoEstoquePermitido}">
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidadeMinima}</td>
						</c:if>
						
							<c:choose>
								<c:when test="${item.peca}">
									<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" colspan="3">${item.descricaoPeca}</td>
								</c:when>
								<c:otherwise>
									<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.medidaExterna}</td>
									<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.medidaInterna}</td>
									<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.comprimento}</td>
								</c:otherwise>
							</c:choose>
							
						<c:if test="${acessoManutencaoEstoquePermitido}">
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoMedio}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.margemMinimaLucro}</td>
						</c:if>	
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoMinimo}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<c:if test="${acessoManutencaoEstoquePermitido}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/estoque/item/${item.id}"/>" method="post">
										<!-- Esse dados foram submetidos para manter o estado da tela durante o filtro  -->
										<input type="hidden" name="material.id" value="${material.id}"/>
										<input type="hidden" name="formaMaterial" value="${formaSelecionada}"/>
										<input type="submit" title="Editar Item do Estoque" value="" class="botaoEditar"/>
									</form>
								</div>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
				
				</tbody>

			</table>
		</div>
	</fieldset>

</body>
</html>