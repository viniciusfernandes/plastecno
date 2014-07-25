<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<fieldset class="bloco_picklist">
	<c:if test="${possuiCampoPesquisa}">
		<div class="label" >${tituloCampoPesquisa}:</div>
			<div class="input" style="width: 20%">
			<input type="text" id="campoPesquisaPicklist" class="pesquisavel"/>
		</div>
		<div class="input" style="width: 60%">
			<input type="button" id="botaoPesquisaPicklist" title="Pesquisar ${tituloPicklist}" value="" class="botaoPesquisarPequeno" style="width: 20px"/>
		</div>
	</c:if>
	

	<legend >::: ${tituloPicklist} :::</legend>
	<div class="picklist" style="width: 33%; margin-left: 16%;">
		<div class="tituloElementos">${labelElementosNaoAssociados}:</div>
		<div class="input" style="width: 100%">
			<select id="SelectList" size="8" multiple="multiple" style="width: 100%">
				<c:forEach var="elemento" items="${mapaElementosNaoAssociados}">
					<option value="${elemento.key}">${elemento.value}</option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="blocoBotoesPicklist picklist">
		<div title="Associar Elemento" class="botaoAdicionarPicklist" onclick="addIt();"></div>
		<div title="Remover Associacao" class="botaoRemoverPicklist" onclick="delIt()"></div>
	</div>

	<div class="picklist" style="width: 33%">
		<div class="tituloElementos <c:if test="${preenchimentoPicklistObrigatorio}">obrigatorio</c:if>">${labelElementosAssociados}:</div>
		<div class="input" style="width: 100%">
			<select id="PickList" size="8" multiple="multiple" style="width: 100%;">
				<c:forEach var="elemento" items="${mapaElementosAssociados}">
					<option value="${elemento.key}">${elemento.value}</option>
				</c:forEach>
			</select>
		</div>
	</div>

</fieldset>