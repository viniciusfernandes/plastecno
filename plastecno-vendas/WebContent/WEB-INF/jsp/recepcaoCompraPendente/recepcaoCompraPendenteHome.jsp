<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Recep��o de Compras Pendentes</title>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>

<script type="text/javascript">

$(document).ready(function() {
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
	
	$('#botaoLimpar').click(function () {
		$('#formVazio').submit();
	});
	
	var parametros = $('#bloco_item_pedido').serialize();
	$('#botaoInserirItemPedido').click(function () {
		var parametros = $('#bloco_item_pedido').serialize();
		$('#formVazio').attr('action', 'action="<c:url value="/compra/item/edicao"/>?"'+paramentros);
		$('#formVazio').submit();
	});
});


function removerItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja REMOVER esse item do pedido de compra?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};


function recepcionarItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja RECEPCIONAR esse item do pedido de compra?',
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

</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" >
	</form>


	<form id="formPesquisa" action="<c:url value="/compra/recepcao/listagem"/>" method="get">
		<fieldset>
			<legend>::: Pedidos de Compra para Recep��o :::</legend>
			<div class="label obrigatorio" style="width: 30%">Data In�cial:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataInicial" name="dataInicial"
					value="${dataInicial}" maxlength="10" class="pesquisavel" />
			</div>

			<div class="label obrigatorio" style="width: 10%">Data Final:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataFinal" name="dataFinal"
					value="${dataFinal}" maxlength="100" class="pesquisavel" />
			</div>
			<div class="label" style="width: 30%">Representada:</div>
			<div class="input" style="width: 40%">
				<select name="idRepresentada" style="width: 70%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="representada" items="${listaRepresentada}">
						<option value="${representada.id}"
							<c:if test="${representada.id eq idRepresentadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>
			<div class="bloco_botoes">
				<input type="submit" value="" class="botaoPesquisar" /> 
				<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Gera��o do Relat�rio de Compras" class="botaoLimpar" />
			</div>
		</fieldset>
	</form>
	
	<c:if test="${not empty relatorio}">
		<jsp:include page="/bloco/bloco_edicao_item_compra.jsp"/>
		
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">Num. Pedido</th>
					<th style="width: 5%">Qtde</th>
					<th style="width: 45%">Desc. Item</th>
					<th style="width: 10%">Comprador</th>
					<th style="width: 10%">Represent.</th>
					<th style="width: 5%">Unid. (R$)</th>
					<th style="width: 5%">Total (R$)</th>
					<th style="width: 10%">A��o</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.listaGrupo}" var="pedido" varStatus="iGrupo">
					<c:forEach items="${pedido.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}">${pedido.id}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeProprietario}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeRepresentada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoUnidadeFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoItemFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/compra/pdf"/>" >
										<input type="hidden" name="idPedido" value="${pedido.id}" /> 
										<input type="submit" value="" title="Visualizar Pedido PDF" class="botaoPdf_16 botaoPdf_16_centro"/>
									</form>
									<form action="<c:url value="/compra/item/recepcao"/>" method="post" >
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<input type="button" value="" title="Recepcionar o Item do Pedido" 
										onclick="recepcionarItem(this);" class="botaoAdicionar_16" />
									</form>
									<form action="<c:url value="/compra/item"/>" method="get">
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<input type="button" value="" title="Editar o Item do Pedido" class="botaoEditar" onclick="submeterForm(this);"/>
									</form>
									<form action="<c:url value="/compra/item/remocao"/>" method="post" >
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<input type="button" value="" title="Remover o Item da Compra" 
											onclick="removerItem(this);" class="botaoRemover" />
									</form>
									
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

</body>
</html>