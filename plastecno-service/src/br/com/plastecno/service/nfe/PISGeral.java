package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(campoCondicional = "codigoSituacaoTributaria", nomeExibicaoCampoCondicional = "Códido da situação tributária")
public class PISGeral {
	@XmlElement(name = "pPIS")
	@InformacaoValidavel(obrigatorio = true, tiposNaoPermitidos = { "4", "6", "7", "8", "9" }, nomeExibicao = "Alíquota do PIS")
	private Double aliquota;

	@XmlElement(name = "CST")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Códido da situação tributária do PIS")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qBCProd")
	@InformacaoValidavel(tiposObrigatorios = { "3", "99" }, tiposNaoPermitidos = { "4", "6", "7", "8", "9" }, nomeExibicao = "Quantidade vendida do PIS")
	private Integer quantidadeVendida;

	@XmlElement(name = "vPIS")
	@InformacaoValidavel(obrigatorio = true, tiposNaoPermitidos = { "4", "6", "7", "8", "9" }, nomeExibicao = "Valor do PIS")
	private Double valor;

	@XmlElement(name = "vAliqProd")
	@InformacaoValidavel(tiposObrigatorios = { "99" }, tiposNaoPermitidos = { "4", "6", "7", "8", "9" }, nomeExibicao = "Valor da alíquota do PIS")
	private Double valorAliquota;

	@XmlElement(name = "vBC")
	@InformacaoValidavel(tiposObrigatorios = { "1", "2", "99", "ST" }, tiposNaoPermitidos = { "4", "6", "7", "8", "9" }, nomeExibicao = "Valor de base de cáculo do PIS")
	private Double valorBC;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC * (aliquota / 100d) : 0;
	}

	public PISGeral carregarValoresAliquotas() {
		valor = calcularValor();
		return this;
	}

	@XmlTransient
	public Double getAliquota() {
		return aliquota;
	}

	@XmlTransient
	public String getCodigoSituacaoTributaria() {
		return codigoSituacaoTributaria;
	}

	@XmlTransient
	public Integer getQuantidadeVendida() {
		return quantidadeVendida;
	}

	@XmlTransient
	public TipoTributacaoPIS getTipoTributacao() {
		return TipoTributacaoPIS.getTipoTributacao(codigoSituacaoTributaria);
	}

	@XmlTransient
	public Double getValor() {
		return valor == null ? 0 : valor;
	}

	@XmlTransient
	public Double getValorAliquota() {
		return valorAliquota;
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