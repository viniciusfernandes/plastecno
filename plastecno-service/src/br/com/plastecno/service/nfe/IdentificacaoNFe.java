package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class IdentificacaoNFe {
	@XmlElement(name = "cUF")
	private String codigoUFEmitente;
	@XmlElement(name = "cNF")
	private String chaveAcesso;
	@XmlElement(name = "natOp")
	private String naturezaOperacao;
	@XmlElement(name = "indPag")
	private Integer indicadorFormaPagamento;
	@XmlElement(name = "mod")
	private String modelo;
	@XmlElement(name = "serie")
	private String serie;
	@XmlElement(name = "nNF")
	private String numero;
	@XmlElement(name = "dEmi")
	private String dataEmissao;
	@XmlElement(name = "dSaiEnt")
	private String horaSaidaEntrada;
	@XmlElement(name = "tpNF")
	private String tipoOperacao;
	@XmlElement(name = "cMunFG")
	private String municipioOcorrenciaFatorGerador;
	public void setCodigoUFEmitente(String codigoUFEmitente) {
		this.codigoUFEmitente = codigoUFEmitente;
	}
	public void setChaveAcesso(String chaveAcesso) {
		this.chaveAcesso = chaveAcesso;
	}
	public void setNaturezaOperacao(String naturezaOperacao) {
		this.naturezaOperacao = naturezaOperacao;
	}
	public void setIndicadorFormaPagamento(Integer indicadorFormaPagamento) {
		this.indicadorFormaPagamento = indicadorFormaPagamento;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}
	public void setHoraSaidaEntrada(String horaSaidaEntrada) {
		this.horaSaidaEntrada = horaSaidaEntrada;
	}
	public void setTipoOperacao(String tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}
	public void setMunicipioOcorrenciaFatorGerador(String municipioOcorrenciaFatorGerador) {
		this.municipioOcorrenciaFatorGerador = municipioOcorrenciaFatorGerador;
	}
}
