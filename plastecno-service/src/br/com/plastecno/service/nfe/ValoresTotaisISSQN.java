package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ValoresTotaisISSQN {
	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total da base de cáculo de ISS")
	@XmlElement(name = "vBC")
	private Double valorBC;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total de COFINS sobre ISS")
	@XmlElement(name = "vCOFINS")
	private Double valorCofins;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total de ISS")
	@XmlElement(name = "vISS")
	private Double valorIss;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total de PIS sobre ISS")
	@XmlElement(name = "vPIS")
	private Double valorPis;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total de serviço dos totais de ISS")
	@XmlElement(name = "vServ")
	private Double valorServico;

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorCofins(Double valorCofins) {
		this.valorCofins = valorCofins;
	}

	public void setValorIss(Double valorIss) {
		this.valorIss = valorIss;
	}

	public void setValorPis(Double valorPis) {
		this.valorPis = valorPis;
	}

	public void setValorServico(Double valorServico) {
		this.valorServico = valorServico;
	}
}
