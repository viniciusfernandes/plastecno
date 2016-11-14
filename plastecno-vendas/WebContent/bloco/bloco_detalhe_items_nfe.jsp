<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:forEach var="item" items="${nf.listaItem}" >
<c:set var="detItem" value="nf.listaItem[${item.indiceItem}]"/>
<%-- bloco de icms --%>
<input type="hidden" id="${detItem}.produtoServicoNFe.ncm" name="${detItem}.produtoServicoNFe.ncm" value="${item.produto.ncm}"/>
<input type="hidden" id="${detItem}.produtoServicoNFe.cest" name="${detItem}.produtoServicoNFe.cest" value="${item.produto.cest}"/>
<input type="hidden" id="${detItem}.produtoServicoNFe.cfop" name="${detItem}.produtoServicoNFe.cfop" value="${item.produto.cfop}"/>
<input type="hidden" id="${detItem}.tributos.valorTotalTributos" name="${detItem}.tributos.valorTotalTributos" value="${item.tributos.valorTotalTributos}"/>

<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.codigoSituacaoTributaria" name="${detItem}.tributos.icms.tipoIcms.codigoSituacaoTributaria" value="${item.tributos.icms.tipoIcms.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.origemMercadoria" name="${detItem}.tributos.icms.tipoIcms.origemMercadoria" value="${item.tributos.icms.tipoIcms.origemMercadoria}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.valorDesonerado" name="${detItem}.tributos.icms.tipoIcms.valorDesonerado" value="${item.tributos.icms.tipoIcms.valorDesonerado}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.motivoDesoneracao" name="${detItem}.tributos.icms.tipoIcms.motivoDesoneracao" value="${item.tributos.icms.tipoIcms.motivoDesoneracao}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.aliquotaST" name="${detItem}.tributos.icms.tipoIcms.aliquotaST" value="${item.tributos.icms.tipoIcms.aliquotaST}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.valorBCST" name="${detItem}.tributos.icms.tipoIcms.valorBCST" value="${item.tributos.icms.tipoIcms.valorBCST}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.valorBC" name="${detItem}.tributos.icms.tipoIcms.valorBC" value="${item.tributos.icms.tipoIcms.valorBC}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.percentualReducaoBC" name="${detItem}.tributos.icms.tipoIcms.percentualReducaoBC" value="${item.tributos.icms.tipoIcms.percentualReducaoBC}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST" name="${detItem}.tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST" value="${item.tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.modalidadeDeterminacaoBCST" name="${detItem}.tributos.icms.tipoIcms.modalidadeDeterminacaoBCST" value="${item.tributos.icms.tipoIcms.modalidadeDeterminacaoBCST}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.modalidadeDeterminacaoBC" name="${detItem}.tributos.icms.tipoIcms.modalidadeDeterminacaoBC" value="${item.tributos.icms.tipoIcms.modalidadeDeterminacaoBC}"/>
<input type="hidden" id="${detItem}.tributos.icms.tipoIcms.aliquota" name="${detItem}.tributos.icms.tipoIcms.aliquota" value="${item.tributos.icms.tipoIcms.aliquota}"/>

<%-- bloco de icms interestadual --%>
<input type="hidden" id="${detItem}.tributos.icms.icmsInterestadual.aliquotaInterestadual" name="${detItem}.tributos.icms.icmsInterestadual.aliquotaInterestadual" value="${item.tributos.icms.icmsInterestadual.aliquotaInterestadual}"/>
<input type="hidden" id="${detItem}.tributos.icms.icmsInterestadual.aliquotaUFDestino" name="${detItem}.tributos.icms.icmsInterestadual.aliquotaUFDestino" value="${item.tributos.icms.icmsInterestadual.aliquotaUFDestino}"/>
<input type="hidden" id="${detItem}.tributos.icms.icmsInterestadual.percentualFCPDestino" name="${detItem}.tributos.icms.icmsInterestadual.percentualFCPDestino" value="${item.tributos.icms.icmsInterestadual.percentualFCPDestino}"/>
<input type="hidden" id="${detItem}.tributos.icms.icmsInterestadual.percentualProvisorioPartilha" name="${detItem}.tributos.icms.icmsInterestadual.percentualProvisorioPartilha" value="${item.tributos.icms.icmsInterestadual.percentualProvisorioPartilha}"/>
<input type="hidden" id="${detItem}.tributos.icms.icmsInterestadual.valorBCUFDestino" name="${detItem}.tributos.icms.icmsInterestadual.valorBCUFDestino" value="${item.tributos.icms.icmsInterestadual.valorBCUFDestino}"/>

<%-- bloco de ipi --%>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.aliquota" name="${detItem}.tributos.ipi.tipoIpi.aliquota" value="${item.tributos.ipi.tipoIpi.aliquota}"/>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.codigoSituacaoTributaria" name="${detItem}.tributos.ipi.tipoIpi.codigoSituacaoTributaria" value="${item.tributos.ipi.tipoIpi.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.valorBC" name="${detItem}.tributos.ipi.tipoIpi.valorBC" value="${item.tributos.ipi.tipoIpi.valorBC}"/>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.quantidadeUnidadeTributavel" name="${detItem}.tributos.ipi.tipoIpi.quantidadeUnidadeTributavel" value="${item.tributos.ipi.tipoIpi.quantidadeUnidadeTributavel}"/>
<input type="hidden" id="${detItem}.tributos.ipi.tipoIpi.valorUnidadeTributavel" name="${detItem}.tributos.ipi.tipoIpi.valorUnidadeTributavel" value="${item.tributos.ipi.tipoIpi.valorUnidadeTributavel}"/>
<input type="hidden" id="${detItem}.tributos.ipi.classeEnquadramentoCigarrosBebidas" name="${detItem}.tributos.ipi.classeEnquadramentoCigarrosBebidas" value="${item.tributos.ipi.classeEnquadramentoCigarrosBebidas}"/>
<input type="hidden" id="${detItem}.tributos.ipi.codigoEnquadramento" name="${detItem}.tributos.ipi.codigoEnquadramento" value="${item.tributos.ipi.codigoEnquadramento}"/>
<input type="hidden" id="${detItem}.tributos.ipi.cnpjProdutor" name="${detItem}.tributos.ipi.cnpjProdutor" value="${item.tributos.ipi.cnpjProdutor}"/>
<input type="hidden" id="${detItem}.tributos.ipi.codigoSeloControle" name="${detItem}.tributos.ipi.codigoSeloControle" value="${item.tributos.ipi.codigoSeloControle}"/>
<input type="hidden" id="${detItem}.tributos.ipi.quantidadeSeloControle" name="${detItem}.tributos.ipi.quantidadeSeloControle" value="${item.tributos.ipi.quantidadeSeloControle}"/>

<%-- bloco de cofins --%>
<input type="hidden" id="${detItem}.tributos.cofins.tipoCofins.aliquota" name="${detItem}.tributos.cofins.tipoCofins.aliquota" value="${item.tributos.cofins.tipoCofins.aliquota}"/>
<input type="hidden" id="${detItem}.tributos.cofins.tipoCofins.codigoSituacaoTributaria" name="${detItem}.tributos.cofins.tipoCofins.codigoSituacaoTributaria" value="${item.tributos.cofins.tipoCofins.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.cofins.tipoCofins.quantidadeVendida" name="${detItem}.tributos.cofins.tipoCofins.quantidadeVendida" value="${item.tributos.cofins.tipoCofins.quantidadeVendida}"/>
<input type="hidden" id="${detItem}.tributos.cofins.tipoCofins.valorBC" name="${detItem}.tributos.cofins.tipoCofins.valorBC" value="${item.tributos.cofins.tipoCofins.valorBC}"/>

<%-- bloco de iss --%>
<input type="hidden" id="${detItem}.tributos.issqn.aliquota" name="${detItem}.tributos.issqn.aliquota" value="${item.tributos.issqn.aliquota}"/>
<input type="hidden" id="${detItem}.tributos.issqn.codigoSituacaoTributaria" name="${detItem}.tributos.issqn.codigoSituacaoTributaria" value="${item.tributos.issqn.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.issqn.valorBC" name="${detItem}.tributos.issqn.valorBC" value="${item.tributos.issqn.valorBC}"/>
<input type="hidden" id="${detItem}.tributos.issqn.codigoMunicipioGerador" name="${detItem}.tributos.issqn.codigoMunicipioGerador" value="${item.tributos.issqn.codigoMunicipioGerador}"/>
<input type="hidden" id="${detItem}.tributos.issqn.itemListaServicos" name="${detItem}.tributos.issqn.itemListaServicos" value="${item.tributos.issqn.itemListaServicos}"/>

<%-- bloco de ii --%>
<input type="hidden" id="${detItem}.tributos.impostoImportacao.valor" name="${detItem}.tributos.impostoImportacao.valor" value="${item.tributos.impostoImportacao.valor}"/>
<input type="hidden" id="${detItem}.tributos.impostoImportacao.valorBC" name="${detItem}.tributos.impostoImportacao.valorBC" value="${item.tributos.impostoImportacao.valorBC}"/>
<input type="hidden" id="${detItem}.tributos.impostoImportacao.valorDespesaAduaneira" name="${detItem}.tributos.impostoImportacao.valorDespesaAduaneira" value="${item.tributos.impostoImportacao.valorDespesaAduaneira}"/>
<input type="hidden" id="${detItem}.tributos.impostoImportacao.valorIOF" name="${detItem}.tributos.impostoImportacao.valorIOF" value="${item.tributos.impostoImportacao.valorIOF}"/>

<%-- bloco de pis --%>
<input type="hidden" id="${detItem}.tributos.pis.tipoPis.aliquota" name="${detItem}.tributos.pis.tipoPis.aliquota" value="${item.tributos.pis.tipoPis.aliquota}"/>
<input type="hidden" id="${detItem}.tributos.pis.tipoPis.codigoSituacaoTributaria" name="${detItem}.tributos.pis.tipoPis.codigoSituacaoTributaria" value="${item.tributos.pis.tipoPis.codigoSituacaoTributaria}"/>
<input type="hidden" id="${detItem}.tributos.pis.tipoPis.quantidadeVendida" name="${detItem}.tributos.pis.tipoPis.quantidadeVendida" value="${item.tributos.pis.tipoPis.quantidadeVendida}"/>
<input type="hidden" id="${detItem}.tributos.pis.tipoPis.valorBC" name="${detItem}.tributos.pis.tipoPis.valorBC" value="${item.tributos.pis.tipoPis.valorBC}"/>

<%-- bloco de informacoes --%>
<input type="hidden" id="${detItem}.produtoServicoNFe.outrasDespesasAcessorias" name="${detItem}.produtoServicoNFe.outrasDespesasAcessorias" value="${item.produto.outrasDespesasAcessorias}"/>
<input type="hidden" id="${detItem}.produtoServicoNFe.valorTotalFrete" name="${detItem}.produtoServicoNFe.valorTotalFrete" value="${item.produto.valorTotalFrete}"/>
<input type="hidden" id="${detItem}.informacoesAdicionais" name="${detItem}.informacoesAdicionais" value="${item.informacoesAdicionais}"/>
<input type="hidden" id="${detItem}.produtoServicoNFe.fichaConteudoImportacao" name="${detItem}.produtoServicoNFe.fichaConteudoImportacao" value="${item.produto.fichaConteudoImportacao}"/>
<input type="hidden" id="${detItem}.produtoServicoNFe.numeroPedidoCompra" name="${detItem}.produtoServicoNFe.numeroPedidoCompra" value="${item.produto.numeroPedidoCompra}"/>
<input type="hidden" id="${detItem}.produtoServicoNFe.itemPedidoCompra" name="${detItem}.produtoServicoNFe.itemPedidoCompra" value="${item.produto.itemPedidoCompra}"/>

<%-- bloco de importacoes --%>
<c:set var="itemImportacao" value="${detItem}.produtoServicoNFe.listaImportacao"/>
<c:forEach var="imp" items="${item.produto.listaImportacao}" varStatus="loop">
<input type="hidden" id="${itemImportacao}[${loop.index}].cnpjEncomendante" name="${itemImportacao}[${loop.index}].cnpjEncomendante" value="${imp.cnpjEncomendante}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].codigoExportador" name="${itemImportacao}[${loop.index}].codigoExportador" value="${imp.codigoExportador}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].dataImportacao" name="${itemImportacao}[${loop.index}].dataImportacao" value="${imp.dataImportacao}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].dataDesembaraco" name="${itemImportacao}[${loop.index}].dataDesembaraco" value="${imp.dataDesembaraco}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].localDesembaraco" name="${itemImportacao}[${loop.index}].localDesembaraco" value="${imp.localDesembaraco}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].numero" name="${itemImportacao}[${loop.index}].numero" value="${imp.numero}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].tipoIntermediacao" name="${itemImportacao}[${loop.index}].tipoIntermediacao" value="${imp.tipoIntermediacao}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].tipoTransporteInternacional" name="${itemImportacao}[${loop.index}].tipoTransporteInternacional" value="${imp.tipoTransporteInternacional}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].ufDesembaraco" name="${itemImportacao}[${loop.index}].ufDesembaraco" value="${imp.ufDesembaraco}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].ufEncomendante" name="${itemImportacao}[${loop.index}].ufEncomendante" value="${imp.ufEncomendante}"/>
<input type="hidden" id="${itemImportacao}[${loop.index}].valorAFRMM" name="${itemImportacao}[${loop.index}].valorAFRMM" value="${imp.valorAFRMM}"/>

<%-- bloco de adicoes --%>
<c:set var="itemAdicao" value="${itemImportacao}[${loop.index}].listaAdicao"/>
<c:forEach var="ad" items="${imp.listaAdicao}" varStatus="adLoop">
<input type="hidden" id="${itemAdicao}[${adLoop.index}].codigoFabricante" name="${itemAdicao}[${adLoop.index}].codigoFabricante" value="${ad.codigoFabricante}"/>
<input type="hidden" id="${itemAdicao}[${adLoop.index}].numero" name="${itemAdicao}[${adLoop.index}].numero" value="${ad.numero}"/>
<input type="hidden" id="${itemAdicao}[${adLoop.index}].numeroDrawback" name="${itemAdicao}[${adLoop.index}].numeroDrawback" value="${ad.numeroDrawback}"/>
<input type="hidden" id="${itemAdicao}[${adLoop.index}].numeroSequencialItem" name="${itemAdicao}[${adLoop.index}].numeroSequencialItem" value="${ad.numeroSequencialItem}"/>
<input type="hidden" id="${itemAdicao}[${adLoop.index}].valorDesconto" name="${itemAdicao}[${adLoop.index}].valorDesconto" value="${ad.valorDesconto}"/>
</c:forEach>

</c:forEach>

<%-- bloco de exportacao --%>
<c:set var="itemExportacao" value="${detItem}.produtoServicoNFe.listaExportacao"/>
<c:forEach var="exp" items="${item.produto.listaExportacao}" varStatus="expLoop">
<input type="hidden" id="${itemExportacao}[${expLoop.index}].numeroDrawback" name="${itemExportacao}[${expLoop.index}].numeroDrawback" value="${exp.numeroDrawback}"/>
<input type="hidden" id="${itemExportacao}[${expLoop.index}].expIndireta.chaveAcessoRecebida" name="${itemExportacao}[${expLoop.index}].expIndireta.chaveAcessoRecebida" value="${exp.expIndireta.chaveAcessoRecebida}"/>
<input type="hidden" id="${itemExportacao}[${expLoop.index}].expIndireta.numeroRegistro" name="${itemExportacao}[${expLoop.index}].expIndireta.numeroRegistro" value="${exp.expIndireta.numeroRegistro}"/>
<input type="hidden" id="${itemExportacao}[${expLoop.index}].expIndireta.quantidadeItem" name="${itemExportacao}[${expLoop.index}].expIndireta.quantidadeItem" value="${exp.expIndireta.quantidadeItem}"/>
</c:forEach>
</c:forEach>