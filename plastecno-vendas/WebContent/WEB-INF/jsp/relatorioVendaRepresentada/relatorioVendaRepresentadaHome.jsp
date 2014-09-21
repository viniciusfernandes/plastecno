<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<jsp:include page="/bloco/bloco_css.jsp"></jsp:include>

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>

<title>Relatório de Vendas por Representada</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
		
		$('#botaoPesquisar').click(function (){
			if(!isAnterior($('#dataInicial').val(), $('#dataFinal').val())){
				gerarListaMensagemErro(new Array('Data inicial deve ser inferior ou igual a data final'));
			} else {
				$(this).closest('form').submit();
			}
		});
	});
</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<form id="formVazio" action="<c:url value="/relatorio/venda/representada"/>" ></form>
	
	<form action="<c:url value="/relatorio/venda/representada/pdf"/>" >
		<fieldset>
			<legend>::: Relatório de Vendas por Representada :::</legend>
					<div class="label obrigatorio" style="width: 30%">Data Inícial:</div>
					<div class="input" style="width: 15%">
						<input type="text" id="dataInicial" name="dataInicial" value="${dataInicial}" maxlength="10" class="pesquisavel" />
					</div>
		
					<div class="label obrigatorio" style="width: 10%">Data Final:</div>
					<div class="input" style="width: 35%">
						<input type="text" id="dataFinal" name="dataFinal" value="${dataFinal}" maxlength="100" class="pesquisavel" style="width: 45%"/>
					</div>
					<div class="label obrigatorio" style="width: 30%">Representada:</div>
					<div class="input" style="width: 40%">
						<select name="idRepresentada" style="width: 70%">
							<c:forEach var="representada" items="${listaRepresentada}">
								<option value="${representada.id}" <c:if test="${representada eq representadaSelecionada}">selected</c:if>>${representada.nomeFantasia}</option>
							</c:forEach>
						</select>
					</div>
		</fieldset>
		<div class="bloco_botoes">
			<input id="botaoPesquisar"  type="button" value="" class="botaoPesquisar"/>
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados de Geração do Relatório de Vendas" class="botaoLimpar"/>
		</div>
	</form>
	
	<a id="rodape"></a>
	
</body>
</html>