package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class RetencaoICMSTransporteNFe {
	@XmlElement(name = "pICMSRet")
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Alíquota de retenção do ICMS do transporte")
	private Double aliquota;

	@XmlElement(name = "CFOP")
	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{4}", padraoExemplo = "4 digitos", nomeExibicao = "CFOP de retenção do ICMS do transporte")
	private String cfop;

	@XmlElement(name = "cMunFG")
	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{7}", padraoExemplo = "7 digitos", nomeExibicao = "Cód. município gerador de retenção do ICMS do transporte")
	private String codigoMunicipioGerador;

	@XmlElement(name = "vICMSRet")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor de retenção do ICMS do transporte")
	private Double valor;

	@XmlElement(name = "vBCRet")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor BC de retenção do ICMS do transporte")
	private Double valorBC;

	@XmlElement(name = "vServ")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor do serviço de retenção do ICMS do transporte")
	private Double valorServico;

	public RetencaoICMSTransporteNFe carregarValorRetido() {
		valor = valorBC != null && aliquota != null ? valorBC * (aliquota / 100d) : 0;
		return this;
	}

	@XmlTransient
	public Double getAliquota() {
		return aliquota;
	}

	@XmlTransient
	public String getCfop() {
		return cfop;
	}

	@XmlTransient
	public String getCodigoMunicipioGerador() {
		return codigoMunicipioGerador;
	}

	@XmlTransient
	public Double getValor() {
		return valor;
	}

	@XmlTransient
	public Double getValorBC() {
		return valorBC;
	}

	@XmlTransient
	public Double getValorServico() {
		return valorServico;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCfop(String cfop) {
		this.cfop = cfop;
	}

	public void setCodigoMunicipioGerador(String codigoMunicipioGerador) {
		this.codigoMunicipioGerador = codigoMunicipioGerador;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorServico(Double valorServico) {
		this.valorServico = valorServico;
	}

}
