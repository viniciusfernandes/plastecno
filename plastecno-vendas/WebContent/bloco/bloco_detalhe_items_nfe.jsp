<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:forEach var="item" items="${nf.listaItem}" >
<%-- bloco de icms --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].produtoServicoNFe.ncm" name="nf.listaItem[${item.indiceItem}].produtoServicoNFe.ncm" value="${item.produto.ncm}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].produtoServicoNFe.cfop" name="nf.listaItem[${item.indiceItem}].produtoServicoNFe.cfop" value="${item.produto.cfop}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.codigoSituacaoTributaria" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.codigoSituacaoTributaria" value="${item.tributos.icms.tipoIcms.codigoSituacaoTributaria}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.origemMercadoria" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.origemMercadoria" value="${item.tributos.icms.tipoIcms.origemMercadoria}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.motivoDesoneracao" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.motivoDesoneracao" value="${item.tributos.icms.tipoIcms.motivoDesoneracao}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.aliquotaST" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.aliquotaST" value="${item.tributos.icms.tipoIcms.aliquotaST}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.valorBCST" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.valorBCST" value="${item.tributos.icms.tipoIcms.valorBCST}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.valorBC" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.valorBC" value="${item.tributos.icms.tipoIcms.valorBC}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.percentualReducaoBC" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.percentualReducaoBC" value="${item.tributos.icms.tipoIcms.percentualReducaoBC}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST" value="${item.tributos.icms.tipoIcms.percentualMargemValorAdicionadoICMSST}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.modalidadeDeterminacaoBCST" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.modalidadeDeterminacaoBCST" value="${item.tributos.icms.tipoIcms.modalidadeDeterminacaoBCST}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.modalidadeDeterminacaoBC" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.modalidadeDeterminacaoBC" value="${item.tributos.icms.tipoIcms.modalidadeDeterminacaoBC}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.aliquota" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.aliquota" value="${item.tributos.icms.tipoIcms.aliquota}"/>
<%-- bloco de icms --%>

<%-- bloco de ipi --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.aliquota" name="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.aliquota" value="${item.tributos.ipi.tipoIpi.aliquota}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.codigoSituacaoTributaria" name="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.codigoSituacaoTributaria" value="${item.tributos.ipi.tipoIpi.codigoSituacaoTributaria}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.valorBC" name="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.valorBC" value="${item.tributos.ipi.tipoIpi.valorBC}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.quantidadeUnidadeTributavel" name="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.quantidadeUnidadeTributavel" value="${item.tributos.ipi.tipoIpi.quantidadeUnidadeTributavel}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.valorUnidadeTributavel" name="nf.listaItem[${item.indiceItem}].tributos.ipi.tipoIpi.valorUnidadeTributavel" value="${item.tributos.ipi.tipoIpi.valorUnidadeTributavel}"/>

<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.classeEnquadramento" name="nf.listaItem[${item.indiceItem}].tributos.ipi.classeEnquadramento" value="${item.tributos.ipi.classeEnquadramento}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.codigoEnquadramento" name="nf.listaItem[${item.indiceItem}].tributos.ipi.codigoEnquadramento" value="${item.tributos.ipi.codigoEnquadramento}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.cnpjProdutor" name="nf.listaItem[${item.indiceItem}].tributos.ipi.cnpjProdutor" value="${item.tributos.ipi.cnpjProdutor}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.codigoSeloControle" name="nf.listaItem[${item.indiceItem}].tributos.ipi.codigoSeloControle" value="${item.tributos.ipi.codigoSeloControle}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.ipi.quantidadeSeloControle" name="nf.listaItem[${item.indiceItem}].tributos.ipi.quantidadeSeloControle" value="${item.tributos.ipi.quantidadeSeloControle}"/>
<%-- bloco de ipi --%>

<%-- bloco de cofins --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.cofins.tipoCofins.aliquota" name="nf.listaItem[${item.indiceItem}].tributos.cofins.tipoCofins.aliquota" value="${item.tributos.cofins.tipoCofins.aliquota}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.cofins.tipoCofins.codigoSituacaoTributaria" name="nf.listaItem[${item.indiceItem}].tributos.cofins.tipoCofins.codigoSituacaoTributaria" value="${item.tributos.cofins.tipoCofins.codigoSituacaoTributaria}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.cofins.tipoCofins.quantidadeVendida" name="nf.listaItem[${item.indiceItem}].tributos.cofins.tipoCofins.quantidadeVendida" value="${item.tributos.cofins.tipoCofins.quantidadeVendida}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.cofins.tipoCofins.valorBC" name="nf.listaItem[${item.indiceItem}].tributos.cofins.tipoCofins.valorBC" value="${item.tributos.cofins.tipoCofins.valorBC}"/>
<%-- bloco de cofins --%>

<%-- bloco de iss --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.issqn.aliquota" name="nf.listaItem[${item.indiceItem}].tributos.issqn.aliquota" value="${item.tributos.issqn.aliquota}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.issqn.codigoSituacaoTributaria" name="nf.listaItem[${item.indiceItem}].tributos.issqn.codigoSituacaoTributaria" value="${item.tributos.issqn.codigoSituacaoTributaria}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.issqn.valorBC" name="nf.listaItem[${item.indiceItem}].tributos.issqn.valorBC" value="${item.tributos.issqn.valorBC}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.issqn.codigoMunicipioGerador" name="nf.listaItem[${item.indiceItem}].tributos.issqn.codigoMunicipioGerador" value="${item.tributos.issqn.codigoMunicipioGerador}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.issqn.itemListaServicos" name="nf.listaItem[${item.indiceItem}].tributos.issqn.itemListaServicos" value="${item.tributos.issqn.itemListaServicos}"/>
<%-- bloco de iss --%>

<%-- bloco de ii --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.impostoImportacao.valor" name="nf.listaItem[${item.indiceItem}].tributos.impostoImportacao.valor" value="${item.tributos.impostoImportacao.valor}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.impostoImportacao.valorBC" name="nf.listaItem[${item.indiceItem}].tributos.impostoImportacao.valorBC" value="${item.tributos.impostoImportacao.valorBC}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.impostoImportacao.valorDespesaAduaneira" name="nf.listaItem[${item.indiceItem}].tributos.impostoImportacao.valorDespesaAduaneira" value="${item.tributos.impostoImportacao.valorDespesaAduaneira}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.impostoImportacao.valorIOF" name="nf.listaItem[${item.indiceItem}].tributos.impostoImportacao.valorIOF" value="${item.tributos.impostoImportacao.valorIOF}"/>
<%-- bloco de ii --%>

<%-- bloco de pis --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.pis.tipoPis.aliquota" name="nf.listaItem[${item.indiceItem}].tributos.pis.tipoPis.aliquota" value="${item.tributos.pis.tipoPis.aliquota}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.pis.tipoPis.codigoSituacaoTributaria" name="nf.listaItem[${item.indiceItem}].tributos.pis.tipoPis.codigoSituacaoTributaria" value="${item.tributos.pis.tipoPis.codigoSituacaoTributaria}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.pis.tipoPis.quantidadeVendida" name="nf.listaItem[${item.indiceItem}].tributos.pis.tipoPis.quantidadeVendida" value="${item.tributos.pis.tipoPis.quantidadeVendida}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.pis.tipoPis.valorBC" name="nf.listaItem[${item.indiceItem}].tributos.pis.tipoPis.valorBC" value="${item.tributos.pis.tipoPis.valorBC}"/>
<%-- bloco de pis --%>

<%-- bloco de informacoes --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].produtoServicoNFe.outrasDespesasAcessorias" name="nf.listaItem[${item.indiceItem}].produtoServicoNFe.outrasDespesasAcessorias" value="${item.produto.outrasDespesasAcessorias}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].informacoesAdicionais" name="nf.listaItem[${item.indiceItem}].informacoesAdicionais" value="${item.informacoesAdicionais}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].numeroPedidoCompra" name="nf.listaItem[${item.indiceItem}].numeroPedidoCompra" value="${item.numeroPedidoCompra}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].itemPedidoCompra" name="nf.listaItem[${item.indiceItem}].itemPedidoCompra" value="${item.itemPedidoCompra}"/>
<%-- bloco de informacoes --%>

<c:forEach var="imp" items="${item.listaImportacao}" varStatus="index">
<%-- bloco de importacoes --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].cnpjEncomendante" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].cnpjEncomendante" value="${imp.cnpjEncomendante}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].codigoExportador" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].codigoExportador" value="${imp.codigoExportador}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].dataImportacao" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].dataImportacao" value="${imp.dataImportacao}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].dataDesembaraco" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].dataDesembaraco" value="${imp.dataDesembaraco}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].localDesembaraco" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].localDesembaraco" value="${imp.localDesembaraco}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].numero" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].numero" value="${imp.numero}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].tipoIntermediacao" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].tipoIntermediacao" value="${imp.tipoIntermediacao}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].tipoTransporteInternacional" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].tipoTransporteInternacional" value="${imp.tipoTransporteInternacional}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].ufDesembaraco" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].ufDesembaraco" value="${imp.ufDesembaraco}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].ufEncomendante" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].ufEncomendante" value="${imp.ufEncomendante}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].valorAFRMM" name="nf.listaItem[${item.indiceItem}].listaImportacao[${index.count}].valorAFRMM" value="${imp.valorAFRMM}"/>
<%-- bloco de importacoes --%>

</c:forEach>

</c:forEach>