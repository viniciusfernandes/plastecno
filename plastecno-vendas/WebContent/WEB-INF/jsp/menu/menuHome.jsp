<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/bloco/bloco_header.jsp" />
<jsp:include page="/bloco/bloco_css.jsp" />
</head>
<body>
	<div id="content">
		<header class="logo">
			<div id="header_content"></div>
		</header>

		<form action="<c:url value="/login/sair"/>" method="get">
			<div class="bloco_autenticacao flutuante_esquerda">
				<input type="submit" value="" class="botaoLogout"
					title="Sair no sistema" /> <label class="bemVindo">Bem-vindo,
					<c:out value="${usuarioInfo.descricaoLogin}" />
				</label>
			</div>
		</form>

		<div class="enfeite flutuante_esquerda"></div>
		<div class="enfeite flutuante_esquerda"></div>
		<div class="enfeite flutuante_esquerda"></div>
		<div class="enfeite flutuante_esquerda"></div>
		<div class="enfeite flutuante_esquerda"></div>

		<nav class="flutuante_esquerda">
			<ul>
				<c:if test="${acessoVendaPermitido}">
				<li><a href="javascript: void(0)" target="principal_frame">Vendas</a>
					<ul>
						<li><a href="pedido" target="principal_frame">Ped. Vendas</a></li>
						<li><a href="revendaEncomendada" target="principal_frame">Ped. Aguard. Material</a></li>
						<li><a href="estoque" target="principal_frame">Estoque</a></li>
					</ul>
				</li>
				</c:if>
				<c:if test="${acessoCompraPermitido}">
					<li><a href="javascript: void(0)" target="principal_frame">Compras</a>
						<ul>
							<li><a href="encomenda" target="principal_frame">Item Aguard. Compra</a></li>
							<li><a href="pedido/compra" target="principal_frame">Ped. Compras</a></li>
							<li><a href="compra/recepcao" target="principal_frame">Recepção Compras</a></li>
							<li><a href="estoque" target="principal_frame">Estoque</a></li>
							<li><a href="empacotamento" target="principal_frame">Empacotamento</a></li>					
						</ul>
					</li>
				</c:if>
				<c:if test="${acessoRecepcaoCompraPermitido}">
					<li><a href="javascript: void(0)" target="principal_frame">Recepção Compras</a>
						<ul>
							<li><a href="compra/recepcao" target="principal_frame">Recepção Compras</a></li>
							<li><a href="estoque" target="principal_frame">Estoque</a></li>
						</ul>
					</li>
				</c:if>
				<li><a href="cliente" target="principal_frame">Clientes</a></li>
				<li><a href="javascript: void(0)">Cadastros</a>
					<ul>
						<li><a href="ramo" target="principal_frame">Ramos Atividades</a></li>
						<li><a href="representada" target="principal_frame">Represent. / Forneced.</a></li>
						<li><a href="transportadora" target="principal_frame">Transportadoras</a></li>
						<li><a href="material" target="principal_frame">Materiais</a></li>
						<c:if test="${acessoAdministracaoPermitido}">
							<li><a href="usuario" target="principal_frame">Usuários</a></li>
							<li><a href="vendedor" target="principal_frame">Vendedores</a></li>
							<li><a href="revendedor" target="principal_frame">Revendedor</a></li>
							<li><a href="comissao" target="principal_frame">Comissão</a></li>
						</c:if>
						<li><a href="regiao" target="principal_frame">Regiões</a></li>
					</ul>
				</li>
				<c:if test="${acessoValorReceitaPermitido}">
				<li><a href="javascript: void(0)">Receita</a>
					<ul>
						<li><a href="relatorio/receita" target="principal_frame">Receita Estimada</a></li>
						<li><a href="relatorio/comissao/vendedor" target="principal_frame">Comissão Estim. Vendedor</a></li>
					</ul>
				</li>
				</c:if>

				<li><a href="javascript: void(0)">Relatórios</a>
					<ul>
						<c:if test="${acessoRelatorioVendasRepresentadaPermitido}">
							<li><a href="relatorio/pedido/periodo?isCompra=false" target="principal_frame">Valor Venda Período</a></li>
						</c:if>
						<c:if test="${acessoCompraPermitido}">
							<li><a href="relatorio/pedido/periodo?isCompra=true" target="principal_frame">Valor Compra Período</a></li>
						</c:if>
						<c:if test="${acessoRelatorioEntregaPermitido}">
							<li><a href="relatorio/pedido?isCompra=false" target="principal_frame">Pedido Venda Período</a></li>
						</c:if>
						<c:if test="${acessoCompraPermitido}">
							<li><a href="relatorio/pedido?isCompra=true" target="principal_frame">Pedido Compra Período</a></li>
						</c:if>
						<c:if test="${acessoRelatorioPedidoRepresentadaPermitido}">
							<li><a href="relatorio/venda/representada" target="principal_frame">Venda Representada</a></li>
							<li><a href="relatorio/venda/cliente" target="principal_frame">Venda Cliente</a></li>
						</c:if>
						<c:if test="${acessoRelatorioEntregaPermitido}">
							<li><a href="relatorio/pedido?isEntrega=true" target="principal_frame">Acompanhamento Entrega</a></li>
						</c:if>
						<c:if test="${acessoRelatorioClienteRamoAtividadePermitido}">
							<li><a href="relatorio/cliente/ramoAtividade" target="principal_frame">Cliente Ramo Atividade</a></li>
						</c:if>
						<c:if test="${acessoRelatorioClienteRegiaoPermitido}">
							<li><a href="relatorio/cliente/regiao" target="principal_frame">Cliente Região</a></li>
						</c:if>
						<c:if test="${acessoVendaPermitido}">
							<li><a href="relatorio/venda/vendedor" target="principal_frame">Venda/Orçamto Vendedor</a></li>
							<li><a href="relatorio/cliente/vendedor" target="principal_frame">Cliente Vendedor</a></li>
						</c:if>
						<c:if test="${acessoRelatorioComissaoVendedor}">
							<li><a href="relatorio/comissao/vendedor" target="principal_frame">Comissão Vendedor</a></li>
						</c:if>
					</ul></li>
				<c:if test="${acessoManutencaoPermitido}">
					<li><a href="javascript: void(0)">Manutenção</a>
						<ul>
							<li><a href="administracao/log" target="principal_frame">Download
									Log Servidor</a></li>
							<li><a href="administracao/sql" target="principal_frame">SQL
									Editor</a></li>
						</ul></li>
				</c:if>
			</ul>
		</nav>
		<div id="center_content">
			<iframe name="principal_frame"
				style="border: 0; height: 95%; width: 100%;"></iframe>
		</div>


		<!--  >div id="footer">Fone: (55)(11)3021-9600  plastecno@plastecno.com.br</div-->
	</div>

</body>
</html>
