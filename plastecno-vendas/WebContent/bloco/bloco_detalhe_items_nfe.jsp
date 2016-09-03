<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:forEach var="item" items="${nf.listaItem}" >
<c:set var="detItem" value="nf.listaItem[${item.indiceItem}]"/>
<%-- bloco de icms --%>
<input type="hidden" id="${detItem}.produtoServicoNFe.ncm" name="${detItem}.produtoServicoNFe.ncm" value="${item.produto.ncm}"/>
<input type="hidden" id="${detItem}.produtoServicoNFe.cfop" name="${detItem}.produtoServicoNFe.cfop" value="${item.produto.cfop}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.codigoSituacaoTributaria" name="${detItem}.tributos.icms.tipoIcms.codigoSituacaoTributaria" value="${item.tributos.icms.tipoIcms.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.origemMercadoria" name="${detItem}.tributos.icms.tipoIcms.origemMercadoria" value="${item.tributos.icms.tipoIcms.origemMercadoria}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.motivoDesoneracao" name="${detItem}.tributos.icms.tipoIcms.motivoDesoneracao" value="${item.tributos.icms.tipoIcms.motivoDesoneracao}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.aliquotaST" name="${detItem}.tributos.icms.tipoIcms.aliquotaST" value="${item.tributos.icms.tipoIcms.aliquotaST}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.valorBCST" name="${detItem}.tributos.icms.tipoIcms.valorBCST" value="${item.tributos.icms.tipoIcms.valorBCST}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.valorBC" name="${detItem}.tributos.icms.tipoIcms.valorBC" value="${item.tributos.icms.tipoIcms.valorBC}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.percentualReducaoBC" name="${detItem}.tributos.icms.tipoIcms.percentualReducaoBC" value="${item.tributos.icms.tipoIcms.percentualReducaoBC}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST" name="${detItem}.tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST" value="${item.tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.modalidadeDeterminacaoBCST" name="${detItem}.tributos.icms.tipoIcms.modalidadeDeterminacaoBCST" value="${item.tributos.icms.tipoIcms.modalidadeDeterminacaoBCST}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.modalidadeDeterminacaoBC" name="${detItem}.tributos.icms.tipoIcms.modalidadeDeterminacaoBC" value="${item.tributos.icms.tipoIcms.modalidadeDeterminacaoBC}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.aliquota" name="${detItem}.tributos.icms.tipoIcms.aliquota" value="${item.tributos.icms.tipoIcms.aliquota}"/>
<%-- bloco de icms --%>

<%-- bloco de ipi --%>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.aliquota" name="${detItem}.tributos.ipi.tipoIpi.aliquota" value="${item.tributos.ipi.tipoIpi.aliquota}"/>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.codigoSituacaoTributaria" name="${detItem}.tributos.ipi.tipoIpi.codigoSituacaoTributaria" value="${item.tributos.ipi.tipoIpi.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.valorBC" name="${detItem}.tributos.ipi.tipoIpi.valorBC" value="${item.tributos.ipi.tipoIpi.valorBC}"/>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.quantidadeUnidadeTributavel" name="${detItem}.tributos.ipi.tipoIpi.quantidadeUnidadeTributavel" value="${item.tributos.ipi.tipoIpi.quantidadeUnidadeTributavel}"/>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.valorUnidadeTributavel" name="${detItem}.tributos.ipi.tipoIpi.valorUnidadeTributavel" value="${item.tributos.ipi.tipoIpi.valorUnidadeTributavel}"/>

<input type="hidden" id="${detItem}.tributos.ipi.classeEnquadramento" name="${detItem}.tributos.ipi.classeEnquadramento" value="${item.tributos.ipi.classeEnquadramento}"/>
<input type="hidden" id="${detItem}.tributos.ipi.codigoEnquadramento" name="${detItem}.tributos.ipi.codigoEnquadramento" value="${item.tributos.ipi.codigoEnquadramento}"/>
<input type="hidden" id="${detItem}.tributos.ipi.cnpjProdutor" name="${detItem}.tributos.ipi.cnpjProdutor" value="${item.tributos.ipi.cnpjProdutor}"/>
<input type="hidden" id="${detItem}.tributos.ipi.codigoSeloControle" name="${detItem}.tributos.ipi.codigoSeloControle" value="${item.tributos.ipi.codigoSeloControle}"/>
<input type="hidden" id="${detItem}.tributos.ipi.quantidadeSeloControle" name="${detItem}.tributos.ipi.quantidadeSeloControle" value="${item.tributos.ipi.quantidadeSeloControle}"/>
<%-- bloco de ipi --%>

<%-- bloco de cofins --%>
<input type="hidden" id="${detItem}.tributos.cofins.tipoCofins.aliquota" name="${detItem}.tributos.cofins.tipoCofins.aliquota" value="${item.tributos.cofins.tipoCofins.aliquota}"/>
<input type="hidden" id="${detItem}.tributos.cofins.tipoCofins.codigoSituacaoTributaria" name="${detItem}.tributos.cofins.tipoCofins.codigoSituacaoTributaria" value="${item.tributos.cofins.tipoCofins.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.cofins.tipoCofins.quantidadeVendida" name="${detItem}.tributos.cofins.tipoCofins.quantidadeVendida" value="${item.tributos.cofins.tipoCofins.quantidadeVendida}"/>
<input type="hidden" id="${detItem}.tributos.cofins.tipoCofins.valorBC" name="${detItem}.tributos.cofins.tipoCofins.valorBC" value="${item.tributos.cofins.tipoCofins.valorBC}"/>
<%-- bloco de cofins --%>

<%-- bloco de iss --%>
<input type="hidden" id="${detItem}.tributos.issqn.aliquota" name="${detItem}.tributos.issqn.aliquota" value="${item.tributos.issqn.aliquota}"/>
<input type="hidden" id="${detItem}.tributos.issqn.codigoSituacaoTributaria" name="${detItem}.tributos.issqn.codigoSituacaoTributaria" value="${item.tributos.issqn.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.issqn.valorBC" name="${detItem}.tributos.issqn.valorBC" value="${item.tributos.issqn.valorBC}"/>
<input type="hidden" id="${detItem}.tributos.issqn.codigoMunicipioGerador" name="${detItem}.tributos.issqn.codigoMunicipioGerador" value="${item.tributos.issqn.codigoMunicipioGerador}"/>
<input type="hidden" id="${detItem}.tributos.issqn.itemListaServicos" name="${detItem}.tributos.issqn.itemListaServicos" value="${item.tributos.issqn.itemListaServicos}"/>
<%-- bloco de iss --%>

<%-- bloco de ii --%>
<input type="hidden" id="${detItem}.tributos.impostoImportacao.valor" name="${detItem}.tributos.impostoImportacao.valor" value="${item.tributos.impostoImportacao.valor}"/>
<input type="hidden" id="${detItem}.tributos.impostoImportacao.valorBC" name="${detItem}.tributos.impostoImportacao.valorBC" value="${item.tributos.impostoImportacao.valorBC}"/>
<input type="hidden" id="${detItem}.tributos.impostoImportacao.valorDespesaAduaneira" name="${detItem}.tributos.impostoImportacao.valorDespesaAduaneira" value="${item.tributos.impostoImportacao.valorDespesaAduaneira}"/>
<input type="hidden" id="${detItem}.tributos.impostoImportacao.valorIOF" name="${detItem}.tributos.impostoImportacao.valorIOF" value="${item.tributos.impostoImportacao.valorIOF}"/>
<%-- bloco de ii --%>

<%-- bloco de pis --%>
<input type="hidden" id="${detItem}.tributos.pis.tipoPis.aliquota" name="${detItem}.tributos.pis.tipoPis.aliquota" value="${item.tributos.pis.tipoPis.aliquota}"/>
<input type="hidden" id="${detItem}.tributos.pis.tipoPis.codigoSituacaoTributaria" name="${detItem}.tributos.pis.tipoPis.codigoSituacaoTributaria" value="${item.tributos.pis.tipoPis.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.pis.tipoPis.quantidadeVendida" name="${detItem}.tributos.pis.tipoPis.quantidadeVendida" value="${item.tributos.pis.tipoPis.quantidadeVendida}"/>
<input type="hidden" id="${detItem}.tributos.pis.tipoPis.valorBC" name="${detItem}.tributos.pis.tipoPis.valorBC" value="${item.tributos.pis.tipoPis.valorBC}"/>
<%-- bloco de pis --%>

<%-- bloco de informacoes --%>
<input type="hidden" id="${detItem}.produtoServicoNFe.outrasDespesasAcessorias" name="${detItem}.produtoServicoNFe.outrasDespesasAcessorias" value="${item.produto.outrasDespesasAcessorias}"/>
<input type="hidden" id="${detItem}.informacoesAdicionais" name="${detItem}.informacoesAdicionais" value="${item.informacoesAdicionais}"/>
<input type="hidden" id="${detItem}.numeroPedidoCompra" name="${detItem}.numeroPedidoCompra" value="${item.numeroPedidoCompra}"/>
<input type="hidden" id="${detItem}.itemPedidoCompra" name="${detItem}.itemPedidoCompra" value="${item.itemPedidoCompra}"/>
<%-- bloco de informacoes --%>

<%-- bloco de importacoes --%>
<c:set var="listaImportacao" value="${detItem}.listaImportacao"/>
<c:forEach var="imp" items="${item.listaImportacao}" varStatus="loop">
<input type="hidden" id="${listaImportacao}[${loop.index}].cnpjEncomendante" name="${listaImportacao}[${loop.index}].cnpjEncomendante" value="${imp.cnpjEncomendante}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].codigoExportador" name="${listaImportacao}[${loop.index}].codigoExportador" value="${imp.codigoExportador}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].dataImportacao" name="${listaImportacao}[${loop.index}].dataImportacao" value="${imp.dataImportacao}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].dataDesembaraco" name="${listaImportacao}[${loop.index}].dataDesembaraco" value="${imp.dataDesembaraco}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].localDesembaraco" name="${listaImportacao}[${loop.index}].localDesembaraco" value="${imp.localDesembaraco}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].numero" name="${listaImportacao}[${loop.index}].numero" value="${imp.numero}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].tipoIntermediacao" name="${listaImportacao}[${loop.index}].tipoIntermediacao" value="${imp.tipoIntermediacao}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].tipoTransporteInternacional" name="${listaImportacao}[${loop.index}].tipoTransporteInternacional" value="${imp.tipoTransporteInternacional}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].ufDesembaraco" name="${listaImportacao}[${loop.index}].ufDesembaraco" value="${imp.ufDesembaraco}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].ufEncomendante" name="${listaImportacao}[${loop.index}].ufEncomendante" value="${imp.ufEncomendante}"/>
<input type="hidden" id="${listaImportacao}[${loop.index}].valorAFRMM" name="${listaImportacao}[${loop.index}].valorAFRMM" value="${imp.valorAFRMM}"/>

<%-- bloco de adicoes --%>
<c:set var="listaAdicao" value="${listaImportacao}[${loop.index}].listaAdicao"/>
<c:forEach var="ad" items="${imp.listaAdicao}" varStatus="adLoop">
<input type="hidden" id="${listaAdicao}[${adLoop.index}].codigoFabricante" name="${listaAdicao}[${adLoop.index}].codigoFabricante" value="${ad.codigoFabricante}"/>
<input type="hidden" id="${listaAdicao}[${adLoop.index}].numero" name="${listaAdicao}[${adLoop.index}].numero" value="${ad.numero}"/>
<input type="hidden" id="${listaAdicao}[${adLoop.index}].numeroDrawback" name="${listaAdicao}[${adLoop.index}].numeroDrawback" value="${ad.numeroDrawback}"/>
<input type="hidden" id="${listaAdicao}[${adLoop.index}].numeroSequencialItem" name="${listaAdicao}[${adLoop.index}].numeroSequencialItem" value="${ad.numeroSequencialItem}"/>
<input type="hidden" id="${listaAdicao}[${adLoop.index}].valorDesconto" name="${listaAdicao}[${adLoop.index}].valorDesconto" value="${ad.valorDesconto}"/>
</c:forEach>
<%-- bloco de adicoes --%>

</c:forEach>
<%-- bloco de importacoes --%>

</c:forEach>