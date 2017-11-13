<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">

$(document).ready(function(){
	
	habilitar('#bloco_item_pedido #descricao', false);
	habilitar('#bloco_item_pedido #peso', false);
	<%--
	habilitar('#bloco_item_pedido #aliquotaIPI', <c:out value="${not empty pedido and pedido.representada.IPIHabilitado}"/>);
	--%>
	
	$('#precoVenda').focus(function (){
		
		if(isEmpty($('#bloco_item_pedido #idMaterial').val())|| isEmpty($('#bloco_item_pedido #formaMaterial').val())){
			return;			
		}
		pesquisarPrecoMinimo();
		pesquisarNcm();
		pesquisarPesoItem();
	});
	
	$('#quantidade').keyup(function (){
		if(isEmpty($('#bloco_item_pedido #idMaterial').val())|| isEmpty($('#bloco_item_pedido #formaMaterial').val())){
			return;			
		}
		pesquisarPesoItem();
	});
	
	$('#ncm').focus(function (){
		if(isEmpty($('#bloco_item_pedido #idMaterial').val())|| isEmpty($('#bloco_item_pedido #formaMaterial').val())){
			return;			
		}
		pesquisarNcm();
	});
	
	$('#botaoPesquisaItemPedidoVendido').click(function (){
		// Aqui estamos enviando alguns dados do item que desejamos pesquisar e que ja foram vendidos.
		// Alem disso estamos reaproveitando todo o codigo que mantem o estado da tela de pedidos atraves do botao de pesquisa ja existente.
		$('<input>').attr('type','hidden').attr('name','itemVendido.formaMaterial').attr('value',$('#bloco_item_pedido #formaMaterial').val()).appendTo('#formPesquisa');
		$('<input>').attr('type','hidden').attr('name','itemVendido.descricaoPeca').attr('value',$('#bloco_item_pedido #descricao').val()).appendTo('#formPesquisa');
		$('<input>').attr('type','hidden').attr('name','itemVendido.material.id').attr('value',$('#bloco_item_pedido #idMaterial').val()).appendTo('#formPesquisa');
		$('<input>').attr('type','hidden').attr('name','itemVendido.medidaExterna').attr('value',$('#bloco_item_pedido #medidaExterna').val()).appendTo('#formPesquisa');
		$('<input>').attr('type','hidden').attr('name','itemVendido.medidaInterna').attr('value',$('#bloco_item_pedido #medidaInterna').val()).appendTo('#formPesquisa');
		$('<input>').attr('type','hidden').attr('name','itemVendido.comprimento').attr('value',$('#bloco_item_pedido #comprimento').val()).appendTo('#formPesquisa');
		$('#botaoPesquisaPedido').click();
	});
	
	inserirMascaraNCM('bloco_item_pedido #ncm');
	inserirMascaraMonetaria('precoVenda', 7);
	inserirMascaraNumerica('aliquotaIPI', '99');
	inserirMascaraMonetaria('aliquotaComissao', 5);
	inserirMascaraNumerica('aliquotaICMS', '99');
	inserirMascaraNumerica('quantidade', '9999999');
	inserirMascaraMonetaria('comprimento', 8);
	inserirMascaraMonetaria('peso', 8);
	inserirMascaraMonetaria('medidaExterna', 8);
	inserirMascaraMonetaria('medidaInterna', 8);
	inserirMascaraNumerica('prazoEntregaItem', '999');
});

function gerarParametrosMedidasItem(){
	var parametro = 'item.material.id='+$('#bloco_item_pedido #idMaterial').val();
	parametro += '&item.formaMaterial='+$('#bloco_item_pedido #formaMaterial').val();
	parametro += '&item.medidaExterna='+$('#bloco_item_pedido #medidaExterna').val();
	parametro += '&item.medidaInterna='+$('#bloco_item_pedido #medidaInterna').val();
	parametro += '&item.comprimento='+$('#bloco_item_pedido #comprimento').val();
	parametro += '&item.quantidade='+$('#bloco_item_pedido #quantidade').val();
	return parametro;
}

function pesquisarPrecoMinimo(){
	var parametro = gerarParametrosMedidasItem();
	var request = $.ajax({
		type: 'get',
		url: '<c:url value="/estoque/item/precominimo"/>',
		data: parametro 
	});
	
	request.done(function (response){
		var preco = response.precoMinimo;
		if(preco == undefined || preco == null){
			return;
		}
		$('#bloco_item_pedido #precoMinimo').val(preco);
	});
	
	request.fail(function(request, status, excecao) {
		var mensagem = 'Falha no calculo do preco de venda sugerido: '+ idCampoPesquisavel;
		mensagem += ' para a URL ' + url;
		mensagem += ' contendo o valor de requisicao ' + parametro;
		mensagem += ' => Excecao: ' + excecao;
		gerarListaMensagemErro(new Array(mensagem));
	});
};

function pesquisarPesoItem(){
	var parametro = gerarParametrosMedidasItem();
	var request = $.ajax({
		type: 'get',
		url: '<c:url value="/pedido/pesoitem"/>',
		data: parametro 
	});
	
	request.done(function (response){
		var peso = response.peso;
		if(peso != undefined || peso != null){
			$('#bloco_item_pedido #peso').val(peso);
			return;
		} 
		var erros = response.erros ; 
		if(erros!= undefined && erros != null){
			gerarListaMensagemErro(new Array(erros));
			return;
		}
	});
	
	request.fail(function(request, status, excecao) {
		var mensagem = 'Falha no calculo do preco de venda sugerido: '+ idCampoPesquisavel;
		mensagem += ' para a URL ' + url;
		mensagem += ' contendo o valor de requisicao ' + parametro;
		mensagem += ' => Excecao: ' + excecao;
		gerarListaMensagemErro(new Array(mensagem));
	});
};

function pesquisarNcm(){
	var parametro = gerarParametrosMedidasItem();
	var request = $.ajax({
		type: 'get',
		url: '<c:url value="/estoque/item/ncm"/>',
		data: parametro 
	});
	
	request.done(function (response){
		if(isEmpty(response.ncm)){
			return;
		}
		
		$('#bloco_item_pedido #ncm').val(response.ncm);
	});
	
	request.fail(function(request, status, excecao) {
		var mensagem = 'Falha na pesquisa do item de venda sugerido: '+ idCampoPesquisavel;
		mensagem += ' para a URL ' + url;
		mensagem += ' contendo o valor de requisicao ' + parametro;
		mensagem += ' => Excecao: ' + excecao;
		gerarListaMensagemErro(new Array(mensagem));
	});
};

</script>
<fieldset id="bloco_item_pedido">
	<legend>::: Itens do ${orcamento ? 'Orçamento': 'Pedido'} de ${isCompra ? 'Compra': 'Venda'} :::</legend>

	<!-- Esse campo sera usado para popular a tabela de itens com os dados que vieram do ItemPedidoJson -->
	<input type="hidden" id="descricaoItemPedido" /> 
	<input type="hidden" id="precoItem" /> 
	<input type="hidden" id="idMaterial" name="itemPedido.material.id" /> 
	<input type="hidden" id="idItemPedido" name="itemPedido.id" /> 
	<input type="hidden" id="sequencial" name="itemPedido.sequencial" /> 
	<input type="hidden" id="precoUnidade" />

	<div class="label">Tipo de ${isCompra ? 'Compra': 'Venda'}:</div>
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
	<div class="input" style="width: 58%">
		<select id="formaMaterial" name="itemPedido.formaMaterial"
			style="width: 45%">
			<option value=""></option>
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
	<div class="input" style="width: 10%">
		<input type="text" id="precoMinimo" name="itemPedido.precoMinimo" maxlength="8" style="width: 100%" disabled="disabled"/>
	</div>
	<div class="label">Peso (kg):</div>
	<div class="input" style="width: 50%">
		<input type="text" id="peso" name="itemPedido.peso" maxlength="8" style="width: 18%" />
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
			maxlength="2" style="width: 10%"/>
	</div>
	<div class="label">Prazo (dias):</div>
	<div class="input" style="width: 5%">
		<input type="text" id="prazoEntregaItem" name="itemPedido.prazoEntrega" maxlength="3" />
	</div>
	<div class="label" style="width: 8%">Comiss. (%):</div>
	<div class="input" style="width: 5%">
		<input type="text" id="aliquotaComissao" name="itemPedido.aliquotaComissao" maxlength="3" 
			<c:out value="${acessoAlteracaoComissaoPermitida ? '' :'disabled=\"disabled\"'}"/>"
			class="<c:out value="${acessoAlteracaoComissaoPermitida ? '' :'uppercaseBloqueado desabilitado'}"/>"
		/>
	</div>
	<div class="label" style="width: 10%">C.S.T:</div>
	<div class="input" style="width: 50%">
		<select id="cst" name="itemPedido.tipoCst" style="width: 30%">
			<option value=""></option>
			<c:forEach var="CST" items="${listaCST}">
				<option value="${CST}">${CST.codigo} - ${CST.descricao}</option>
			</c:forEach>
		</select>
	</div>
	<div class="label" >NCM:</div>
	<div class="input" style="width: 80%">
		<input type="text" id="ncm" name="itemPedido.ncm" value="${itemPedido.ncm}" style="width: 10%"/>
	</div>
	<div class="bloco_botoes">
		<c:if test="${not pedidoDesabilitado and acessoCadastroPedidoPermitido}">
			<a id="botaoInserirItemPedido" title="Adicionar Dados do Item do Pedido" class="botaoAdicionar"></a>
			<a type="button" id="botaoPesquisaItemPedidoVendido" title="Pesquisar Item de Pedido Vendidos" class="botaoPesquisar"></a>
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
					<th style="width: 7%">${isCompra ? 'Compra': 'Venda'}</th>
					<th style="width: 10%">Preço (R$)</th>
					<th style="width: 10%">Unid. (R$)</th>
					<th style="width: 10%">Total Item (R$)</th>
					<th style="width: 10%">IPI (%)</th>
					<th style="width: 10%">ICMS (%)</th>
					<th style="width: 5%">Prazo (dias)</th>
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
				<tr>
					<td></td>
					<td colspan="4"></td>
					<td colspan="2" style="text-align: right;">TOTAL SEM FRETE:</td>
					<td colspan="4"><div id="valorTotalSemFrete"
							style="text-align: left;">R$ ${not empty pedido.valorTotalSemFreteFormatado ? pedido.valorTotalSemFreteFormatado : 0}</div></td>
				</tr>
			</tfoot>
		</table>
	</div>

</fieldset>