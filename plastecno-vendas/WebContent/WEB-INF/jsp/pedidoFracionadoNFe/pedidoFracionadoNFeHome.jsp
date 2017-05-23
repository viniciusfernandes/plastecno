<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<jsp:include page="/bloco/bloco_modal_js.jsp" />

</head>
<script type="text/javascript">
function removerItem(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja REMOVER esse item?',
		confirmar: function(){
			$(botao).closest('form').submit();	
		}
	});
};

</script>
<body>
	<div id="modal"></div>
	<fieldset>
		<legend>::: ${relatorio.titulo} :::</legend>
		<table class="listrada">
			<thead>
				<tr>
					<th style="width: 10%">Pedido</th>
					<th style="width: 5%">Item</th>
					<th style="width: 5%">Qtde. Frac.</th>
					<th style="width: 5%">Qtde.</th>
					<th style="width: 40%">Descrição</th>
					<th style="width: 12%">NFe</th>
					<th style="width: 5%">Valor</th>
					<th style="width: 8%">Ação</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${relatorio.listaGrupo}" var="grupo" varStatus="iGrupo">
					<c:forEach items="${grupo.listaElemento}" var="item" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento}">${grupo.id}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.numeroItem}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidadeFracionada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.quantidade}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.numeroNFe}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${item.valorBruto}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="pedidoFracionado/emissaoNFe"/>" >
										<input type="hidden" name="idPedido" value="${grupo.id}" /> 
										<input type="submit" value="" title="Emissão de Pedido" class="botaoEditar"/>
									</form>
									<form action="<c:url value="/pedido/pdf"/>" >
										<input type="hidden" name="idPedido" value="${grupo.id}" /> 
										<input type="submit" value="" title="Visualizar Pedido PDF" class="botaoPdf_16 botaoPdf_16_centro"/>
									</form>
									<form action="<c:url value="pedidoFracionado/remocao"/>" method="post">
										<input type="hidden" name="idItemFracionado" value="${item.id}" /> 
										<input type="button" value="" title="Remover Item Fracionado" class="botaoRemover" onclick="removerItem(this);"/>
									</form>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>
		</fieldset>
</body>
</html>