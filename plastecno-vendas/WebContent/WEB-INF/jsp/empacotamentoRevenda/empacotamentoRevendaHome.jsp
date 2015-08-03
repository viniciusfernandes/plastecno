<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Recep��o de Compras Pendentes</title>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>

<script type="text/javascript">

var listaIdPedido = new Array();

$(document).ready(function() {
	
	scrollTo('${ancora}');
	
	$('#botaoLimpar').click(function () {
		$('#formVazio').submit();	
	});
	
	$('#botaoInserirItemPedido').click(function () {
		var parametros = $('#bloco_item_pedido').serialize();
		parametros += '&' + $('#formPesquisa').serialize();
		var form = $('#formVazio');
		$(form).attr('method', 'post');
		$(form).attr('action', '<c:url value="/compra/item/inclusao"/>?'+parametros);
		$(form).submit();
	});
	
	$('#botaoEmpacotamentarPedidos').click(function () {

		inicializarModalConfirmacao({
			mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja EMPACOTAR ESSE(S) PEDIDO(S)?',
			confirmar: function(){
				var parametros = $('#formPesquisa').serialize();
				for (var i = 0; i < listaIdPedido.length; i++) {
					// Estamos validando aqui pois no DELETE dos itens da lista o javascript mantem undefined.
					if(listaIdPedido[i] != undefined){
						parametros+='&listaIdPedido='+listaIdPedido[i];
					}
				};
				var action = '<c:url value="/empacotamento/itens/inclusao"/>'+'?'+parametros;
				$('#formVazio').attr('method', 'post').attr('action', action);
				$('#formVazio').submit();
			}
		});
	});
	
	inicializarAutocompleteCliente();
});

function inicializarAutocompleteCliente(){
	autocompletar({
		url : '<c:url value="/cliente/listagem/nome"/>',
		campoPesquisavel : 'nomeFantasia',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaCliente',
		selecionarItem: function(itemLista) {
			$('#formPesquisa #idCliente').val(itemLista.id);
		}
	});	
}

function empacotarItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja EMPACOTAR esse item do pedido de revenda?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};

function enviarEncomenda(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa a��o n�o poder� ser� desfeita. Voc� tem certeza de que deseja REENVIAR esse item do pedido de ENCOMENDA?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};

function empacotarPedido(campo){
	if(campo.checked){
		listaIdPedido.push(campo.value);	
	} else {
		var index = listaIdPedido.indexOf(campo.value);
		delete listaIdPedido[index];
	}
};
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" action="<c:url value="/empacotamento"/>">
	</form>


	<form id="formPesquisa" action="<c:url value="/empacotamento/revenda/listagem"/>" method="get">
		<input type="hidden" id="idCliente" name="cliente.id" value="${cliente.id}"/>
		<fieldset>
			<legend>::: Pedidos de Revenda para Empacotamento :::</legend>
			<div class="label" style="width: 30%">Cliente:</div>
			<div class="input" style="width: 50%">
				<input type="text" id="nomeFantasia" name="cliente.nomeFantasia" value="${cliente.nomeFantasia}" class="pesquisavel" style="width: 40%"/>
				<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
			</div>
			<div class="bloco_botoes">
				<input type="submit" value="" class="botaoPesquisar" /> 
				<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Pesquisa dos Pedidos para Empacotamento" class="botaoLimpar" />
			</div>
		</fieldset>
	</form>
	
	<c:if test="${not empty relatorio}">
		<div class="bloco_botoes">
			<input type="button" id="botaoEmpacotamentarPedidos" title="Empacotar Pedidos" value="" class="botaoInserir"/>
		</div>
		<table class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">Num. Pedido</th>
					<th style="width: 10%">Dt. Entrega</th>
					<th style="width: 2%">Empc.</th>
					<th style="width: 1%">Item</th>
					<th style="width: 5%">Qtde</th>
					<th style="width: 43%">Desc. Item</th>
					<th style="width: 10%">Vendedor</th>
					<th style="width: 9%">A��o</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.listaGrupo}" var="pedido" varStatus="iGrupo">
					<c:forEach items="${pedido.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}">${pedido.id}</td>
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}">${pedido.propriedades['dataEntrega']}</td>
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${pedido.totalElemento}"><input type="checkbox" name="idPedido" value="${pedido.id}" onclick="empacotarPedido(this)"/></td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.sequencial}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeProprietario}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/empacotamento/pedido/pdf"/>" >
										<input type="hidden" name="idPedido" value="${pedido.id}" /> 
										<input type="submit" value="" title="Visualizar Pedido PDF" class="botaoPdf_16 botaoPdf_16_centro"/>
									</form>
									<form action="<c:url value="/empacotamento/item/reencomenda"/>"
											method="post">
											<input type="hidden" name="idItemPedido" value="${item.id}"> 
											<input type="button" title="Reenviar Item do Pedido para Encomenda" value="" class="botaoRemover"
												onclick="enviarEncomenda(this);"/>
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