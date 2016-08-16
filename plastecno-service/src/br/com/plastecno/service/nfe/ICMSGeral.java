package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ICMSGeral {

	@XmlElement(name = "pICMS")
	private Double aliquota;

	@XmlElement(name = "pICMSST")
	private Double aliquotaST;

	@XmlElement(name = "CST")
	private Integer codigoSituacaoTributaria;

	@XmlElement(name = "modBC")
	private Integer modalidadeDeterminacaoBC;

	@XmlElement(name = "modBCST")
	private Integer modalidadeDeterminacaoBCST;

	@XmlElement(name = "motDesICMS")
	private String motivoDesoneracao;

	@XmlElement(name = "orig")
	private Integer origemMercadoria;

	@XmlElement(name = "pMVAST")
	private Double percentualMargemValorAdicionadoICMSST;

	@XmlElement(name = "pRedBC")
	private Double percentualReducaoBC;

	@XmlElement(name = "vICMS")
	private Double valor;

	@XmlElement(name = "vBC")
	private Double valorBC;

	@XmlElement(name = "vBCST")
	private Double valorBCST;

	@XmlElement(name = "vICMSST")
	private Double valorST;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC
				* (aliquota / 100d) : 0d;
	}

	public double calcularValorST() {
		return valorBCST != null && aliquotaST != null ? valorBCST
				* (aliquotaST / 100d) : 0d;
	}

	public ICMSGeral carregarValoresAliquotas() {
		valor = calcularValor();
		valorST = calcularValorST();
		return this;
	}

	@XmlTransient
	public Double getAliquota() {
		return aliquota;
	}

	@XmlTransient
	public Double getAliquotaST() {
		return aliquotaST;
	}

	@XmlTransient
	public TipoTributacaoICMS getTipoTributacao() {
		return TipoTributacaoICMS.getTipoTributacao(codigoSituacaoTributaria);
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
	public Double getValorBCST() {
		return valorBCST == null ? 0 : valorBCST;
	}

	@XmlTransient
	public Double getValorST() {
		return valorST;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setAliquotaST(Double aliquotaST) {
		this.aliquotaST = aliquotaST;
	}

	public void setCodigoSituacaoTributaria(Integer codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setModalidadeDeterminacaoBC(Integer modalidadeDeterminacaoBC) {
		this.modalidadeDeterminacaoBC = modalidadeDeterminacaoBC;
	}

	public void setModalidadeDeterminacaoBCST(Integer modalidadeDeterminacaoBCST) {
		this.modalidadeDeterminacaoBCST = modalidadeDeterminacaoBCST;
	}

	public void setMotivoDesoneracao(String motivoDesoneracao) {
		this.motivoDesoneracao = motivoDesoneracao;
	}

	public void setOrigemMercadoria(Integer origemMercadoria) {
		this.origemMercadoria = origemMercadoria;
	}

	public void setPercentualMargemValorAdicionadoICMSST(
			Double percentualMargemValorAdicionadoICMSST) {
		this.percentualMargemValorAdicionadoICMSST = percentualMargemValorAdicionadoICMSST;
	}

	public void setPercentualReducaoBC(Double percentualReducaoBC) {
		this.percentualReducaoBC = percentualReducaoBC;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorBCST(Double valorBCST) {
		this.valorBCST = valorBCST;
	}

	public void setValorST(Double valorST) {
		this.valorST = valorST;
	}

}