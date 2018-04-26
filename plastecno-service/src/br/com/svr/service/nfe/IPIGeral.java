package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.svr.service.nfe.constante.TipoTributacaoIPI;
import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel(campoCondicional = "codigoSituacaoTributaria", nomeExibicaoCampoCondicional = "Código da situação tributária")
public class IPIGeral {
	@XmlElement(name = "pIPI")
	@InformacaoValidavel(tiposObrigatorios = { "00", "49", "50", "99" }, nomeExibicao = "Alíquota do IPI")
	private Double aliquota;

	@XmlElement(name = "CST")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Código da situação tributária deo IPI")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qUnid")
	@InformacaoValidavel(decimal = { 13, 4 }, tiposObrigatorios = { "00", "49", "50", "99" }, nomeExibicao = "Quantidade de unidade tributável do IPI")
	private Double quantidadeUnidadeTributavel;

	@XmlElement(name = "vIPI")
	@InformacaoValidavel(tiposObrigatorios = { "00", "49", "50", "99" }, nomeExibicao = "Valor do IPI")
	private Double valor;

	@XmlElement(name = "vBC")
	@InformacaoValidavel(tiposObrigatorios = { "00", "49", "50", "99" }, nomeExibicao = "Valor de base de cáculo do IPI")
	private Double valorBC;

	@XmlElement(name = "vUnid")
	@InformacaoValidavel(tiposObrigatorios = { "00", "49", "50", "99" }, nomeExibicao = "Valor da unidade tributável do IPI")
	private Double valorUnidadeTributavel;

	public Double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC * (aliquota / 100d) : null;
	}

	public IPIGeral carregarValores() {
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
	public Double getQuantidadeUnidadeTributavel() {
		return quantidadeUnidadeTributavel;
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

	@XmlTransient
	public Double getValorUnidadeTributavel() {
		return valorUnidadeTributavel;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoSituacaoTributaria(String codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setQuantidadeUnidadeTributavel(Double quantidadeUnidadeTributavel) {
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
