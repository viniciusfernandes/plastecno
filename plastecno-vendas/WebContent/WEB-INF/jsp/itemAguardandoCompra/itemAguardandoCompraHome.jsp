<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Itens para Encomendar</title>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>

<script type="text/javascript">

var listaIdItem = new Array();

$(document).ready(function() {
	
	scrollTo('${ancora}');
	
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
	
	$('#botaoLimpar').click(function () {
		$('#formVazio').submit();
	});
	
	$('#botaoPesquisar').click(function () {
		adicionarInputHiddenFormulario('formVazio', 'dataInicial', document.getElementById('dataInicial').value);
		adicionarInputHiddenFormulario('formVazio', 'dataFinal', document.getElementById('dataFinal').value);
		adicionarInputHiddenFormulario('formVazio', 'cliente.nomeFantasia', document.getElementById('nomeFantasia').value);
		adicionarInputHiddenFormulario('formVazio', 'cliente.id', document.getElementById('idCliente').value);
		$('#formVazio').attr('action', '<c:url value="/itemAguardandoCompra/item/listagem"/>').attr('method', 'get').submit();
	});
	
	$('#botaoComprar').click(function () {

		inicializarModalConfirmacao({
			mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja COMPRAR OS ITEN(S) DO(S) PEDIDO(S)?',
			confirmar: function(){
				var parametros = $('#formPesquisa').serialize();
				parametros+='&idRepresentadaFornecedora='+$('#fornecedor').val();
				for (var i = 0; i < listaIdItem.length; i++) {
					// Estamos validando aqui pois no DELETE dos itens da lista o javascript mantem undefined.
					if(listaIdItem[i] != undefined){
						parametros+='&listaIdItem='+listaIdItem[i];
					}
				};
				var action = '<c:url value="/itemAguardandoCompra/item/compra"/>'+'?'+parametros;
				$('#formVazio').attr('method', 'post').attr('action', action);
				$('#formVazio').submit();
			}
		});
	});
	
	inicializarAutocompleteCliente();
});

function enviarEmpacotamento(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja ENVIAR esse PEDIDO INTEIRO para o EMPACOTAMENTO?',
		confirmar: function(){
			submeterForm(botao);
		}
	});
};

function inicializarAutocompleteCliente(){
	autocompletar({
		url : '<c:url value="/cliente/listagem/nome"/>',
		campoPesquisavel : 'nomeFantasia',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaCliente',
		selecionarItem: function(itemLista) {
			$('#bloco_pesquisa #idCliente').val(itemLista.id);
			$('#botaoPesquisar').click();
		}
	});
};

function selecionarItemPedido(campo){
	if(campo.checked){
		listaIdItem.push(campo.value);	
	} else {
		var index = listaIdItem.indexOf(campo.value);
		delete listaIdItem[index];
	}
};
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>

	<form id="formVazio" >
	</form>

		
		<fieldset id="bloco_pesquisa">
		
			<input type="hidden" id="idCliente" name="cliente.id" value="${cliente.id}"/>
			
			<legend>::: Pesquisa de Itens para Comprar :::</legend>
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
			<div class="label" style="width: 30%">Cliente:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="nomeFantasia" name="cliente.nomeFantasia" value="${cliente.nomeFantasia}" class="pesquisavel" />
				<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
			</div>
			<div class="bloco_botoes">
				<input id="botaoPesquisar" type="button" value="" class="botaoPesquisar" /> 
				<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Compras" class="botaoLimpar" />
			</div>
		</fieldset>
	
	<c:if test="${not empty relatorio}">
		<fieldset>
		<legend>::: Itens de Pedidos para Comprar :::</legend>
		<div class="label obrigatorio" style="width: 30%">Fornecedor:</div>
		<div class="input" style="width: 30%">
			<select id="fornecedor" style="width: 50%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="fornecedor" items="${listaFornecedor}">
					<option value="${fornecedor.id}"
						<c:if test="${fornecedor eq fornecedorSelecionado}">selected</c:if>>${fornecedor.nomeFantasia}</option>
				</c:forEach>
			</select>
		</div>
		<div class="bloco_botoes">
			<input type="button" id="botaoComprar" title="Enviar Itens Para Compras" value="" class="botaoEnviarEmail"/>
		</div>
		<table id="tabelaItemEncomenda" class="listrada">
			<thead>
				<tr>
					<th style="width: 10%">Num. Pedido</th>
					<th style="width: 10%">Dt. Entrega</th>
					<th style="width: 1%">Encom.</th>
					<th style="width: 1%">Item</th>
					<th style="width: 5%">Qtde.</th>
					<th style="width: 48%">Desc. Item</th>
					<th style="width: 10%">Comprador</th>
					<th style="width: 5%">Unid. (R$)</th>
					<th style="width: 5%">Total (R$)</th>
					<th style="width: 5%">Ação</th>
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
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}"><input type="checkbox" name="idItemPedido" value="${item.id}" onclick="selecionarItemPedido(this)"/></td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.sequencial}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidadeEncomendada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeProprietario}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoUnidadeFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.precoItemFormatado}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/pedido/pdf"/>" >
										<input type="hidden" name="idPedido" value="${pedido.id}" /> 
										<input type="submit" value="" title="Visualizar Pedido PDF" class="botaoPdf_16 botaoPdf_16_centro"/>
									</form>
									<form action="<c:url value="/itemAguardandoCompra/empacotamento"/>" method="post" >
										<input type="hidden" name="idPedido" value="${pedido.id}" /> 
										<input type="button" value="" title="Enviar Pedido para o Empacotamento" 
										onclick="enviarEmpacotamento(this);" class="botaoAdicionar_16" />
									</form>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>
		</fieldset>
	</c:if>

</body>
</html>