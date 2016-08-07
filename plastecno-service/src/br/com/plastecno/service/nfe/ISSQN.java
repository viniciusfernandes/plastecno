package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ISSQN {
	@XmlElement(name = "vAliq")
	private Double aliquota;

	@XmlElement(name = "cMunFG")
	private Long codigoMunicipioGerador;

	@XmlElement(name = "cSitTrib")
	private Integer codigoSituacaoTributaria;

	@XmlElement(name = "cListServ")
	private Integer itemListaServicos;

	@XmlElement(name = "vISSQN")
	private Double valor;

	@XmlElement(name = "vBC")
	private Double valorBC;

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoMunicipioGerador(Long codigoMunicipioGerador) {
		this.codigoMunicipioGerador = codigoMunicipioGerador;
	}

	public void setCodigoSituacaoTributaria(Integer codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setItemListaServicos(Integer itemListaServicos) {
		this.itemListaServicos = itemListaServicos;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

}
