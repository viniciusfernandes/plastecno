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
</head>
<script type="text/javascript">

$(document).ready(function() {
	scrollTo('${ancora}');
});

</script>
<body>
<fieldset>
		<legend>::: ${relatorio.titulo} :::</legend>
		<table class="listrada">
			<thead>
				<tr>
					<th style="width: 15%">Pedido</th>
					<th style="width: 5%">Item</th>
					<th style="width: 5%">Qtde. Frac.</th>
					<th style="width: 5%">Qtde.</th>
					<th style="width: 50%">Descrição</th>
					<th style="width: 15%">NFe</th>
					<th style="width: 5%">Valor</th>
					<th>Ação</th>
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