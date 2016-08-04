package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;

public class PISGeral {
	@XmlElement(name = "pPIS")
	private Double aliquota;

	@XmlElement(name = "CST")
	private Integer codigoSituacaoTributaria;

	@XmlElement(name = "qBCProd")
	private Integer quantidadeVendida;

	@XmlElement(name = "vPIS")
	private Double valor;

	@XmlElement(name = "vAliqProd")
	private Double valorAliquota;

	@XmlElement(name = "vBC")
	private Double valorBC;

	@XmlTransient
	public TipoTributacaoPIS getTipoTributacao() {
		return TipoTributacaoPIS.getTipoTributacao(codigoSituacaoTributaria);
	}

	@XmlTransient
	public Double getValor() {
		return valor == null ? 0 : valor;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoSituacaoTributaria(Integer codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setQuantidadeVendida(Integer quantidadeVendida) {
		this.quantidadeVendida = quantidadeVendida;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorAliquota(Double valorAliquota) {
		this.valorAliquota = valorAliquota;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}
}