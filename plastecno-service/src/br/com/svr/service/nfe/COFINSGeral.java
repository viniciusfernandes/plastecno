package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel(campoCondicional = "codigoSituacaoTributaria", nomeExibicaoCampoCondicional = "Código de situação tributária")
@XmlType(propOrder = { "codigoSituacaoTributaria", "valorBC", "aliquota", "quantidadeVendida", "valorAliquota", "valor" })
public class COFINSGeral {
	@XmlElement(name = "pCOFINS")
	@InformacaoValidavel(decimal = { 3, 4 }, tiposObrigatorios = { "01", "02", "03", "49", "50", "51", "52", "53",
			"54", "55", "56", "60", "61", "62", "63", "64", "65", "66", "67", "70", "71", "72", "73", "74", "75", "98",
			"99", "ST" }, nomeExibicao = "Alíquota COFINS", tiposNaoPermitidos = { "04", "05", "06", "07", "08", "09" })
	private Double aliquota;

	@XmlElement(name = "CST")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Código da situação tributária do COFINS")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qBCProd")
	@InformacaoValidavel(decimal = { 12, 4 }, tiposObrigatorios = { "03", "49", "50", "51", "52", "53", "54", "55",
			"56", "60", "61", "62", "63", "64", "65", "66", "67", "70", "71", "72", "73", "74", "75", "98", "99", "ST" }, nomeExibicao = "Quantidade vendida do COFINS", tiposNaoPermitidos = {
			"04", "05", "06", "07", "08", "09" })
	private Double quantidadeVendida;

	@XmlElement(name = "vCOFINS")
	@InformacaoValidavel(decimal = { 13, 2 }, tiposObrigatorios = { "01", "02", "03", "49", "50", "51", "52", "53",
			"54", "55", "56", "60", "61", "62", "63", "64", "65", "66", "67", "70", "71", "72", "73", "74", "75", "98",
			"99", "ST" }, nomeExibicao = "Valor COFINS", tiposNaoPermitidos = { "04", "05", "06", "07", "08", "09" })
	private Double valor;

	@XmlElement(name = "vAliqProd")
	@InformacaoValidavel(decimal = { 11, 4 }, tiposObrigatorios = { "03", "49", "50", "51", "52", "53", "54", "55",
			"56", "60", "61", "62", "63", "64", "65", "66", "67", "70", "71", "72", "73", "74", "75", "98", "99", "ST" }, tiposNaoPermitidos = {
			"04", "05", "06", "07", "08", "09" }, nomeExibicao = "Valor da alíquota do COFINS")
	private Double valorAliquota;

	@XmlElement(name = "vBC")
	@InformacaoValidavel(decimal = { 13, 2 }, tiposObrigatorios = { "01", "02", "49", "50", "51", "52", "53", "54",
			"55", "56", "60", "61", "62", "63", "64", "65", "66", "67", "70", "71", "72", "73", "74", "75", "98", "99",
			"ST" }, nomeExibicao = "Valor da base de cáculo do COFINS", tiposNaoPermitidos = { "03", "04", "05", "06",
			"07", "08", "09" })
	private Double valorBC;

	public Double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC * (aliquota / 100d) : null;
	}

	public COFINSGeral carregarValores() {
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
	public Double getQuantidadeVendida() {
		return quantidadeVendida;
	}

	@XmlTransient
	public TipoTributacaoCOFINS getTipoTributacao() {
		return TipoTributacaoCOFINS.getTipoTributacao(codigoSituacaoTributaria);
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

	public void setQuantidadeVendida(Double quantidadeVendida) {
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