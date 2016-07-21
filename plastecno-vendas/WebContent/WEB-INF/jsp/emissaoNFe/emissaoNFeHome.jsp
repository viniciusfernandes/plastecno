<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">

<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.paginate.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js"/>"></script>


<style type="text/css">
fieldset .fieldsetInterno {
	margin: 0 1% 1% 1%;
}
fieldset .fieldsetInterno legend {
	font-size: 10px;
}
</style>
<script type="text/javascript">

$(document).ready(function() {

	$("#botaoInserirRamo").click(function() {
		toUpperCaseInput();
		$('#formRamo').submit();
				
	});
	
	$("#botaoPesquisarRamo").click(function() {
		toUpperCaseInput();
		inicializarFiltro();
		$('#formPesquisa').submit();
	});
	
	$("#tipoTributacaoICMS").change(function() {
		if("00" === $(this).val()){
			$('.icms00').fadeIn();
			$('.icms10').fadeOut();
		} else if("10" === $(this).val()){
			$('.icms10').fadeIn();
			$('.icms00').fadeOut();
		} 
	});
	
	
	$('#bloco_logradouro').addClass('fieldsetInterno');

	autocompletar({
		url : '<c:url value="/cliente/listagem/nome"/>',
		campoPesquisavel : 'nomeCliente',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaCliente',
		selecionarItem : function(itemLista) {
			// Vamos utilizar a conversao de pedido/cliente/1, onde o ultimo
			// termo se refere ao ID do cliente
			var request = $.ajax({
				type : "get",
				url : '<c:url value="/cliente/serializacao"/>'+ '/' + itemLista.id
			});

			request.done(function(response) {
				var erros = response.erros;
				var contemErro = erros != undefined;
				if (!contemErro) {
					var cliente = response.cliente;
					$('#email').val(cliente.email);
					$('#cnpj').val(cliente.cnpj);
					$('#cpf').val(cliente.cpf);
					$('#inscricaoEstadual').val(cliente.inscricaoEstadual);
					$('#telefone').val(cliente.telefone);
					$('#nomeCliente').val(cliente.razaoSocial);

				} else if (erros != undefined) {
					gerarListaMensagemErro(erros);
				}

			});
			
			request.fail(function(request, status, excecao) {
				var mensagem = 'Falha na pesquisa do autocomplete para o campo: '+ idCampoPesquisavel;
				mensagem += ' para a URL ' + url;
				mensagem += ' contendo o valor de requisicao ' + parametro;
				mensagem += ' => Excecao: ' + excecao;
				gerarListaMensagemErro(new Array(mensagem));
			});
		}
	});
	
	<jsp:include page="/bloco/bloco_paginador.jsp" />
	
});

function inicializarFiltro() {
	$("#filtroSigla").val($("#sigla").val());
	$("#filtroDescricao").val($("#descricao").val());	
}

function inicializarModalCancelamento(botao){
	inicializarModalConfirmacao({
		mensagem: 'Essa ação não poderá será desfeita. Você tem certeza de que deseja DESATIVAR esse ramo de atividade?',
		confirmar: function(){
			$(botao).closest('form').submit();	
		}
	});
}
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
	<div id="modal"></div>
	<form id="formVazio" ></form>

	<form id="formPesquisa" action="<c:url value="/emissaoNFe/pesquisarpedido"/>"
		method="get">
	</form>
	<form id="formEmissao" action="<c:url value="/emissaoNFe/emitirNFe"/>"
		method="post">
		<input type="hidden" name="idPedido" value="${idPedido}"/>
		<fieldset>
			<legend>::: Dados da NF-e :::</legend>
			<div class="label obrigatorio">Tipo Documento:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado"  class="semprehabilitado" style="width: 100%">
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Forma Pagamento:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado" name="nf.identificacaoNFe.indicadorFormaPagamento" class="semprehabilitado" style="width: 100%">
					<c:forEach var="formaPagamento" items="${listaTipoFormaPagamento}">
						<option value="${formaPagamento.codigo}" <c:if test="${formaPagamento eq formaPagamentoPadrao}">selected</c:if>>${formaPagamento.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Forma Emissão:</div>
			<div class="input" style="width: 20%">
				<select name="nf.identificacaoNFe.tipoEmissao" class="semprehabilitado" style="width: 50%">
					<c:forEach var="tipoEmissao" items="${listaTipoEmissao}">
						<option value="${tipoEmissao.codigo}" <c:if test="${tipoEmissao eq tipoEmissaoPadrao}">selected</c:if>>${tipoEmissao.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Finalidade Emissão:</div>
			<div class="input" style="width: 10%">
				<select name="nf.identificacaoNFe.finalidadeEmissao"
					style="width: 100%" class="semprehabilitado">
					<c:forEach var="finalidade" items="${listaTipoFinalidadeEmissao}">
						<option value="${finalidade.codigo}" <c:if test="${finalidade eq finalidadeEmissaoPadrao}">selected</c:if>>${finalidade.descricao}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Tipo Impressão:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado"
					style="width: 100%" class="semprehabilitado">
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Consumidor Final:</div>
			<div class="input" style="width: 20%">
				<select id="pedidoAssociado" 
					style="width: 50%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Destino Operação:</div>
			<div class="input" style="width: 10%">
				<select id="pedidoAssociado" 
					style="width: 100%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			<div class="label obrigatorio">Natureza Operação:</div>
			<div class="input" style="width: 36%">
				<select id="pedidoAssociado"
					style="width: 100%" class="semprehabilitado">
					<option value=""></option>
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
		</fieldset>
		
		<fieldset>
			<legend>::: Destinatário :::</legend>
			<div class="label obrigatorio">Razão Social/Nome:</div>
			<div class="input" style="width: 80%">
				<input type="text" id="nomeCliente" name="nf.identificacaoDestinatarioNFe.nomeFantasia" value="${cliente.razaoSocial}" class="pesquisavel" style="widows: 60%"/>
				<div class="suggestionsBox" id="containerPesquisaCliente" style="display: none; width: 50%"></div>
			</div>
			
			<div class="label">CNPJ:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="cnpj" name="nf.identificacaoDestinatarioNFe.cnpj"
					value="${cliente.cnpj}" class="pesquisavel" />
			</div>
			<div class="label">Insc. Estadual:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="inscricaoEstadual"
					name="nf.identificacaoDestinatarioNFe.inscricaoEstadual"
					value="${cliente.inscricaoEstadual}"
					style="width: 40%; text-align: right;" />
			</div>
			<div class="label">CPF:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="cpf" name="nf.identificacaoDestinatarioNFe.cpf"
					value="${cliente.cpf}" class="pesquisavel" />
			</div>
			<div class="label">Telefone:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="telefone" name="cliente.cpf"
					value="${cliente.contatoPrincipal.telefoneFormatado}" class="pesquisavel" />
			</div>
			<div class="label">Email:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="email" name="nf.identificacaoDestinatarioNFe.email"
					value="${cliente.email}" class="apenasLowerCase uppercaseBloqueado lowerCase" />
			</div>
			<jsp:include page="/bloco/bloco_logradouro.jsp"></jsp:include>
		</fieldset>
		
		<fieldset>
			<legend>::: Produtos e Serviços :::</legend>
			<div class="label obrigatorio">CFOP:</div>
			<div class="input" style="width: 80%">
				<select id="pedidoAssociado" 
					style="width: 10%" class="semprehabilitado">
					<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
						<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
					</c:forEach>
				</select>
			</div>
			
			<fieldset class="fieldsetInterno">
				<legend>::: ICMS :::</legend>
				<div class="label obrigatorio">Regime:</div>
				<div class="input" style="width: 10%">
					<select id="pedidoAssociado" 
						style="width: 100%" class="semprehabilitado">
						<c:forEach var="icms" items="${listaRegime}">
							<option value="${icms.codigo}">${icms.descricao}</option>
						</c:forEach>
					</select>
				</div>
				<div class="label obrigatorio">Situação Tribut.:</div>
				<div class="input" style="width: 50%">
					<select id="tipoTributacaoICMS" 
						style="width: 80%" class="semprehabilitado">
						<c:forEach var="icms" items="${listaTipoTributacaoICMS}">
							<option value="${icms.codigo}">${icms.descricao}</option>
						</c:forEach>
					</select>
				</div>
				<div class="label obrigatorio">Origem:</div>
				<div class="input" style="width: 70%">
					<select id="pedidoAssociado" 
						style="width: 100%" class="semprehabilitado">
						<c:forEach var="origem" items="${listaTipoOrigemMercadoria}">
							<option value="${origem.codigo}">${origem.descricao}</option>
						</c:forEach>
					</select>
				</div>
				
				<div class="icms00 label obrigatorio">ICMS 00:</div>
				<div class="icms00 input" style="width: 70%">
					<select style="width: 100%" class="icms00 semprehabilitado">
						<option value="00">icms 00</option>
					</select>
				</div>
				<div  class="icms10 label obrigatorio">ICMS 10:</div>
				<div class="icms10 input" style="width: 70%">
					<input type="text" id="icms10" style="width: 100%" class="icms10 semprehabilitado"/>
				</div>
				
			</fieldset>
			
			<fieldset class="fieldsetInterno">
				<legend>::: IPI :::</legend>
				<div class="label obrigatorio">Situação Tribut.:</div>
				<div class="input" style="width: 10%">
					<select id="pedidoAssociado" 
						style="width: 100%" class="semprehabilitado">
						<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
							<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
						</c:forEach>
					</select>
				</div>
			</fieldset>
			
			<fieldset class="fieldsetInterno">
				<legend>::: COFINS :::</legend>
				<div class="label obrigatorio">Situação Tribut.:</div>
				<div class="input" style="width: 10%">
					<select id="pedidoAssociado" 
						style="width: 100%" class="semprehabilitado">
						<c:forEach var="idPedidoAssociado" items="${listaIdPedidoAssociado}">
							<option value="${idPedidoAssociado}">${idPedidoAssociado}</option>
						</c:forEach>
					</select>
				</div>
			</fieldset>
		</fieldset>
		
			<div class="label">Info. Adicionais.:</div>
			<div class="input areatexto" style="width: 70%">
				<textarea name="nf.informacoesAdicionaisNFe.informacoesAdicionaisInteresseFisco" style="width: 100%"></textarea>
			</div>
			
			<table class="listrada">
				<thead>
					<tr>
						<th>Item</th>
						<th>Desc.</th>
						<th>NCM</th>
						<th>CFOP</th>
						<th>Venda</th>
						<th>Qtde.</th>
						<th>Unid.(R$)</th>
						<th>Tot.(R$)</th>
						<th>BC ICMS</th>
						<th>V. ICMS(R$)</th>
						<th>V IPI.(R$)</th>
						<th>Aliq. ICMS(R$)</th>
						<th>Aliq. IPI(R$)</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${listaItem}" varStatus="count">
						<tr>
							<td>${item.sequencial}</td>
							<td>${item.descricaoSemFormatacao}</td>
							<td>${item.ncm}</td>
							<td>null</td>
							<td>${item.tipoVenda}</td>
							<td>${item.quantidade}</td>
							<td>${item.precoUnidade}</td>
							<td>${item.valorTotal}</td>
							<td>${item.valorTotal}</td>
							<td>${item.valorICMS}</td>
							<td>null</td>
							<td>${item.aliquotaICMS}</td>
							<td>${item.aliquotaIPI}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		
		
		<c:forEach var="item" items="${listaItem}" varStatus="count">
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].numeroItem"></c:out>" value="${item.sequencial}"/>
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].produtoServicoNFe.codigo"></c:out>" value="${item.descricaoSemFormatacao}"/>
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].produtoServicoNFe.descricao"></c:out>" value="${item.descricaoSemFormatacao}"/>
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].produtoServicoNFe.ncm"></c:out>" value="${item.ncm}"/>
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].produtoServicoNFe.CFOP"></c:out>" />
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].produtoServicoNFe.unidadeComercial"></c:out>" value="${item.tipoVenda}"/>
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].produtoServicoNFe.quantidadeComercial"></c:out>" value="${item.quantidade}"/>
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].produtoServicoNFe.quantidadeTributavel"></c:out>" value="${item.quantidade}"/>
			<input type="hidden" name="<c:out value="nf.listaItem[${count.index}].produtoServicoNFe.valorUnitarioComercializacao"></c:out>" value="${item.precoUnidade}"/>
		</c:forEach>
		
		<div class="bloco_botoes">
			<input type="submit" id="botaoEnviarNF" title="Enviar Nota Fiscal" value="" class="botaoEnviarEmail"/>
		</div>
		
	</form>

	
</body>
</html>
