package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "valorBCUFDestino", "percentualFCPDestino", "aliquotaUFDestino", "aliquotaInterestadual",
		"percentualProvisorioPartilha", "valorFCPDestino", "valorUFDestino", "valorUFRemetente" })
public class ICMSInterestadual {
	@XmlElement(name = "pICMSInter")
	@InformacaoValidavel(obrigatorio = true, decimal = { 2, 2 }, nomeExibicao = "Alíquota interestadual do ICMS interestadual")
	private Double aliquotaInterestadual;

	@XmlElement(name = "pICMSUFDest")
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Alíquota de destino do ICMS interestadual")
	private Double aliquotaUFDestino;

	@XmlElement(name = "pFCPUFDest")
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Percentual do fundo combate probreza do ICMS interestadual")
	private Double percentualFCPDestino;

	@XmlElement(name = "pICMSInterPart")
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Percentual Provisorio de Partilha do ICMS interestadual")
	private Double percentualProvisorioPartilha;

	@XmlElement(name = "vBCUFDest")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor da BC do ICMS interestadual")
	private Double valorBCUFDestino;

	@XmlElement(name = "vFCPUFDest")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor do fundo combate probreza do ICMS interestadual")
	private Double valorFCPDestino;

	@XmlElement(name = "vICMSUFDest")
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Valor de destino do ICMS interestadual")
	private Double valorUFDestino;

	@XmlElement(name = "vICMSUFRemet")
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Valor de destino do remetente do ICMS interestadual")
	private Double valorUFRemetente;

	public ICMSInterestadual carregarValores() {
		if (valorBCUFDestino != null && percentualFCPDestino != null) {
			valorFCPDestino = valorBCUFDestino * percentualFCPDestino / 100d;
		} else {
			valorFCPDestino = 0d;
		}

		if (valorBCUFDestino != null && aliquotaUFDestino != null && percentualProvisorioPartilha != null) {
			valorUFDestino = valorBCUFDestino * aliquotaUFDestino * percentualProvisorioPartilha / 10000d;
		} else {
			valorUFDestino = 0d;
		}

		if (valorBCUFDestino != null && aliquotaUFDestino != null) {
			valorUFRemetente = valorBCUFDestino * aliquotaUFDestino / 100d - valorUFDestino;
		} else {
			valorFCPDestino = 0d;
		}
		return this;
	}

	@XmlTransient
	public Double getAliquotaInterestadual() {
		return aliquotaInterestadual;
	}

	@XmlTransient
	public Double getAliquotaUFDestino() {
		return aliquotaUFDestino;
	}

	@XmlTransient
	public Double getPercentualFCPDestino() {
		return percentualFCPDestino;
	}

	@XmlTransient
	public Double getPercentualProvisorioPartilha() {
		return percentualProvisorioPartilha;
	}

	@XmlTransient
	public Double getValorBCUFDestino() {
		return valorBCUFDestino;
	}

	@XmlTransient
	public Double getValorFCPDestino() {
		return valorFCPDestino == null ? 0 : valorFCPDestino;
	}

	@XmlTransient
	public Double getValorUFDestino() {
		return valorUFDestino == null ? 0 : valorUFDestino;
	}

	@XmlTransient
	public Double getValorUFRemetente() {
		return valorUFRemetente == null ? 0 : valorUFRemetente;
	}

	public void setAliquotaInterestadual(Double aliquotaInterestadual) {
		this.aliquotaInterestadual = aliquotaInterestadual;
	}

	public void setAliquotaUFDestino(Double aliquotaUFDestino) {
		this.aliquotaUFDestino = aliquotaUFDestino;
	}

	public void setPercentualFCPDestino(Double percentualFCPDestino) {
		this.percentualFCPDestino = percentualFCPDestino;
	}

	public void setPercentualProvisorioPartilha(Double percentualProvisorioPartilha) {
		this.percentualProvisorioPartilha = percentualProvisorioPartilha;
	}

	public void setValorBCUFDestino(Double valorBCUFDestino) {
		this.valorBCUFDestino = valorBCUFDestino;
	}

	public void setValorFCPDestino(Double valorFCPDestino) {
		this.valorFCPDestino = valorFCPDestino;
	}

	public void setValorUFDestino(Double valorUFDestino) {
		this.valorUFDestino = valorUFDestino;
	}

	public void setValorUFRemetente(Double valorUFRemetente) {
		this.valorUFRemetente = valorUFRemetente;
	}

}
