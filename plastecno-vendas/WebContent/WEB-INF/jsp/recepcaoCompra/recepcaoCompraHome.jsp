<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Recepção de Compras</title>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript">

$(document).ready(function() {
	
	scrollTo('${ancora}');
	
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
	
	habilitar('#bloco_item_pedido input:radio', false);
	habilitar('#bloco_item_pedido #formaMaterial', false);
	habilitar('#bloco_item_pedido #descricao', false);
	habilitar('#bloco_item_pedido #material', false);
	habilitar('#bloco_item_pedido #medidaExterna', false);
	habilitar('#bloco_item_pedido #medidaInterna', false);
	habilitar('#bloco_item_pedido #comprimento', false);
	habilitar('#bloco_item_pedido #preco', false);
	habilitar('#bloco_item_pedido #aliquotaIPI', false);
	habilitar('#bloco_item_pedido #aliquotaICMS', false);
	
	$('#botaoLimpar').click(function () {
		$('#formVazio').submit();
	});
	
	$('#botaoInserirItemPedido').click(function () {
		if(isEmpty($('#bloco_item_pedido #idItemPedido').val())){
			return;
		}
		var parametros = 'idItemPedido='+$('#bloco_item_pedido #idItemPedido').val()+'&quantidadeRecepcionada='+$('#bloco_item_pedido #quantidade').val();
		parametros += '&' + $('#formPesquisa').serialize();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/compra/item/recepcaoparcial"/>?'+parametros);
		$(form).submit();
	});
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

</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" >
	</form>


	<form id="formPesquisa" action="<c:url value="/compra/recepcao/listagem"/>" method="get">
		<fieldset>
			<legend>::: Pedidos de Compra para Recepção :::</legend>
			<div class="label" style="width: 30%">Data Inícial:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataInicial" name="dataInicial"
					value="${dataInicial}" maxlength="10" class="pesquisavel" />
			</div>

			<div class="label" style="width: 10%">Data Final:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataFinal" name="dataFinal"
					value="${dataFinal}" maxlength="100" class="pesquisavel" />
			</div>
			<div class="label" style="width: 30%">Fornecedor:</div>
			<div class="input" style="width: 50%">
				<select name="idRepresentada" style="width: 30%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="representada" items="${listaRepresentada}">
						<option value="${representada.id}"
							<c:if test="${representada.id eq idRepresentadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
					</c:forEach>
				</select>
			</div>
			<div class="bloco_botoes">
				<input type="submit" value="" class="botaoPesquisar" /> 
				<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Compras" class="botaoLimpar" />
			</div>
		</fieldset>
	</form>
	
	<c:if test="${not empty itemPedido}">
		<jsp:include page="/bloco/bloco_edicao_item.jsp"/>
	</c:if>
	
	<c:if test="${not empty relatorio}">
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">Num. Pedido</th>
					<th style="width: 5%">Dt. Entrega</th>
					<th style="width: 1%">Item</th>
					<th style="width: 5%">Qtde</th>
					<th style="width: 5%">Qtde Recep.</th>
					<th style="width: 40%">Desc. Item</th>
					<th style="width: 10%">Comprador</th>
					<th style="width: 10%">Forneced.</th>
					<th style="width: 15%">Ação</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.listaGrupo}" var="pedido" varStatus="iGrupo">
					<c:forEach items="${pedido.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}">${pedido.id}</td>
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}">${pedido.propriedades['dataEntrega']}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.sequencial}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidadeRecepcionada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeProprietario}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeRepresentada}</td>
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
									<form action="<c:url value="/compra/item/edicao"/>" method="post">
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<input type="button" value="" title="Editar o Item do Pedido" class="botaoEditar" onclick="submeterForm(this);"/>
									</form>
									<form action="<c:url value="/compra/item/remocao"/>" method="post" >
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<input type="button" value="" title="Remover o Item do Pedido" 
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