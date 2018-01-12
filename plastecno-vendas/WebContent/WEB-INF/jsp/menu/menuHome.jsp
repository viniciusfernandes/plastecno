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
	<div class="main_wrapper">
		<nav>
			<ul>
				<c:if test="${acessoVendaPermitido}">
				<li id="menuPedido"><a href="pedido/venda" target="principal_frame">Ped. Vendas +</a>
					<ul>
						<li><a href="itemAguardandoMaterial" target="principal_frame">Ped. Aguard. Material</a></li>
						<li><a href="estoque" target="principal_frame">Estoque</a></li>
					</ul>
				</li>
				</c:if>
				<li><a href="cliente" target="principal_frame">Clientes</a></li>
				<c:if test="${acessoVendaPermitido}">
					<li><a href="orcamento" target="principal_frame">Orçamento</a></li>
				</c:if>
				<c:if test="${acessoCompraPermitido}">
					<li id="menuPedidoCompra"><a href="pedido/compra" target="principal_frame">Ped. Compras +</a>
						<ul>
							<li><a href="itemAguardandoCompra/item/listagem" target="principal_frame">Item Aguard. Compra</a></li>
							<li><a href="compra/recepcao/listagem" target="principal_frame">Recepção Compras</a></li>
							<li><a href="estoque" target="principal_frame">Estoque</a></li>
							<li><a href="empacotamento" target="principal_frame">Empacotamento</a></li>					
						</ul>
					</li>
				</c:if>
				<c:if test="${acessoRecepcaoCompraPermitido}">
					<li><a href="javascript: void(0)" target="principal_frame">Recepção Compras +</a>
						<ul>
							<li><a href="compra/recepcao" target="principal_frame">Recepção Compras</a></li>
							<li><a href="estoque" target="principal_frame">Estoque</a></li>
						</ul>
					</li>
				</c:if>
				<li id="menuCadastros"><a href="javascript: void(0)">Cadastros +</a>
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
				<c:if test="${acessoNFePermitido}">
				<li id="menuNFe"><a href="emissaoNFe" target="principal_frame">Emis. NFe +</a>
					<ul>
						<li><a href="pedidoFracionadoNFe" target="principal_frame">Ped. Fracionado</a></li>
					</ul>
				</li>
				</c:if>
				
				<!-- c:if test="${acessoValorReceitaPermitido}" -->
				<c:if test="${false}">
				<li><a href="javascript: void(0)">Receita</a>
					<ul>
						<li><a href="relatorio/receita" target="principal_frame">Receita Estimada</a></li>
						<li><a href="relatorio/comissao/vendedor" target="principal_frame">Comissão Estim. Vendedor</a></li>
					</ul>
				</li>
				</c:if>
				<c:if test="${acessoPagamentoPermitido}">
				<li>
					<a href="pagamento/periodo/listagem" target="principal_frame">Pagamento</a>
				</li>
				</c:if>
				<li id="menuRelatorios"><a href="javascript: void(0)">Relatórios +</a>
					<ul>
						<c:if test="${acessoFluxoCaixaPermitido}">
							<li><a href="fluxocaixa" target="principal_frame">Fluxo Caixa</a></li>
						</c:if>
						<c:if test="${acessoRelatorioDuplicataPermitido}">
							<li><a href="relatorio/duplicata" target="principal_frame">Duplicatas</a></li>
						</c:if>
						<c:if test="${acessoRelatorioComissaoVendedorPermitido}">
							<li><a href="relatorio/comissao/vendedor" target="principal_frame">Comissão Vendedor</a></li>
						</c:if>
						<c:if test="${acessoRelatorioFaturamentoPermitido}">
							<li><a href="relatorio/faturamento" target="principal_frame">Faturamento</a></li>
						</c:if>
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
			<iframe id="conteudo_principal" name="principal_frame"></iframe>
		</div>
	</div>
	</div>
</body>
</html>
