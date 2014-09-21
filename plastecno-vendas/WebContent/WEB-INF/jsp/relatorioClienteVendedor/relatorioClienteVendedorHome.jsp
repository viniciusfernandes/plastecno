<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>

<title>Relatório de Clientes do Vendedor</title>
<script type="text/javascript">

$(document).ready(function() {
	<c:if test="${acessoClienteVendedorPermitido}">
		autocompletar(
				{
					url: '<c:url value="/relatorio/cliente/vendedor/nome"/>',
					campoPesquisavel: 'nomeVendedor', 
					parametro: 'nome', 
					containerResultados: 'containerPesquisaVendedor',
					selecionarItem: function (itemLista){
						$('#idVendedor').val(itemLista.id);
					}
				}
		);
	</c:if>
	$('#chekboxClienteInativo').change(function (){
		$('#formPesquisa #clienteInativo').val($(this).prop('checked'));
	});
}); 

</script>
</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />

		<fieldset>
			<legend>::: Dados do Relatório de Clientes do Vendedor :::</legend>
					<div class="label" style="width: 30%">Pesquisar Clientes Inativos:</div>
					<div class="input" style="width: 60%">
						<input type="checkbox" id="chekboxClienteInativo" 
							<c:if test="${pesquisaClienteInativo}">checked</c:if> class="checkbox"/>
					</div>
					<div class="label" style="width: 30%">Vendedor:</div>
					<div class="input" style="width: 30%">
						<input type="text" id="nomeVendedor" value="${vendedor.nomeCompleto}" maxlength="50" 
							class="pesquisavel <c:if test="${not acessoClienteVendedorPermitido}">desabilidado</c:if>" 
							<c:if test="${not acessoClienteVendedorPermitido}">disabled="disabled"</c:if>/>
						<div class="suggestionsBox" id="containerPesquisaVendedor" style="display:none; width: 50%"></div>
					</div>
		</fieldset>
		<div class="bloco_botoes">
						<form id="formPesquisa" action="<c:url value="/relatorio/cliente/vendedor/listagem"/>" method="get">
							<input type="hidden" id="idVendedor" name="idVendedor" value="${vendedor.id}"/>
							<input type="hidden" id="clienteInativo" name="pesquisaClienteInativo" value="${pesquisaClienteInativo}"/>
							<input type="submit" title="Pesquisar Clientes do Vendedor" value="" class="botaoPesquisar"/>
							
						</form>
						<form action="<c:url value="/relatorio/cliente/vendedor"/>" method="get">
							<input type="submit" value="" title="Limpar Dados do Relatório de Clientes" class="botaoLimpar"/>			
						</form>
		</div>
	
	<a id="rodape"></a>

	<c:if test="${relatorioGerado}">
	<table class="listrada">
		<caption>${tituloRelatorio}</caption>
		<thead>
			<tr>
				<th style="width: 10%">Ult. Cont.</th>
				<th style="width: 50%">Cliente</th>
				<th style="width: 35%">Contato</th>
				<th>Ações</th>
			</tr>
		</thead>

		<tbody>
			<c:forEach var="cliente" items="${listaCliente}">
				<tr>
					<td>${cliente.dataUltimoContatoFormatada}</td>
					<td>${cliente.nomeCompleto}</td>
					<td>${cliente.contatoPrincipal.nome} -
						${cliente.contatoPrincipal.telefoneFormatado}</td>
					<td>
						<div class="coluna_acoes_listagem">
							<form action="<c:url value="/cliente/${cliente.id}"/>"
								method="get">
								<input type="submit" title="Vizualizar Dados do Cliente"
									value="" class="botaoEditar" />
							</form>
						</div>
					</td>
				</tr>

			</c:forEach>
		</tbody>
	</table>
	</c:if>
</body>
</html>