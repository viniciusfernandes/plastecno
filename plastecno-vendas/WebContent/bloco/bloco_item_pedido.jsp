<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">

$(document).ready(function(){
	
	inserirMascaraNCM('bloco_item_pedido #ncm');
	
	$('#precoVenda').focus(function (){
		
		if(isEmpty($('#bloco_item_pedido #idMaterial').val())|| isEmpty($('#bloco_item_pedido #formaMaterial').val())){
			return;			
		}
		
		var parametro = 'itemEstoque.material.id='+$('#bloco_item_pedido #idMaterial').val();
		parametro += '&itemEstoque.formaMaterial='+$('#bloco_item_pedido #formaMaterial').val();
		parametro += '&itemEstoque.medidaExterna='+$('#bloco_item_pedido #medidaExterna').val();
		parametro += '&itemEstoque.medidaInterna='+$('#bloco_item_pedido #medidaInterna').val();
		parametro += '&itemEstoque.comprimento='+$('#bloco_item_pedido #comprimento').val();
		
		var request = $.ajax({
			type: 'get',
			url: '<c:url value="/estoque/item/precominimo"/>',
			data: parametro 
		});
		
		request.done(function (response){
			$('#bloco_item_pedido #precoMinimo').val(response.precoMinimo);
		});
		
		request.fail(function(request, status, excecao) {
			var mensagem = 'Falha no calculo do preco de venda sugerido: '+ idCampoPesquisavel;
			mensagem += ' para a URL ' + url;
			mensagem += ' contendo o valor de requisicao ' + parametro;
			mensagem += ' => Excecao: ' + excecao;
			gerarListaMensagemErro(new Array(mensagem));
		});
	});
});

</script>
<fieldset id="bloco_item_pedido">
	<legend>::: Itens do ${orcamento ? 'Orçamento': 'Pedido'} de ${not empty tipoPedido ? 'Compra': 'Venda'} :::</legend>

	<!-- Esse campo sera usado para popular a tabela de itens com os dados que vieram do ItemPedidoJson -->
	<input type="hidden" id="descricaoItemPedido" /> 
	<input type="hidden" id="precoItem" /> 
	<input type="hidden" id="idMaterial" name="itemPedido.material.id" /> 
	<input type="hidden" id="idItemPedido" name="itemPedido.id" /> 
	<input type="hidden" id="sequencial" name="itemPedido.sequencial" /> 
	<input type="hidden" id="precoUnidade" />

	<div class="label">Tipo de ${not empty tipoPedido ? 'Compra': 'Venda'}:</div>
	<div class="input">
		<input type="radio" id="tipoVendaKilo" name="itemPedido.tipoVenda"
			value="KILO" <c:if test="${empty pedido.id}">checked</c:if> />
	</div>
	<div class="label label_radio_button" style="width: 2%">Kilo</div>
	<div class="input">
		<input type="radio" id="tipoVendaPeca" name="itemPedido.tipoVenda"
			value="PECA" />
	</div>
	<div class="label label_radio_button" style="width: 60%">Peça</div>
	<div class="label">Qtde:</div>
	<div class="input" style="width: 7%">
		<input type="text" id="quantidade" name="itemPedido.quantidade" />
	</div>
	<div class="label" style="width: 6%">Forma:</div>
	<div class="input" style="width: 60%">
		<select id="formaMaterial" name="itemPedido.formaMaterial"
			style="width: 40%">
			<option value="">&lt&lt SELECIONE &gt&gt</option>
			<c:forEach var="formaMaterial" items="${listaFormaMaterial}">
				<option value="${formaMaterial}">${formaMaterial.descricao}</option>
			</c:forEach>
		</select>
	</div>
	<div class="label">Descrição:</div>
	<div class="input" style="width: 80%">
		<input type="text" id="descricao" name="itemPedido.descricaoPeca"
			style="width: 50%" />
		<div class="suggestionsBox" id="containerPesquisaDescricaoPeca"
			style="display: none; width: 50%"></div>
	</div>
	<div class="label">Material:</div>
	<div class="input" style="width: 80%">
		<input type="text" id="material" name="material.id" style="width: 50%" />
		<div class="suggestionsBox" id="containerPesquisaMaterial"
			style="display: none; width: 50%"></div>
	</div>
	<div class="label">Med. Ext / Espessura:</div>
	<div class="input" style="width: 10%">
		<input type="text" id="medidaExterna" name="itemPedido.medidaExterna"
			maxlength="11" />
	</div>

	<div class="label">Med. Int / Largura:</div>
	<div class="input" style="width: 10%">
		<input type="text" id="medidaInterna" name="itemPedido.medidaInterna"
			maxlength="11" style="width: 90%" />
	</div>

	<div class="label" style="width: 10%">Comprimento:</div>
	<div class="input" style="width: 30%">
		<input type="text" id="comprimento" name="itemPedido.comprimento"
			maxlength="11" style="width: 30%" />
	</div>
	<div class="label">Preço Mín.:</div>
	<div class="input" style="width: 70%">
		<input type="text" id="precoMinimo" name="itemPedido.precoMinimo" maxlength="8" style="width: 7%" disabled="disabled"/>
	</div>
	<div class="label">Preço:</div>
	<div class="input" style="width: 5%">
		<input type="text" id="precoVenda" name="itemPedido.precoVenda"
			maxlength="8" />
	</div>
	<div class="label" style="width: 8%">IPI (%) :</div>
	<div class="input" style="width: 5%">
		<!--  Esse campo nao sera inserido no banco de dados e sera submetido 
					para o servidor apenas para calcular o valor do item com o IPI por isso nao temos item.aliquotaIPI definidos. -->
		<input type="text" id="aliquotaIPI" name="aliquotaIPI" maxlength="2" />
	</div>
	<div class="label" style="width: 10%">ICMS (%) :</div>
	<div class="input" style="width: 40%">
		<input type="text" id="aliquotaICMS" name="itemPedido.aliquotaICMS"
			maxlength="2" style="width: 5%"/>
	</div>
	<div class="label">Prazo:</div>
	<div class="input" style="width: 5%">
		<input type="text" id="prazoEntregaItem" name="itemPedido.prazoEntrega" maxlength="3" />
	</div>
	<div class="label" style="width: 8%">Comiss. (%):</div>
	<div class="input" style="width: 5%">
		<input type="text" id="aliquotaComissao" name="itemPedido.aliquotaComissao" maxlength="3" />
	</div>
	<div class="label" style="width: 10%">NCM:</div>
		<div class="input" style="width: 8%">
			<input type="text" id="ncm" name="itemPedido.ncm"/>
		</div>
	<div class="label" style="width: 8%">CFOP:</div>
	<div class="input" style="width: 7%">
		<select id="cfop" name="itemPedido.tipoCFOP" >
			<option value="">&lt&lt SELECIONE &gt&gt</option>
			<c:forEach var="CFOP" items="${listaCFOP}">
				<option value="${CFOP}">${CFOP.descricao}</option>
			</c:forEach>
		</select>
	</div>
	<div class="bloco_botoes">
		<c:if test="${not pedidoDesabilitado and acessoCadastroPedidoPermitido}">
			<a id="botaoInserirItemPedido" title="Adicionar Dados do Item do Pedido" class="botaoAdicionar"></a>
			<a id="botaoLimparItemPedido" title="Limpar Dados do Item do Pedido" class="botaoLimpar"></a>
		</c:if>
	</div>

	<div style="width: 100%; margin-top: 15px;">
		<table id="tabelaItemPedido" class="listrada">
			<thead>
				<tr>
					<th style="display: none;">Item</th>
					<th style="width: 2%">Item</th>
					<th style="width: 5%">Qtde.</th>
					<th style="width: 45%">Descrição</th>
					<th style="width: 7%">${not empty tipoPedido ? 'Compra': 'Venda'}</th>
					<th style="width: 10%">Preço (R$)</th>
					<th style="width: 10%">Unid. (R$)</th>
					<th style="width: 10%">Total Item (R$)</th>
					<th style="width: 10%">IPI (%)</th>
					<th style="width: 10%">ICMS (%)</th>
					<th style="width: 5%">Prazo</th>
					<th>Ações</th>
				</tr>
			</thead>

			<tbody>
				<c:forEach items="${listaItemPedido}" var="itemPedido" varStatus="status">
					<tr id="${status.count - 1}">
						<td style="display: none;">${itemPedido.id}</td>
						<td>${itemPedido.sequencial}</td>
						<td class="valorNumerico">${itemPedido.quantidade}</td>
						<td>${itemPedido.descricao}</td>
						<td style="text-align: center;">${itemPedido.tipoVenda}</td>
						<td class="valorNumerico">${itemPedido.precoVendaFormatado}</td>
						<td class="valorNumerico">${itemPedido.precoUnidadeFormatado}</td>
						<td class="valorNumerico">${itemPedido.precoItemFormatado}</td>
						<td class="valorNumerico">${itemPedido.aliquotaIPIFormatado}</td>
						<td class="valorNumerico">${itemPedido.aliquotaICMSFormatado}</td>
						<td class="valorNumerico">${itemPedido.prazoEntrega}</td>
						<td>
							<c:if test="${not pedidoDesabilitado}">
								<input type="button" id="botaoEditarPedido" title="Editar Dados do Item do Pedido" value=""
									class="botaoEditar" onclick="editarItemPedido(this);" />
								<input type="button" id="botaoEditarPedido" title="Remover Dados do Item do Pedido" value=""
									class="botaoRemover" onclick="removerItemPedido(this);" />
							</c:if>
						</td>
					</tr>
				</c:forEach>


			</tbody>
			<tfoot>
				<tr>
					<td></td>
					<td colspan="4"></td>
					<td colspan="2" style="text-align: right;">TOTAL SEM IPI:</td>
					<td colspan="4"><div id="valorPedido"
							style="text-align: left;">R$ ${not empty pedido.valorPedido ? pedido.valorPedidoFormatado : 0}</div></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="4"></td>
					<td colspan="2" style="text-align: right;">TOTAL COM IPI:</td>
					<td colspan="4"><div id="valorPedidoIPI"
							style="text-align: left;">R$ ${not empty pedido.valorPedidoIPI ? pedido.valorPedidoIPIFormatado : 0}</div></td>
				</tr>
			</tfoot>
		</table>
	</div>

</fieldset>