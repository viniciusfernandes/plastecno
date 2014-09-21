<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>


<title>Relatório de Vendas por Rerepsentada</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/cliente/ramoAtividade"/>" ></form>
	
	<form action="<c:url value="/relatorio/cliente/ramoAtividade/listagem"/>" method="get">
		<fieldset>
			<legend>::: Relatório de Clientes por Ramos de Atividades :::</legend>
			<div class="label obrigatorio" style="width: 35%">Ramo Atividade:</div>
			<div class="input" style="width: 50%">
				<select id="ramoAtividade" name="idRamoAtividade" style="width: 30%">
					<option value="">&lt&lt SELECIONE &gt&gt</option>
					<c:forEach var="ramoAtividade" items="${listaRamoAtividade}">
						<option value="${ramoAtividade.id}" <c:if test="${ramoAtividade.id eq ramoAtividadeSelecionado}">selected</c:if>>${ramoAtividade.sigla}</option>
					</c:forEach>
				</select>
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input type="submit" value="" class="botaoPesquisar"/>
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Cliente por Ramo de Atividades" class="botaoLimpar"/>
		</div>
	</form>
	
	<a id="rodape"></a>
	<c:if test="${relatorioGerado}">
	<table class="listrada">
		<caption>${relatorio.titulo}</caption>
		<thead>
			<tr>
				<th style="width: 20%">Vendedor</th>
				<th style="width: 30%">Cliente</th>
				<th style="width: 50%">Contato</th>
			</tr>			
		</thead>
		<tbody>
			<c:forEach items="${relatorio.listaVendedorCliente}" var="vendedor" varStatus="indiceVendedor"> 
				<!-- bloco de totalizacao de valores vendidos para cada representada -->		
				<c:forEach items="${vendedor.listaCliente}" var="cliente" varStatus="indiceCliente">
					<tr>
						<c:if test="${indiceCliente.count eq 1}">
							<td class="fundo${indiceVendedor.index % 2 == 0 ? 1 : 2}" 
								rowspan="${vendedor.totalClientes}">${vendedor.nome}</td>
						</c:if>
						<td class="fundo${indiceVendedor.index % 2 == 0 ? 1 : 2}">${cliente.nome}</td>
						<td class="fundo${indiceVendedor.index % 2 == 0 ? 1 : 2}">${cliente.contato}</td>
					</tr>
				</c:forEach>
			</c:forEach>
			
		</tbody>
		
	</table>
	</c:if>
</body>
</html>