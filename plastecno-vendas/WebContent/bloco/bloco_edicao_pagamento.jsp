<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">

$(document).ready(function() {
	$('#botaoLimparPagamento').click(function(){
		$('#formPagamento input:text').val('');
		$('#formPagamento input:hidden').val('');
		$('#formPagamento select').val('');	
	});
	
	inserirMascaraData('dataVencimento');
	inserirMascaraData('dataEmissao');
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
	inserirMascaraData('dataRecebimento');

	inserirMascaraMonetaria('valorNF', 9);
	inserirMascaraMonetaria('valor', 9);
	inserirMascaraMonetaria('valorCreditoICMS', 9);
	inserirMascaraNumerica('quantidade', '99999');
	inserirMascaraNumerica('parcela', '999');
	inserirMascaraNumerica('pedido', '9999999');
	inserirMascaraNumerica('totalParcelas', '99999');
	inserirMascaraNumerica('numeroNF', '9999999');
	inserirMascaraNumerica('sequencial', '99');

	autocompletar({
		url : '<c:url value="/pagamento/fornecedor/listagem"/>',
		campoPesquisavel : 'fornecedor',
		parametro : 'nomeFantasia',
		containerResultados : 'containerPesquisaFornecedor',
		selecionarItem : function(itemLista) {
			$('#idFornecedor').val(itemLista.id);
		}
	});
});
</script>
<form id="formPagamento" action="<c:url value="/pagamento/inclusao"/>" method="post">
		<input type="hidden" id="idFornecedor" name="pagamento.idFornecedor" value="${pagamento.idFornecedor}"/>
		<input type="hidden" id="idPagamento" name="pagamento.id" value="${pagamento.id}"/>
		<input type="hidden" id="idItemPedidoPagamento" name="pagamento.idItemPedido" value="${pagamento.idItemPedido}"/>
		
	<fieldset>
		<legend>Pagamento</legend>
		<div class="label">Dt. Venc.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataVencimento" name="pagamento.dataVencimento" value="${pagamento.dataVencimentoFormatada}" style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Tipo:</div>
		<div class="input" style="width: 13%">
			<select id="tipoPagamento" name="pagamento.tipoPagamento" style="width: 100%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="tipoPagamento" items="${listaTipoPagamento}">
					<option value="${tipoPagamento}"
						<c:if test="${tipoPagamento eq pagamento.tipoPagamento}">selected</c:if>>${tipoPagamento}</option>
				</c:forEach>
			</select>
		</div>
		<div class="label" style="width: 10%">Situação:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="situacao" value="${pagamento.situacaoPagamento}" class="desabilitado" disabled="disabled" style="width: 75%"/>
		</div>
		<div class="label obrigatorio" >Parc.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="parcela" name="pagamento.parcela" value="${pagamento.parcela}" style="width: 100%"/>
		</div>
		<div class="label" style="width: 9%">Tot. Parc.:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="totalParcelas" name="pagamento.totalParcelas" value="${pagamento.totalParcelas}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Val. Parc:</div>
		<div class="input" style="width: 40%">
			<input type="text" id="valor" name="pagamento.valor" value="${pagamento.valor}" style="width: 75%"/>
		</div>
			
		<div class="label obrigatorio">NF:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="numeroNF" name="pagamento.numeroNF" value="${pagamento.numeroNF}" style="width: 100%"/>
		</div>
		<div class="input" style="width: 2%">
			<input type="button" id="botaoPesquisarNF" title="Pesquisar Pagamentos da NF" value="" class="botaoPesquisarPequeno" />
		</div>
		<div class="label" style="width: 7%">Val. NF:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="valorNF" name="pagamento.valorNF" value="${pagamento.valorNF}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 10%">Dt. Emiss.:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="dataEmissao" name="pagamento.dataEmissao" value="${pagamento.dataEmissaoFormatada}" style="width: 100%"/>
		</div>
		
		<div class="label" style="width: 8%">Dt. Receb.:</div>
		<div class="input" style="width: 11%">
			<input type="text" id="dataRecebimento" name="pagamento.dataRecebimento" value="${pagamento.dataRecebimentoFormatada}" style="width: 100%" />
		</div>
		
		<div class="label">Pedido:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="pedido" name="pagamento.idPedido" value="${pagamento.idPedido}" style="width: 100%"/>
		</div>
		<div class="input" style="width: 2%">
			<input type="button" id="botaoPesquisarPedido" title="Pesquisar Pagamentos do Pedido" value="" class="botaoPesquisarPequeno" />
		</div>
		<div class="label" style="width: 7%">Fornec.:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="fornecedor" name="pagamento.nomeFornecedor" value="${pagamento.nomeFornecedor}" style="width: 100%"/>
			<div class="suggestionsBox" id="containerPesquisaFornecedor" style="display: none; width: 30%"></div>
		</div>
		<div class="input" style="width: 50%">
			<input type="button" id="botaoPesquisarFornecedor" title="Pesquisar Pagamentos do Fornecedor" value="" class="botaoPesquisarPequeno" />
		</div>
		<div class="label">Item:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="sequencial" name="pagamento.sequencialItem" value="${pagamento.sequencialItem}" style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Qtde:</div>
		<div class="input" style="width: 13%">
			<input type="text" id="quantidade" name="pagamento.quantidadeItem" value="${pagamento.quantidadeItem}" style="width: 100%"/>
		</div>
		<div class="label" style="width: 10%">Descrição:</div>
			<div class="input" style="width: 40%">
				<input type="text" id="descricao" name="pagamento.descricao" value="${pagamento.descricao}" style="width: 75%"/>
		</div>
		<div class="label">Cred. ICMS:</div>
		<div class="input" style="width: 10%">
			<input type="text" id="valorCreditoICMS" name="pagamento.valorCreditoICMS" value="${pagamento.valorCreditoICMS}"  style="width: 100%" />
		</div>
		<div class="label" style="width: 9%">Mod. Frete:</div>
		<div class="input" style="width: 13%">
			<select id="frete" name="pagamento.modalidadeFrete" style="width: 100%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="frete" items="${listaModalidadeFrete}">
					<option value="${frete.codigo}"
						<c:if test="${frete.codigo eq pagamento.modalidadeFrete}">selected</c:if>>${frete.descricao}</option>
				</c:forEach>
			</select>
		</div>
		<div class="bloco_botoes">
			<input type="button" id="botaoInserirPagamento" title="Inserir Pagamento" value="" class="botaoInserir"/>
			<input type="button" id="botaoLimparPagamento" value="" title="Limpar Pagamento" class="botaoLimpar" />
		</div>
	</fieldset>
</form>