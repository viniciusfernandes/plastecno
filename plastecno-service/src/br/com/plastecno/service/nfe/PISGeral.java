package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(campoCondicional = "codigoSituacaoTributaria", nomeExibicaoCampoCondicional = "Códido da situação tributária")
public class PISGeral {
	@XmlElement(name = "pPIS")
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, tiposNaoPermitidos = { "04", "05", "06", "07", "08",
			"09" }, nomeExibicao = "Alíquota do PIS")
	private Double aliquota;

	@XmlElement(name = "CST")
	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{2}", padraoExemplo = "2 dígitos", nomeExibicao = "Códido da situação tributária do PIS")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qBCProd")
	@InformacaoValidavel(decimal = { 12, 4 }, tiposObrigatorios = { "03", "49", "50", "51", "52", "53", "54", "55",
			"56", "60", "61", "62", "63", "64", "65", "66", "67", "70", "71", "72", "73", "74", "75", "98", "99","ST" }, tiposNaoPermitidos = {
			"04", "05", "06", "07", "08", "09" }, nomeExibicao = "Quantidade vendida do PIS")
	private Integer quantidadeVendida;

	@XmlElement(name = "vPIS")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, tiposNaoPermitidos = { "04", "05", "06", "07", "08",
			"09" }, nomeExibicao = "Valor do PIS")
	private Double valor;

	@XmlElement(name = "vAliqProd")
	@InformacaoValidavel(decimal = { 11, 4 }, tiposObrigatorios = { "03", "49", "50", "51", "52", "53", "54", "55",
			"56", "60", "61", "62", "63", "64", "65", "66", "67", "70", "71", "72", "73", "74", "75", "98", "99","ST" }, tiposNaoPermitidos = {
			"04", "05", "06", "07", "08", "09" }, nomeExibicao = "Valor da alíquota do PIS")
	private Double valorAliquota;

	@XmlElement(name = "vBC")
	@InformacaoValidavel(decimal = { 13, 2 }, tiposObrigatorios = { "01", "02", "49", "50", "51", "52", "53", "54",
			"55", "56", "60", "61", "62", "63", "64", "65", "66", "67", "70", "71", "72", "73", "74", "75", "98", "99",
			"ST" }, tiposNaoPermitidos = { "04", "05", "06", "07", "08", "09" }, nomeExibicao = "Valor de base de cáculo do PIS")
	private Double valorBC;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC * (aliquota / 100d) : 0;
	}

	public PISGeral carregarValores() {
		valor = calcularValor();
		valorAliquota = valor;
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