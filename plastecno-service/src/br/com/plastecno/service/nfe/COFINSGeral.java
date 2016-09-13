package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(campoCondicional = "codigoSituacaoTributaria", nomeExibicaoCampoCondicional = "Código de situação tributária")
public class COFINSGeral {
	@XmlElement(name = "pCOFINS")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Alíquota COFINS", tiposNaoPermitidos = { "4", "6", "7",
			"8", "9" })
	private Double aliquota;

	@XmlElement(name = "CST")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Código da situação tributária do COFINS")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qBCProd")
	@InformacaoValidavel(tiposObrigatorios = { "3", "9", "ST" }, nomeExibicao = "Quantidade vendida do COFINS", tiposNaoPermitidos = {
			"4", "6", "7", "8", "9" })
	private Integer quantidadeVendida;

	@XmlElement(name = "vCOFINS")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Valor COFINS", tiposNaoPermitidos = { "4", "6", "7", "8",
			"9" })
	private Double valor;

	@XmlElement(name = "vAliqProd")
	@InformacaoValidavel(tiposObrigatorios = { "3", "9", "ST" }, nomeExibicao = "Valor da alíquota do COFINS")
	private Double valorAliquota;

	@XmlElement(name = "vBC")
	@InformacaoValidavel(tiposObrigatorios = { "1", "2", "99", "ST" }, nomeExibicao = "Valor da base de cáculo do COFINS", tiposNaoPermitidos = {
			"4", "6", "7", "8", "9" })
	private Double valorBC;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC * (aliquota / 100d) : 0;
	}

	public COFINSGeral carregarValoresAliquotas() {
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