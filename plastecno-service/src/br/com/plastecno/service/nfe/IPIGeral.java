package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;

public class IPIGeral {
	@XmlElement(name = "pIPI")
	private Double aliquota;

	@XmlElement(name = "CST")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qUnid")
	private Integer quantidadeUnidadeTributavel;

	@XmlElement(name = "vIPI")
	private Double valor;

	@XmlElement(name = "vBC")
	private Double valorBC;

	@XmlElement(name = "vUnid")
	private Double valorUnidadeTributavel;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC
				* (aliquota / 100d) : 0;
	}

	public IPIGeral carregarValoresAliquotas() {
		valor = calcularValor();
		return this;
	}

	@XmlTransient
	public TipoTributacaoIPI getTipoTributacao() {
		return TipoTributacaoIPI.getTipoTributacao(codigoSituacaoTributaria);
	}

	@XmlTransient
	public Double getValor() {
		return valor == null ? 0 : valor;
	}

	@XmlTransient
	public Double getValorBC() {
		return valorBC == null ? 0 : valorBC;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoSituacaoTributaria(String codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setQuantidadeUnidadeTributavel(
			Integer quantidadeUnidadeTributavel) {
		this.quantidadeUnidadeTributavel = quantidadeUnidadeTributavel;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorUnidadeTributavel(Double valorUnidadeTributavel) {
		this.valorUnidadeTributavel = valorUnidadeTributavel;
	}

}
