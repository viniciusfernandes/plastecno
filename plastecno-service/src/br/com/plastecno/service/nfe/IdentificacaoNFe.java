package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class IdentificacaoNFe {

	@XmlElement(name = "cNF")
	private String chaveAcesso;
	@XmlElement(name = "cUF")
	private String codigoUFEmitente;
	@XmlElement(name = "dEmi")
	private String dataEmissao;
	@XmlElement(name = "dhCont")
	private String dataHoraContingencia;
	@XmlElement(name = "cDV")
	private String digitoVerificador;
	@XmlElement(name = "finNFe")
	private String finalidadeEmissao;
	@XmlElement(name = "dSaiEnt")
	private String horaSaidaEntrada;
	@XmlElement(name = "indPag")
	private Integer indicadorFormaPagamento;
	@XmlElement(name = "xJust")
	private String justificativaContigencia;
	@XmlElement(name = "mod")
	private String modelo;
	@XmlElement(name = "cMunFG")
	private String municipioOcorrenciaFatorGerador;
	@XmlElement(name = "natOp")
	private String naturezaOperacao;
	@XmlElement(name = "nNF")
	private String numero;
	@XmlElement(name = "procEmi")
	private String processoEmissao;
	@XmlElement(name = "serie")
	private String serie;
	@XmlElement(name = "tpAmb")
	private String tipoAmbiente;
	@XmlElement(name = "tpImp")
	private String tipoEmissao;

	@XmlElement(name = "tpImp")
	private String tipoImpressao;

	@XmlElement(name = "tpNF")
	private String tipoOperacao;

	@XmlElement(name = "verProc")
	private String versaoProcessoEmissao;

	public void setChaveAcesso(String chaveAcesso) {
		this.chaveAcesso = chaveAcesso;
	}

	public void setCodigoUFEmitente(String codigoUFEmitente) {
		this.codigoUFEmitente = codigoUFEmitente;
	}

	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public void setDataHoraContingencia(String dataHoraContingencia) {
		this.dataHoraContingencia = dataHoraContingencia;
	}

	public void setDigitoVerificador(String digitoVerificador) {
		this.digitoVerificador = digitoVerificador;
	}

	public void setFinalidadeEmissao(String finalidadeEmissao) {
		this.finalidadeEmissao = finalidadeEmissao;
	}

	public void setHoraSaidaEntrada(String horaSaidaEntrada) {
		this.horaSaidaEntrada = horaSaidaEntrada;
	}

	public void setIndicadorFormaPagamento(Integer indicadorFormaPagamento) {
		this.indicadorFormaPagamento = indicadorFormaPagamento;
	}

	public void setJustificativaContigencia(String justificativaContigencia) {
		this.justificativaContigencia = justificativaContigencia;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public void setMunicipioOcorrenciaFatorGerador(String municipioOcorrenciaFatorGerador) {
		this.municipioOcorrenciaFatorGerador = municipioOcorrenciaFatorGerador;
	}

	public void setNaturezaOperacao(String naturezaOperacao) {
		this.naturezaOperacao = naturezaOperacao;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setProcessoEmissao(String processoEmissao) {
		this.processoEmissao = processoEmissao;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public void setTipoAmbiente(String tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}

	public void setTipoEmissao(String tipoEmissao) {
		this.tipoEmissao = tipoEmissao;
	}

	public void setTipoImpressao(String tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public void setTipoOperacao(String tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}

	public void setVersaoProcessoEmissao(String versaoProcessoEmissao) {
		this.versaoProcessoEmissao = versaoProcessoEmissao;
	}
}
