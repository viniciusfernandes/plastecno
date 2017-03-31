<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />


<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>


<title>Relat�rio das Duplicatas</title>
<script type="text/javascript">
	$(document).ready(function() {
		$('#botaoLimpar').click(function () {
			$('#formVazio').submit();
		});
		inserirMascaraData('dataInicial');
		inserirMascaraData('dataFinal');
		inserirMascaraData('dataVencimento');
		inserirMascaraMonetaria('valor', 10);
		
		$('#botaoPesquisar').click(function () {
			adicionarInputHiddenFormulario('formVazio', 'dataInicial', document.getElementById('dataInicial').value);
			adicionarInputHiddenFormulario('formVazio', 'dataFinal', document.getElementById('dataFinal').value);
			$('#formVazio').attr('action', '<c:url value="/relatorio/duplicata/listagem"/>').attr('method', 'get').submit();
		});
	});
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
	<form id="formVazio" action="<c:url value="/relatorio/duplicata"/>">
	</form>

		<fieldset id="bloco_pesquisa">
			<legend>::: Relat�rio das Duplicatas :::</legend>
			<div class="label obrigatorio" style="width: 30%">Data In�cial:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataInicial" name="dataInicial"
					value="${dataInicial}" maxlength="10" class="pesquisavel" />
			</div>

			<div class="label obrigatorio" style="width: 10%">Data Final:</div>
			<div class="input" style="width: 35%">
				<input type="text" id="dataFinal" name="dataFinal"
					value="${dataFinal}" maxlength="100" class="pesquisavel"
					style="width: 45%" />
			</div>
		</fieldset>
		<div class="bloco_botoes">
			<input id="botaoPesquisar" type="button" class="botaoPesquisar" title="Pesquisar Dados das Duplicatas no Per�odo" /> 
			<input id="botaoLimpar" type="button" value="" title="Limpar Dados das Duplicatas no Per�odo" class="botaoLimpar" />
		</div>

	<c:if test="${not empty idDuplicata}">
	<fieldset id="bloco_edicao_duplicata">
		<legend>::: Relat�rio das Duplicatas :::</legend>
		<form action="<c:url value="/duplicata/alteracaodata"/>" method="post">
			<input type="hidden" name="idDuplicata" value="${idDuplicata}"/>
			<input type="hidden" name="dataInicial" value="${dataInicial}"/>
			<input type="hidden" name="dataFinal" value="${dataFinal}"/>
			<div class="label">Dt Venc.:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="dataVencimento" name="dataVencimento" value="${dataVencimento}" />
			</div>
			<div class="label">Vl. (R$):</div>
			<div class="input" style="width: 15%">
				<input type="text" id="valor" name="valor" value="${valor}"/>
			</div>
			<div class="bloco_botoes">
				<input type="submit" value="" class="botaoInserir" title="Alterar Data da Duplicata"/>
			</div>
		</form>
	</fieldset>
	</c:if>
	
	<a id="rodape"></a>
		<c:if test="${not empty relatorio}">
		<table id="tabelaItemPedido" class="listrada">
			<caption>${relatorio.titulo}</caption>
			<thead>
				<tr>
					<th style="width: 10%">NFe</th>
					<th style="width: 35%">Cliente</th>
					<th style="width: 15%">Data Venc.</th>
					<th style="width: 10%">Vl. (R$)</th>
					<th style="width: 15%">Situa��o</th>
					<th style="width: 5%">A��es</th>
				</tr>
			</thead>
			
			<tbody>
			
			<c:forEach items="${relatorio.listaGrupo}" var="grupo" varStatus="iGrupo">
					<c:forEach items="${grupo.listaElemento}" var="elemento" varStatus="iElemento">
						<tr>
							<c:if test="${iElemento.count le 1}">
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento}">${grupo.id}</td>
								<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}" rowspan="${grupo.totalElemento}">${grupo.propriedades['nomeCliente']}</td>
							</c:if>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.dataVencimentoFormatada}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.valor}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">${elemento.tipoSituacaoDuplicata.descricao}</td>
							<td class="fundo${iGrupo.index % 2 == 0 ? 1 : 2}">
								<div class="coluna_acoes_listagem">
									<form action="<c:url value="/duplicata/${elemento.id}"/>" method="get">
										<input type="hidden" name="dataInicial" value="${dataInicial}"/>
										<input type="hidden" name="dataFinal" value="${dataFinal}"/>
										<input type="submit" title="Editar Duplicata" value="" class="botaoEditar" />
									</form>
									<form action="<c:url value="/duplicata/liquidacao/${elemento.id}"/>" method="post">
										<input type="hidden" name="dataInicial" value="${dataInicial}"/>
										<input type="hidden" name="dataFinal" value="${dataFinal}"/>
										<input type="submit" title="Liquidar Duplicata" value="" class="botaoVerificarPequeno" />
									</form>
									<form action="<c:url value="/duplicata/remocao/${elemento.id}"/>" method="post">
										<input type="hidden" name="dataInicial" value="${dataInicial}"/>
										<input type="hidden" name="dataFinal" value="${dataFinal}"/>
										<input type="submit" title="Remover Duplicata" value="" class="botaoRemover" />
									</form>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>

			</tbody>
		</table>
		</c:if>
</body>
</html>