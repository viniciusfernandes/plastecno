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
		<header>
			<div id="header_content"></div>
			<div class="logo"></div>
			<form action="/vendas/login/sair" method="get">
				<div class="bloco_autenticacao">
					<input value="" class="botaoLogout" title="Sair no sistema" type="submit"> <label class="bemVindo">Bem-vindo,
						VINICIUS FERNANDES - 27/12/2017 12:23:54
					</label>
				</div>
			</form>
		</header>

		<div class="enfeite flutuante_esquerda"></div>
		<div class="enfeite flutuante_esquerda"></div>
		<div class="enfeite flutuante_esquerda" style="margin-bottom: 5px;"></div>


		<div class="main_wrapper">
			<nav>
				<ul>
					
					<li><a href="pedido" target="principal_frame">Ped. Vendas +</a>
						<ul>
							<li><a href="itemAguardandoMaterial" target="principal_frame">Ped. Aguard. Material</a></li>
							<li><a href="estoque" target="principal_frame">Estoque</a></li>
						</ul>
					</li>
					
					<li><a href="cliente" target="principal_frame">Clientes</a></li>
					
						<li><a href="orcamento" target="principal_frame">Orçamento</a></li>
					
					
						<li class="pedidos_compras"><a href="pedido/compra" target="principal_frame">Ped. Compras +</a>
							<ul>
								<li><a href="itemAguardandoCompra/item/listagem" target="principal_frame">Item Aguard. Compra</a></li>
								<li><a href="compra/recepcao/listagem" target="principal_frame">Recepção Compras</a></li>
								<li><a href="estoque" target="principal_frame">Estoque</a></li>
								<li><a href="empacotamento" target="principal_frame">Empacotamento</a></li>					
							</ul>
						</li>
					
					
					<li class="cadastros"><a href="javascript: void(0)">Cadastros +</a>
						<ul>
							<li><a href="ramo" target="principal_frame">Ramos Atividades</a></li>
							<li><a href="representada" target="principal_frame">Represent. / Forneced.</a></li>
							<li><a href="transportadora" target="principal_frame">Transportadoras</a></li>
							<li><a href="material" target="principal_frame">Materiais</a></li>
							
								<li><a href="usuario" target="principal_frame">Usuários</a></li>
								<li><a href="vendedor" target="principal_frame">Vendedores</a></li>
								<li><a href="revendedor" target="principal_frame">Revendedor</a></li>
								<li><a href="comissao" target="principal_frame">Comissão</a></li>
							
							<li><a href="regiao" target="principal_frame">Regiões</a></li>
						</ul>
					</li>
					
					<li class="nfe"><a href="emissaoNFe" target="principal_frame">Emis. NFe +</a>
						<ul>
							<li><a href="pedidoFracionadoNFe" target="principal_frame">Ped. Fracionado</a></li>
							<li><a href="javascript: void(0);"></a></li>
						</ul>
					</li>
					
					
					<!-- c:if test="true" -->
					
					
					<li>
						<a href="pagamento/periodo/listagem" target="principal_frame">Pagamento</a>
					</li>
					
					<li class="relatorios"><a href="javascript: void(0)">Relatórios +</a>
						<ul>
							
								<li><a href="fluxocaixa" target="principal_frame">Fluxo Caixa</a></li>
							
							
								<li><a href="relatorio/duplicata" target="principal_frame">Duplicatas</a></li>
							
							
								<li><a href="relatorio/comissao/vendedor" target="principal_frame">Comissão Vendedor</a></li>
							
							
								<li><a href="relatorio/faturamento" target="principal_frame">Faturamento</a></li>
							
							
								<li><a href="relatorio/pedido/periodo?isCompra=false" target="principal_frame">Valor Venda Período</a></li>
							
							
								<li><a href="relatorio/pedido/periodo?isCompra=true" target="principal_frame">Valor Compra Período</a></li>
							
							
								<li><a href="relatorio/pedido?isCompra=false" target="principal_frame">Pedido Venda Período</a></li>
							
							
								<li><a href="relatorio/pedido?isCompra=true" target="principal_frame">Pedido Compra Período</a></li>
							
							
								<li><a href="relatorio/venda/representada" target="principal_frame">Venda Representada</a></li>
								<li><a href="relatorio/venda/cliente" target="principal_frame">Venda Cliente</a></li>
							
							
								<li><a href="relatorio/pedido?isEntrega=true" target="principal_frame">Acompanhamento Entrega</a></li>
							
							
								<li><a href="relatorio/cliente/ramoAtividade" target="principal_frame">Cliente Ramo Atividade</a></li>
							
							
								<li><a href="relatorio/cliente/regiao" target="principal_frame">Cliente Região</a></li>
							
							
								<li><a href="relatorio/venda/vendedor" target="principal_frame">Venda/Orçamto Vendedor</a></li>
								<li><a href="relatorio/cliente/vendedor" target="principal_frame">Cliente Vendedor</a></li>
							
						</ul></li>
					
				</ul>
			</nav>
			<div style="width:88%; float:right" id="center_content">
				<iframe name="principal_frame"></iframe>
			</div>
		</div>
	</div>


</body>
</html>
