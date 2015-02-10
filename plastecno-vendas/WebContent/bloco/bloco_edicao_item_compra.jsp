<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<fieldset id="bloco_item_pedido">
	<legend>::: Item de ${not empty tipoPedido ? 'Compra': 'Estoque'} :::</legend>
	<input type="hidden" id="idItemPedido" name="itemPedido.id" value="${itemPedido.id}"/>
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
	<div class="label label_radio_button" style="width: 60%">Pe�a</div>
	<div class="label">Qtde:</div>
	<div class="input" style="width: 7%">
		<input type="text" id="quantidade" name="itemPedido.quantidade" value="${itemPedido.quantidade}"/>
	</div>
	<div class="label" style="width: 6%">Forma:</div>
	<div class="input" style="width: 60%">
		<select id="formaMaterial" name="itemPedido.formaMaterial"
			style="width: 40%">
			<option value="">&lt&lt SELECIONE &gt&gt</option>
			<c:forEach var="formaMaterial" items="${listaFormaMaterial}">
				<option value="${formaMaterial}" <c:if test="${formaMaterial eq itemPedido.formaMaterial}">selected</c:if>>${formaMaterial.descricao}</option>
			</c:forEach>
		</select>
	</div>
	<div class="label">Descri��o:</div>
	<div class="input" style="width: 80%">
		<input type="text" id="descricao" name="itemPedido.descricaoPeca" value="${itemPedido.descricaoPeca}" style="width: 50%" />
	</div>
	<div class="label">Material:</div>
	<div class="input" style="width: 80%">
		<input type="text" id="material" name="material.id" style="width: 50%" value="${itemPedido.material.descricao}"/>
		<div class="suggestionsBox" id="containerPesquisaMaterial"
			style="display: none; width: 50%"></div>
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
	<div class="label">Pre�o:</div>
	<div class="input" style="width: 5%">
		<input type="text" id="precoVenda" name="itemPedido.precoVenda"
			value="${itemPedido.precoVenda}" maxlength="8" />
	</div>
	<div class="label" style="width: 8%">IPI (%) :</div>
	<div class="input" style="width: 5%">
		<input type="text" id="aliquotaIPI" name="aliquotaIPI" value="${itemPedido.aliquotaIPI}" maxlength="2" />
	</div>
	<div class="label" style="width: 10%">ICMS (%) :</div>
	<div class="input" style="width: 5%">
		<input type="text" id="aliquotaICMS" name="itemPedido.aliquotaICMS"
			value="${itemPedido.aliquotaICMS}" maxlength="2" />
	</div>
	<div class="bloco_botoes">
		<a id="botaoInserirItemPedido" title="Adicionar Dados do Item do Pedido" class="botaoAdicionar"></a>
		<a id="botaoLimparItemPedido" title="Limpar Dados do Item do Pedido" class="botaoLimpar"></a>
	</div>

</fieldset>