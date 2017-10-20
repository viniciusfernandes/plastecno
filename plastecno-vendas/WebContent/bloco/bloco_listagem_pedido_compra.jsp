<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">

$(document).ready(function() {
	var nome = 'listaIdItemSelecionado[]';	
	var id = 'idItemSelec';
	var idForm = 'formPagamento';
	var form = document.getElementById(idForm);
	tabelaLinhaSelecionavel({
		idTabela: 'tabelaPedidoCompra',
		idBotaoLimpar:'botaoLimparPagamento',
		listaValorSelecionado: <c:out value="${not empty listaIdItemSelecionado ? listaIdItemSelecionado : \'new Array()\'}"/>,
		onSelect: function(json){
			gerarInputHiddenFormulario(idForm, id+json.valCheckbox, nome, json.valCheckbox);
			if(onSelectItemTabela != undefined) {
				onSelectItemTabela(json);
			}
		},
		onUnselect: function(json){
			form.removeChild(document.getElementById(id+json.valCheckbox));
			if(onSelectItemTabela != undefined) {
				onSelectItemTabela(json);
			}
		},
		onInit: function(listaValorSelecionado){
			for (var i = 0; i < listaValorSelecionado.length; i++) {
				gerarInputHiddenFormulario(idForm, id+listaValorSelecionado[i], nome, listaValorSelecionado[i]);
			}
			if(onSelectItemTabela != undefined) {
				onSelectItemTabela({checked:true, totChecked:listaValorSelecionado.length});
			}
		},
		onClean: function(){
			$('#'+idForm+' input[id^=\''+id+'\']').remove();
		}
	});
});
</script>
	<c:if test="${not empty relatorio}">
		<table id='tabelaPedidoCompra' class="listrada" >
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 7%">Num. Pedido</th>
					<th style="width: 5%">Dt. Entrega</th>
					<th style="width: 7%">Num. Venda</th>
					<th style="width: 1%">Item</th>
					<th style="width: 5%">Qtde</th>
					<th style="width: 5%">Qtde Recep.</th>
					<th style="width: 38%">Desc. Item</th>
					<th style="width: 10%">Comprador</th>
					<th style="width: 10%">Forneced.</th>
					<th style="width: 10%">Ação</th>
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
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.idPedidoVenda}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.sequencial}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidadeRecepcionada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeProprietario}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.nomeRepresentada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
								<c:if test="${alteracaoPedidoCompraHabilitada}">
									<form action="<c:url value="/pedido/pdf"/>" >
										<input type="hidden" name="idPedido" value="${pedido.id}" />
										<input type="hidden" name="tipoPedido" value="${relatorio.propriedades['tipoPedido']}" />
										<div class="input" style="width: 20%"> 
											<input type="submit" value="" title="Visualizar Pedido PDF" class="botaoPdf_16 botaoPdf_16_centro"/>
										</div>
									</form>
									<form action="<c:url value="/compra/item/recepcao"/>" method="post" >
										<input type="hidden" name="idItemPedido" value="${item.id}" /> 
										<div class="input" style="width: 20%">
											<input type="button" value="" title="Recepcionar o Item do Pedido" onclick="recepcionarItem(this);" class="botaoAdicionar_16" />
										</div>
									</form>
									<form action="<c:url value="/compra/item/edicao"/>" method="post">
										<input type="hidden" name="idItemPedido" value="${item.id}" />
										<div class="input" style="width: 20%">
											<input type="button" value="" title="Editar o Item do Pedido" class="botaoEditar" onclick="submeterForm(this);"/>
										</div>
									</form>
									<form action="<c:url value="/compra/item/remocao"/>" method="post" >
										<input type="hidden" name="idItemPedido" value="${item.id}" />
										<div class="input" style="width: 20%">
											<input type="button" value="" title="Remover o Item do Pedido" onclick="removerItem(this);" class="botaoRemover" />
										</div>
									</form>
								</c:if>
									<div class="input" style="width: 20%">
										<input type="checkbox" name="idItemPedido" value="${item.id}" ${not empty idSelec[item.id]?'checked':''}/>
									</div>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>
	</c:if>