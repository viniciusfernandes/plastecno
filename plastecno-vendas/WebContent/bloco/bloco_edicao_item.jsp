<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">

$(document).ready(function() {

	inserirMascaraMonetaria('bloco_item_pedido #preco', 7);
	inserirMascaraNumerica('bloco_item_pedido #aliquotaIPI', '99');
	inserirMascaraNumerica('bloco_item_pedido #aliquotaICMS', '9999999');
	inserirMascaraMonetaria('bloco_item_pedido #comprimento', 8);
	inserirMascaraMonetaria('bloco_item_pedido #medidaExterna', 8);
	inserirMascaraMonetaria('bloco_item_pedido #medidaInterna', 8);
	
	$('#botaoLimparItemPedido').click(function () {
		$('#bloco_item_pedido input').val('');
		$('#formaMaterial').val('');
		habilitarCamposEdicaoItem(true);
	});
	
	$('#bloco_item_pedido #botaoInserirLimiteMinimo').click(function () {
		var parametros = serializarForm('bloco_item_pedido');
		// tivemos que fazer esse append pois esse campos esta disabled, entao nao serao serializados
		parametros+='&itemPedido.medidaExterna='+$('#bloco_item_pedido #medidaExterna').val();
		parametros+='&itemPedido.medidaInterna='+$('#bloco_item_pedido #medidaInterna').val();
		parametros+='&itemPedido.comprimento='+$('#bloco_item_pedido #comprimento').val();
		
		alert(parametros);
		$('#formPesquisa').attr('action', '<c:url value="estoque/limiteminimo/inclusao"/>?'+parametros).attr('method', 'post').submit();
	});
});


function habilitarCamposEdicaoItem(habilitado){
	habilitar('#bloco_item_pedido #formaMaterial', habilitado);
	habilitar('#bloco_item_pedido #descricao', habilitado);
	habilitar('#bloco_item_pedido #material', habilitado);
	habilitar('#bloco_item_pedido #medidaExterna', habilitado);
	habilitar('#bloco_item_pedido #medidaInterna', habilitado);
	habilitar('#bloco_item_pedido #comprimento', habilitado);
};
</script>
<fieldset id="bloco_item_pedido">
	<legend>::: Edição do Item de ${not isEstoque ? 'Compra': 'Estoque'} :::</legend>
	<input type="hidden" id="idMaterial" name="itemPedido.material.id" value="${itemPedido.material.id}"/>
	
	<c:if test="${not empty itemPedido}">
		<!-- INCLUI ESSE CAMPO OCULTO COM A FORMA DE MATERIAL POIS A EDICAO DESABILITA O CAMPO E NO SUBMIT TEMOS QUE MANTER A INFORMACAO -->
		<input type="hidden" id="idFormaMaterial" name="itemPedido.formaMaterial" value="${itemPedido.formaMaterial}"/>
	</c:if>
	<input type="hidden" id="idItemPedido" name="itemPedido.id" value="${itemPedido.id}"/>
	
	<c:if test="${not isEstoque}">
		<input type="hidden" id="idTipoVenda" name="itemPedido.tipoVenda" value="${itemPedido.tipoVenda}"/>
		<input type="hidden" id="idDescricaoPeca" name="itemPedido.descricaoPeca" value="${itemPedido.descricaoPeca}"/>
		<input type="hidden" id="itemSequencial" name="itemPedido.sequencial" value="${itemPedido.sequencial}"/>
		
		<div class="label">Tipo de ${not empty tipoPedido ? 'Compra': 'Venda'}:</div>
		<div class="input">
			<input type="radio" id="tipoVendaKilo" name="itemPedido.tipoVenda"
				value="KILO" <c:if test="${empty itemPedido or itemPedido.vendaKilo}">checked</c:if> />
		</div>
		<div class="label label_radio_button" style="width: 2%">Kilo</div>
		<div class="input">
			<input type="radio" id="tipoVendaPeca" name="itemPedido.tipoVenda"
				value="PECA" <c:if test="${not empty itemPedido and not itemPedido.vendaKilo}">checked</c:if>/>
		</div>
		<div class="label label_radio_button" style="width: 60%">Peça</div>
	</c:if>
	
	<div class="label">Qtde:</div>
	<div class="input" style="width: 7%">
		<c:choose>
			<c:when test="${isEstoque}">
				<input type="text" id="quantidade" name="itemPedido.quantidade" value="${itemPedido.quantidade}"/>
			</c:when>
			<c:otherwise>
				<input type="text" id="quantidade" name="itemPedido.quantidadeRecepcionada" value="${itemPedido.quantidadeRecepcionada}"/>
			</c:otherwise>
		</c:choose>
	</div>
	<div class="label" style="width: 6%">Forma:</div>
	<div class="input" style="width: 60%">
		<select id="formaMaterial" name="itemPedido.formaMaterial" style="width: 35%">
			<option value="">&lt&lt SELECIONE &gt&gt</option>
			<c:forEach var="formaMaterial" items="${listaFormaMaterial}">
				<option value="${formaMaterial}" <c:if test="${formaMaterial eq itemPedido.formaMaterial}">selected</c:if>>${formaMaterial.descricao}</option>
			</c:forEach>
		</select>
	</div>
	<div class="label">Descrição:</div>
	<div class="input" style="width: 70%">
		<input type="text" id="descricao" name="itemPedido.descricaoPeca" value="${itemPedido.descricaoPeca}" style="width: 50%" />
	</div>
	<div class="label">Material:</div>
	<div class="input" style="width: 70%">
		<input type="text" id="material" name="itemPedido.material.descricao" style="width: 50%" value="${itemPedido.material.descricao}"/>
		<div class="suggestionsBox" id="containerPesquisaMaterial" style="display: none; width: 50%"></div>
	</div>
	<div class="label">Med. Ext / Espessura:</div>
	<div class="input" style="width: 10%">
		<input type="text" id="medidaExterna" name="itemPedido.medidaExterna"
			value="${itemPedido.medidaExterna}" maxlength="11" />
	</div>

	<div class="label">Med. Int / Largura:</div>
	<div class="input" style="width: 10%">
		<input type="text" id="medidaInterna" name="itemPedido.medidaInterna"
			value="${itemPedido.medidaInterna}" maxlength="11" style="width: 90%" />
	</div>

	<div class="label" style="width: 10%">Comprimento:</div>
	<div class="input" style="width: 30%">
		<input type="text" id="comprimento" name="itemPedido.comprimento"
			value="${itemPedido.comprimento}" maxlength="11" style="width: 30%" />
	</div>
	<div class="label">Preço:</div>
	<div class="input" style="width: 5%">
		<c:choose>
			<c:when test="${isEstoque}">
				<input type="text" id="preco" name="itemPedido.precoMedio" value="${itemPedido.precoMedio}" maxlength="8" />
			</c:when>
			<c:otherwise>
				<input type="text" id="preco" name="itemPedido.precoVenda" value="${itemPedido.precoVenda}" maxlength="8" />
			</c:otherwise>
		</c:choose>
	</div>
	
	<div class="label" style="width: 8%">IPI (%) :</div>
	<div class="input" style="width: 5%">
		<input type="text" id="aliquotaIPI" name="itemPedido.aliquotaIPI" value="${itemPedido.aliquotaIPIFormatado}" maxlength="2" />
	</div>
	<div class="label" style="width: 10%">ICMS (%) :</div>
	<div class="input" style="width: 40%">
		<input type="text" id="aliquotaICMS" name="itemPedido.aliquotaICMS"
			value="${itemPedido.aliquotaICMSFormatado}" maxlength="2" style="width: 5%"/>
	</div>
	
	<c:if test="${isEstoque and acessoManutencaoEstoquePermitido}">
		<div class="label">Qtde. Mín.:</div>
		<div class="input" style="width: 7%">
			<input type="text" id="quantidadeMinima" name="itemPedido.quantidadeMinima" value="${itemPedido.quantidadeMinima}"/>
		</div>
		<div class="label">Margem Mín.(%):</div>
		<div class="input" style="width: 7%">
			<input type="text" id="margemMinimaLucro" name="itemPedido.margemMinimaLucro" value="${itemPedido.margemMinimaLucro}"/>
		</div>
		<div class="input" style="width: 7%">
			<div id="botaoInserirLimiteMinimo" title="Inserir Limite Mínimo de Estoque" class="botaoInserir"></div>
		</div>
	</c:if>
	
	<div class="bloco_botoes">
		<!-- a id="botaoInserirLimiteMinimo" title="Inserir Limite Mínimo de Estoque" class="botaoInserir"></a -->
		<a id="botaoInserirItemPedido" title="${not empty itemPedido.id ? 'Refazer os Dados do Item' : 'Adicionar Dados do Item'}" class="botaoAdicionar"></a>
		<a id="botaoLimparItemPedido" title="Limpar Dados do Item" class="botaoLimpar"></a>
	</div>

</fieldset>