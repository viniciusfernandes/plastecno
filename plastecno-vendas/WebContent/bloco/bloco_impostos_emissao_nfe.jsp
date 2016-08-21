<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:forEach var="item" items="${nf.listaItem}" >
<%-- bloco de icms --%>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].produto.ncm" name="nf.listaItem[${item.indiceItem}].produto.ncm" value="${item.produto.ncm}"/>
<input type="hidden" id="nf.listaItem[${item.indiceItem}].produto.cfop" name="nf.listaItem[${item.indiceItem}].produto.cfop" value="${item.produto.cfop}"/>
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
<input type="hidden" id="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.aliquota" name="nf.listaItem[${item.indiceItem}].tributos.icms.tipoIcms.aliquota" value="${item.tributos.icms.tipoIcms.aliquota}"/>
<%-- bloco de icms --%>

</c:forEach>