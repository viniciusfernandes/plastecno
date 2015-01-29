<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Recepção de Compras Pendentes</title>

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
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" action="<c:url value="/compra/recepcao"/>">
	</form>


	<form id="formPesquisa" action="<c:url value="/compra/recepcao/listagem"/>" method="get">
		<fieldset>
			<legend>::: Pedidos de Compra para Recepção :::</legend>
			<div class="label obrigatorio" style="width: 30%">Data Inícial:</div>
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
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Compras" class="botaoLimpar" />
		</div>
	</form>
	
	<c:if test="${not empty relatorio}">
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">Num. Pedido</th>
					<th style="width: 5%">Qtde</th>
					<th style="width: 45%">Desc. Item</th>
					<th style="width: 10%">Comprador</th>
					<th style="width: 10%">Represent.</th>
					<th style="width: 5%">Valor (R$)</th>
					<th style="width: 5%">Ação</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.lista}" var="master" varStatus="countMaster">
					<c:forEach items="${master.details}" var="detail"
						varStatus="countDetail">
						<tr>
							<c:if test="${countDetail.count le 1}">
								<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}"
								rowspan="${master.size}">${master.label}</td>
							</c:if>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${detail[0]}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${detail[1]}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${detail[2]}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${detail[3]}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">${detail[4]}</td>
							<td class="fundo${iteracaoRepresentada.index % 2 == 0 ? 1 : 2}">
								<form action="<c:url value="/compra/item/recepcao"/>" method="post" style="width: 20%">
									<input type="hidden" name="idItemCompra" value="${detail[5]}" /> 
									<input type="button" value="" title="Recepcionar o Item do Pedido" 
									onclick="recepcionarItem(this);" class="botaoAdicionar" style="border: none;" />
								</form>
								<form action="<c:url value="/compra/pdf"/>" style="width: 20%">
									<input type="hidden" name="idPedido" value="${master.label}" /> 
									<input type="submit" value="" title="Visualizar Pedido PDF" class="botaoPDF" style="border: none;" />
								</form>
								<form action="<c:url value="/compra/edicao"/>" style="width: 20%">
									<input type="hidden" name="idPedido" value="${master.label}" /> 
									<input type="submit" value="" title="Editar o Item do Pedido" class="botaoEditar" style="border: none;" />
								</form>
								<form action="<c:url value="/compra/item/remocao"/>" method="post" style="width: 20%">
									<input type="hidden" name="idItemCompra" value="${detail[5]}" /> 
									<input type="button" value="" title="Remover o Item da Compra" 
										onclick="removerItem(this);" class="botaoRemover" style="border: none;" />
								</form>
							</td>

						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

</body>
</html>