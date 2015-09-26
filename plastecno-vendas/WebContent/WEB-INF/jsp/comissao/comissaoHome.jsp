<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bloco/contato.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>


<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');

	$("#botaoPesquisarVendedor").click(function() {
		$('#formVazio').attr('action', '<c:url value="/comissao/vendedor/listagem"/>');
		$('#formVazio').submit();
	});
	
	$("#botaoPesquisarProduto").click(function() {
		var parametros = serializarBloco('bloco_comissao_produto');
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/comissao/produto/listagem"/>?'+parametros);
		$(form).submit();
	});
	
	$("#botaoInserirVendedor").click(function() {
		var parametros = serializarBloco('bloco_comissao_vendedor');
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/comissao/vendedor/inclusao"/>?'+parametros);
		$(form).submit();
	});
	
	$("#botaoInserirProduto").click(function() {
		var parametros = serializarBloco('bloco_comissao_produto');
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/comissao/produto/inclusao"/>?'+parametros);
		$(form).submit();
	});
	
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
	autocompletar({
		url : '<c:url value="/comissao/vendedor/listagem/nome"/>',
		campoPesquisavel : 'nome',
		parametro : 'nome',
		containerResultados : 'containerPesquisaVendedor',
		selecionarItem: function(itemLista) {
			$('#formVazio #idVendedor').val(itemLista.id);
			$('#formVazio').attr('action', '<c:url value="/comissao/vendedor"/>');
			$('#formVazio').submit();		
		}
	});
	
	autocompletar({
		url : '<c:url value="/estoque/material/listagem"/>',
		campoPesquisavel : 'material',
		parametro : 'sigla',
		containerResultados : 'containerPesquisaMaterial',
		selecionarItem : function(itemLista) {
			$('#idMaterial').val(itemLista.id);
		}
	});
	
	inserirMascaraNumerica('comissaoRevenda', '99');
	inserirMascaraNumerica('comissaoRepresentacao', '99');
	inserirMascaraNumerica('comissaoProduto', '99');
});

function limpar(){
	$('#formVazio').submit();
};

function inicializarFiltro(){
	
};

function remover(codigo, sigla) {
	var remocaoConfirmada = confirm("Voce tem certeza de que deseja desabilitar o vendedor \""+sigla+"\"?");
	if (remocaoConfirmada) {
		$("#formVendedorRemocao").attr("action","<c:url value="/vendedor/remocao"/>?id="+codigo);
		$("#formVendedorRemocao").attr("method","post");
		$('#formVendedorRemocao').submit();
	}	
}
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	
	
	<form id="formVazio" action="<c:url value="/comissao"/>" method="get">
		<input id="idVendedor" type="hidden" name="idVendedor" value="${vendedor.id}"/>
	</form>

	<fieldset id="bloco_comissao_vendedor">
		<legend>::: Comissão do Vendedor :::</legend>
			<input type="hidden" id="idVendedor" name="vendedor.id" value="${vendedor.id}"/>
			<div class="label">Nome:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="nome" name="vendedor.nome" value="${vendedor.nome}"/>
				<div class="suggestionsBox" id="containerPesquisaVendedor" style="display: none; width: 50%"></div>
			</div>
			<div class="label">Sobrenome:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="sobrenome" name="vendedor.sobrenome" value="${vendedor.sobrenome}" 
					style="width: 50%"
					class="uppercaseBloqueado pesquisavel desabilitado" disabled='disabled'/>
			</div>
			<div class="label">Revenda (%):</div>
			<div class="input" style="width: 70%">
				<input type="text" id="comissaoRevenda" name="comissaoRevenda" value="${comissao.aliquotaRevendaFormatada}" style="width: 5%"/>
			</div>
			<div class="label">Representação (%):</div>
			<div class="input" style="width: 70%">
				<input type="text" id="comissaoRepresentacao" name="comissaoRepresentacao" value="${comissao.aliquotaRepresentacaoFormatada}" style="width: 5%"/>
			</div>
	</fieldset>
	<div class="bloco_botoes">
		<input type="button" id="botaoInserirVendedor" title="Inserir Comissão do Vendedor" class="botaoInserir" value=""/>
		<input type="button" id="botaoPesquisarVendedor" title="Pesquisar Comissão do Vendedor" class="botaoPesquisar" value=""/>
		<input type="button" id="botaoLimpar" title="Limpar Dados da Comissão" class="botaoLimpar" value="" onclick="limpar()"/>
	</div>
	
	<fieldset id="bloco_comissao_produto">
		<legend>::: Comissão do Produto :::</legend>
			<div class="label condicional" >Forma Material:</div>
			<div class="input" style="width: 80%">
				<select name="formaMaterial" style="width: 20%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="forma" items="${listaFormaMaterial}">
						<option value="${forma}"
							<c:if test="${forma eq formaSelecionada}">selected</c:if>>${forma.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label condicional" >Material:</div>
			<div class="input" style="width: 80%">
				<input type="hidden" id="idMaterial" name="material.id" value="${material.id}"/>
				<input type="text" id="material" name="material.descricao" value="${material.descricao}" style="width: 40%" />
				<div class="suggestionsBox" id="containerPesquisaMaterial" style="display: none; width: 50%"></div>
			</div>
			<div class="label">Comissão (%):</div>
			<div class="input" style="width: 70%">
				<input type="text" id="comissaoProduto" name="comissaoRevenda" value="${comissao.aliquotaRevendaFormatada}" style="width: 5%"/>
			</div>
	</fieldset>
	<div class="bloco_botoes">
		<input type="button" id="botaoInserirProduto" title="Inserir Comissão do Produto" class="botaoInserir" value=""/>
		<input type="button" id="botaoPesquisarProduto" title="Pesquisar Comissão do Produto" class="botaoPesquisar"/> 
		<input type="button" id="botaoLimpar" title="Limpar Dados da Comissão" class="botaoLimpar" value="" onclick="limpar()"/>
	</div>

	<a id="rodape"></a>
	<fieldset>
		<legend>::: Resultado da Pesquisa de Comissões do ${isProduto ? 'Produto' : 'Vendedor'} :::</legend>
		<div id="paginador"></div>
		<div>
			<table class="listrada">
				<thead>
					<tr>
						<th style="width: 5%">Vigente</th>
						<th style="width: 30%">${isProduto ? 'Produto' : 'Vendedor'}</th>
						<th style="width: 10%">Revenda(%)</th>
						<th style="width: 10%">Represt.(%)</th>
						<th style="width: 10%">Inicio</th>
						<th style="width: 10%">Fim</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="comissao" items="${listaComissao}">
						<tr>
							<c:choose>
								<c:when test="${comissao.vigente}">
									<td><div class="flagOK"></div></td>
								</c:when>
								<c:otherwise>
									<td><div class="flagNaoOK"></div></td>
								</c:otherwise>
							</c:choose>
							<td>${isProduto ? comissao.descricaoProduto : comissao.nomeVendedor}</td>
							<td>${comissao.aliquotaRevendaFormatada}</td>
							<td>${comissao.aliquotaRepresentacaoFormatada}</td>
							<td>${comissao.dataInicioFormatado}</td>
							<td>${comissao.dataFimFormatado}</td>
						</tr>

					</c:forEach>
				</tbody>

			</table>
		</div>
	</fieldset>
</body>
</html>