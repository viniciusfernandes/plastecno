package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class RetencaoICMSTransporteNFe {
	@XmlElement(name = "pICMSRet")
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Al�quota de reten��o do ICMS do transporte")
	private Double aliquota;

	@XmlElement(name = "CFOP")
	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{4}", padraoExemplo = "4 digitos", nomeExibicao = "CFOP de reten��o do ICMS do transporte")
	private String cfop;

	@XmlElement(name = "vServ")
	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{7}", padraoExemplo = "7 digitos", nomeExibicao = "C�d. munic�pio gerador de reten��o do ICMS do transporte")
	private String codigoMunicipioGerador;

	@XmlElement(name = "vICMSRet")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor de reten��o do ICMS do transporte")
	private Double valor;

	@XmlElement(name = "vBCRet")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor BC de reten��o do ICMS do transporte")
	private Double valorBC;

	@XmlElement(name = "vServ")
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor do servi�o de reten��o do ICMS do transporte")
	private Double valorServico;

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
