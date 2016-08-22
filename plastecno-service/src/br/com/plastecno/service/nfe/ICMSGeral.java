package br.com.plastecno.service.nfe;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoICMS.*;

public class ICMSGeral {

	@XmlElement(name = "pICMS")
	private Double aliquota;

	@XmlElement(name = "pICMSST")
	private Double aliquotaST;

	@XmlElement(name = "CST")
	private String codigoSituacaoTributaria;

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

	@XmlElement(name = "pRedBCST")
	private Double percentualReducaoBCST;

	@XmlElement(name = "vICMS")
	private Double valor;

	@XmlElement(name = "vBC")
	private Double valorBC;

	@XmlElement(name = "vBCST")
	private Double valorBCST;

	@XmlElement(name = "vBCSTRet")
	private Double valorBCSTRetido;

	@XmlElement(name = "vICMSST")
	private Double valorST;

	@XmlElement(name = "vSTRet")
	private Double valorSTRetido;

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
	public String getCodigoSituacaoTributaria() {
		return codigoSituacaoTributaria;
	}

	@XmlTransient
	public Integer getModalidadeDeterminacaoBC() {
		return modalidadeDeterminacaoBC;
	}

	@XmlTransient
	public Integer getModalidadeDeterminacaoBCST() {
		return modalidadeDeterminacaoBCST;
	}

	@XmlTransient
	public String getMotivoDesoneracao() {
		return motivoDesoneracao;
	}

	@XmlTransient
	public Integer getOrigemMercadoria() {
		return origemMercadoria;
	}

	@XmlTransient
	public Double getPercentualMargemValorAdicionadoICMSST() {
		return percentualMargemValorAdicionadoICMSST;
	}

	@XmlTransient
	public Double getPercentualReducaoBC() {
		return percentualReducaoBC;
	}

	@XmlTransient
	public Double getPercentualReducaoBCST() {
		return percentualReducaoBCST;
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
	public Double getValorBCSTRetido() {
		return valorBCSTRetido;
	}

	@XmlTransient
	public Double getValorST() {
		return valorST == null ? 0 : valorST;
	}

	@XmlTransient
	public Double getValorSTRetido() {
		return valorSTRetido;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setAliquotaST(Double aliquotaST) {
		this.aliquotaST = aliquotaST;
	}

	public void setCodigoSituacaoTributaria(String codigoSituacaoTributaria) {
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

	public void setPercentualReducaoBCST(Double percentualReducaoBCST) {
		this.percentualReducaoBCST = percentualReducaoBCST;
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

	public void setValorBCSTRetido(Double valorBCSTRetido) {
		this.valorBCSTRetido = valorBCSTRetido;
	}

	public void setValorST(Double valorST) {
		this.valorST = valorST;
	}

	public void setValorSTRetido(Double valorSTRetido) {
		this.valorSTRetido = valorSTRetido;
	}

	public void validar() throws BusinessException {
		TipoTributacaoICMS t = getTipoTributacao();
		List<String> l = new ArrayList<String>();

		if (origemMercadoria == null) {
			l.add("Origem da mercadoria do ICMS do ICMS é obrigatório");
		}

		if (t == null) {
			l.add("Código da situação de tributação do ICMS é obrigatório");
		}

		if (modalidadeDeterminacaoBC == null) {
			l.add("Modalidade de determinação da base de cálculo do ICMS é obrigatório");
		}

		if (aliquota == null) {
			l.add("Alíquota do ICMS é obrigatório");
		}

		if (valor == null
				&& (!ICMS_40.equals(t) && !ICMS_41.equals(t) && !ICMS_50
						.equals(t))) {
			l.add("Valor do ICMS do ICMS é obrigatório");
		}

		if (valorBC == null) {
			l.add("Valor da base de cálculo do ICMS é obrigatório");
		}

		if (valorBCST == null
				&& (ICMS_10.equals(t) || ICMS_30.equals(t) || ICMS_60.equals(t) || ICMS_90
						.equals(t))) {
			l.add("Valor da base de cálculo ST do ICMS é obrigatório");
		}

		if (modalidadeDeterminacaoBCST == null
				&& (ICMS_10.equals(t) || ICMS_30.equals(t) || ICMS_60.equals(t) || ICMS_90
						.equals(t))) {
			l.add("Modalidade de determinação do ST do ICMS é obrigatório");
		}

		if (percentualMargemValorAdicionadoICMSST == null
				&& (ICMS_10.equals(t) || ICMS_30.equals(t) || ICMS_90.equals(t))) {
			l.add("Percentual de margem do valor adicionado do ST do ICMS é obrigatório");
		}

		if (percentualReducaoBC == null
				&& (ICMS_51.equals(t) || ICMS_60.equals(t))) {
			l.add("Percentual de redução de BC do ICMS é obrigatório");
		}

		if (percentualReducaoBCST == null
				&& (ICMS_10.equals(t) || ICMS_30.equals(t))) {
			l.add("Percentual de redução de BC do ST do ICMS é obrigatório");
		}

		if (valorST == null
				&& (ICMS_10.equals(t) || ICMS_30.equals(t) || ICMS_60.equals(t) || ICMS_90
						.equals(t))) {
			l.add("Valor do ICMS ST é obrigatório");
		}

		if (aliquotaST == null
				&& (ICMS_10.equals(t) || ICMS_30.equals(t) || ICMS_60.equals(t) || ICMS_90
						.equals(t))) {
			l.add("Alíquota do ICMS ST é obrigatório");
		}

		if (percentualReducaoBC == null && ICMS_20.equals(t)) {
			l.add("Percentual de redução de BC do ICMS é obrigatório");
		}

		if (valorBCSTRetido == null && ICMS_60.equals(t)) {
			l.add("Valor de BC do ICMS retido é obrigatório");
		}

		if (valorSTRetido == null && ICMS_60.equals(t)) {
			l.add("Valor de ST do ICMS retido é obrigatório");
		}

		if (l.size() > 0) {
			throw new BusinessException(l);
		}
	}

}