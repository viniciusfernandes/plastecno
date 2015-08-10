<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">

$(document).ready(function() {

	inserirMascaraNumerica('bloco_limite_minimo #quantidade', '9999999');
	inserirMascaraMonetaria('bloco_limite_minimo #comprimento', 8);
	inserirMascaraMonetaria('bloco_limite_minimo #medidaExterna', 8);
	inserirMascaraMonetaria('bloco_limite_minimo #medidaInterna', 8);
	inserirMascaraNumerica('bloco_limite_minimo #taxaMinima', '99');
	
	$('#botaoLimparLimite').click(function () {
		$('#bloco_limite_minimo input').val('');
		$('#bloco_limite_minimo #formaMaterial').val('');
		habilitarCamposEdicaoItem(true);
	});
	
	$('#bloco_limite_minimo #botaoPesquisarLimite').click(function () {
		var parametros = serializarForm('bloco_limite_minimo');
		$('#formVazioLimite').attr('action', '<c:url value="estoque/limiteminimo"/>?'+parametros).attr('method', 'post').submit();
	});
	
	$('#bloco_limite_minimo #botaoInserirLimite').click(function () {
		var parametros = serializarForm('bloco_limite_minimo');
		$('#formVazioLimite').attr('action', '<c:url value="estoque/limiteminimo/inclusao"/>?'+parametros).attr('method', 'post').submit();
	});
	
});

</script>

<form id="formVazioLimite">
</form>

<form id="formPesquisaLimite" action="estoque/limiteminimo/inclusao" method="post">
<fieldset id="bloco_limite_minimo">
	<legend>::: Limite Mínimo Item do Estoque :::</legend>
	<input type="hidden" id="idMaterial" name="limite.material.id" value="${limite.material.id}"/>
	<input type="hidden" id="idLimite" name="limite.id" value="${limite.id}"/>
	<c:if test="${not empty itemPedido}">
		<!-- INCLUI ESSE CAMPO OCULTO COM A FORMA DE MATERIAL POIS A EDICAO DESABILITA O CAMPO E NO SUBMIT TEMOS QUE MANTER A INFORMACAO -->
		<input type="hidden" id="idFormaMaterial" name="limite.formaMaterial" value="${limite.formaMaterial}"/>
	</c:if>
	<input type="hidden" id="idLimiteMinimo" name="limite.id" value="${limite.id}"/>
	
	<div class="label">Qtde Mínima:</div>
	<div class="input" style="width: 7%">
		<input type="text" id="quantidade" name="limite.quantidadeMinima" value="${limite.quantidadeMinima}"/>
	</div>
	<div class="label" style="width: 6%">Forma:</div>
	<div class="input" style="width: 60%">
		<select id="formaMaterial" name="limite.formaMaterial" style="width: 35%">
			<option value="">&lt&lt SELECIONE &gt&gt</option>
			<c:forEach var="formaMaterial" items="${listaFormaMaterial}">
				<option value="${formaMaterial}" <c:if test="${formaMaterial eq limite.formaMaterial}">selected</c:if>>${formaMaterial.descricao}</option>
			</c:forEach>
		</select>
	</div>
	<div class="label">Material:</div>
	<div class="input" style="width: 70%">
		<input type="text" id="material" name="limite.material.descricaoFormatada" style="width: 50%" value="${limite.material.descricao}"/>
		<div class="suggestionsBox" id="containerPesquisaMaterial" style="display: none; width: 50%"></div>
	</div>
	<div class="label">Med. Ext / Espessura:</div>
	<div class="input" style="width: 10%">
		<input type="text" id="medidaExterna" name="limite.medidaExterna"
			value="${limite.medidaExterna}" maxlength="11" />
	</div>

	<div class="label">Med. Int / Largura:</div>
	<div class="input" style="width: 10%">
		<input type="text" id="medidaInterna" name="limite.medidaInterna"
			value="${limite.medidaInterna}" maxlength="11" style="width: 90%" />
	</div>

	<div class="label" style="width: 10%">Comprimento:</div>
	<div class="input" style="width: 30%">
		<input type="text" id="comprimento" name="limite.comprimento"
			value="${limite.comprimento}" maxlength="11" style="width: 30%" />
	</div>
	<div class="label">Taxa Mín.(%):</div>
	<div class="input" style="width: 7%">
		<input type="text" id="taxaMinima" name="limite.taxaMinima" value="${limite.taxaMinima}"/>
	</div>
	
	<div class="bloco_botoes">
		<a id="botaoPesquisarLimite" class="botaoPesquisar" title="Pesquisar Limite Mínimo"/></a>
		<a type="submit" id="botaoInserirLimite" title="Editar o limite mínimo do estoque" class="botaoInserir"></a>
		<a id="botaoLimparLimite" title="Limpar Dados do Item" class="botaoLimpar"></a>
	</div>

</fieldset>
</form>