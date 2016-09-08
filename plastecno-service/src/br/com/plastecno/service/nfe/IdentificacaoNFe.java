package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class IdentificacaoNFe {

	@InformacaoValidavel(obrigatorio = true, tamanho = 8, nomeExibicao = "C�digo da chave de acesso")
	@XmlElement(name = "cNF")
	private String chaveAcesso;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "UF da identifica��o do emitente da NFe")
	@XmlElement(name = "cUF")
	private String codigoUFEmitente;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{4}-\\d{2}-\\d{2}", nomeExibicao = "Data emiss�o da NFe")
	@XmlElement(name = "dEmi")
	private String dataEmissao;

	@XmlElement(name = "dhCont")
	private String dataHoraContingencia;

	@InformacaoValidavel(padrao = "\\d{4}-\\d{2}-\\d{5}:\\d{2}:\\d{2}", nomeExibicao = "Dada/hora entrada em contig�ncia")
	@XmlElement(name = "verProc")
	private String dataHoraEntradaContigencia;

	@InformacaoValidavel(padrao = "\\d{4}-\\d{2}-\\d{2}T\\d{1,2}:\\d{1,2}:\\d{1,2}[-^+]\\d{4}", nomeExibicao = "Data e hora entrada/sa�da produto")
	@XmlElement(name = "dhSaiEnt")
	private String dataHoraEntradaSaidaProduto;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", nomeExibicao = "Destino da opera��o da NFe")
	@XmlElement(name = "idDest")
	private String destinoOperacao;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", nomeExibicao = "D�gito verificador da NFe")
	@XmlElement(name = "cDV")
	private String digitoVerificador;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", nomeExibicao = "Finalidade da emiss�o  da NFe")
	@XmlElement(name = "finNFe")
	private Integer finalidadeEmissao;

	@InformacaoValidavel(padrao = "\\d{2}:\\d{2}:\\d{2}", nomeExibicao = "Hora entrada/sa�da produto")
	@XmlElement(name = "dSaiEnt")
	private String horaSaidaEntradaProduto;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Forma de pagamento da NFe")
	@XmlElement(name = "indPag")
	private Integer indicadorFormaPagamento;

	@InformacaoValidavel(intervaloComprimento = { 0, 256 }, nomeExibicao = "Justificativa de entrada em contig�ncia")
	@XmlElement(name = "xJust")
	private String justificativaContigencia;

	@XmlElement(name = "NFref")
	private List<NFeReferenciada> listaNFeReferenciada;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Modelo da NFe")
	@XmlElement(name = "mod")
	private final String modelo = "55";

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{7}", nomeExibicao = "C�digo munic�pio do fator gerador")
	@XmlElement(name = "cMunFG")
	private String municipioOcorrenciaFatorGerador;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Natureza da opera��o da NFe")
	@XmlElement(name = "natOp")
	private String naturezaOperacao;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,9}", nomeExibicao = "N�mero da NFe")
	@XmlElement(name = "nNF")
	private String numero;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", nomeExibicao = "Opera��o com consumidor final da NFe")
	@XmlElement(name = "indFinal")
	private String operacaoConsumidorFinal;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 9 }, nomeExibicao = "Processo de emiss�o da NFe")
	@XmlElement(name = "procEmi")
	private String processoEmissao;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,3}", nomeExibicao = "S�rie da NFe")
	@XmlElement(name = "serie")
	private String serie;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", nomeExibicao = "Identifica��o do ambiente de emiss�o")
	@XmlElement(name = "tpAmb")
	private String tipoAmbiente;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 9 }, nomeExibicao = "Tipo emiss�o da NFe")
	@XmlElement(name = "tpEmis")
	private String tipoEmissao;

	@InformacaoValidavel(obrigatorio = true, opcoes = { "0", "1", "2", "3", "4", "5" }, nomeExibicao = "Formato da impress�o do DANFE")
	@XmlElement(name = "tpImp")
	private String tipoImpressao;

	@InformacaoValidavel(obrigatorio = true, opcoes = { "0", "1" }, nomeExibicao = "Tipo opera��o da NFe")
	@XmlElement(name = "tpNF")
	private String tipoOperacao;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 20 }, nomeExibicao = "Vers�o do processo de emiss�o da NFe")
	@XmlElement(name = "verProc")
	private String versaoProcessoEmissao;

	@XmlTransient
	public String getChaveAcesso() {
		return chaveAcesso;
	}

	@XmlTransient
	public String getCodigoUFEmitente() {
		return codigoUFEmitente;
	}

	@XmlTransient
	public String getDataEmissao() {
		return dataEmissao;
	}

	@XmlTransient
	public String getDataHoraContingencia() {
		return dataHoraContingencia;
	}

	@XmlTransient
	public String getDataHoraEntradaContigencia() {
		return dataHoraEntradaContigencia;
	}

	@XmlTransient
	public String getDataHoraEntradaSaidaProduto() {
		return dataHoraEntradaSaidaProduto;
	}

	@XmlTransient
	public String getDestinoOperacao() {
		return destinoOperacao;
	}

	@XmlTransient
	public String getDigitoVerificador() {
		return digitoVerificador;
	}

	@XmlTransient
	public Integer getFinalidadeEmissao() {
		return finalidadeEmissao;
	}

	@XmlTransient
	public String getHoraSaidaEntradaProduto() {
		return horaSaidaEntradaProduto;
	}

	@XmlTransient
	public Integer getIndicadorFormaPagamento() {
		return indicadorFormaPagamento;
	}

	@XmlTransient
	public String getJustificativaContigencia() {
		return justificativaContigencia;
	}

	@XmlTransient
	public List<NFeReferenciada> getListaNFeReferenciada() {
		return listaNFeReferenciada;
	}

	@XmlTransient
	public String getModelo() {
		return modelo;
	}

	@XmlTransient
	public String getMunicipioOcorrenciaFatorGerador() {
		return municipioOcorrenciaFatorGerador;
	}

	@XmlTransient
	public String getNaturezaOperacao() {
		return naturezaOperacao;
	}

	@XmlTransient
	public String getNumero() {
		return numero;
	}

	@XmlTransient
	public String getOperacaoConsumidorFinal() {
		return operacaoConsumidorFinal;
	}

	@XmlTransient
	public String getProcessoEmissao() {
		return processoEmissao;
	}

	@XmlTransient
	public String getSerie() {
		return serie;
	}

	@XmlTransient
	public String getTipoAmbiente() {
		return tipoAmbiente;
	}

	@XmlTransient
	public String getTipoEmissao() {
		return tipoEmissao;
	}

	@XmlTransient
	public String getTipoImpressao() {
		return tipoImpressao;
	}

	@XmlTransient
	public String getTipoOperacao() {
		return tipoOperacao;
	}

	@XmlTransient
	public String getVersaoProcessoEmissao() {
		return versaoProcessoEmissao;
	}

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

	public void setDataHoraEntradaContigencia(String dataHoraEntradaContigencia) {
		this.dataHoraEntradaContigencia = dataHoraEntradaContigencia;
	}

	public void setDataHoraEntradaSaidaProduto(String dataHoraEntradaSaidaProduto) {
		this.dataHoraEntradaSaidaProduto = dataHoraEntradaSaidaProduto;
	}

	public void setDestinoOperacao(String destinoOperacao) {
		this.destinoOperacao = destinoOperacao;
	}

	public void setDigitoVerificador(String digitoVerificador) {
		this.digitoVerificador = digitoVerificador;
	}

	public void setFinalidadeEmissao(Integer finalidadeEmissao) {
		this.finalidadeEmissao = finalidadeEmissao;
	}

	public void setHoraSaidaEntradaProduto(String horaSaidaEntradaProduto) {
		this.horaSaidaEntradaProduto = horaSaidaEntradaProduto;
	}

	public void setIndicadorFormaPagamento(Integer indicadorFormaPagamento) {
		this.indicadorFormaPagamento = indicadorFormaPagamento;
	}

	public void setJustificativaContigencia(String justificativaContigencia) {
		this.justificativaContigencia = justificativaContigencia;
	}

	public void setListaNFeReferenciada(List<NFeReferenciada> listaNFeReferenciada) {
		this.listaNFeReferenciada = listaNFeReferenciada;
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

	public void setOperacaoConsumidorFinal(String operacaoConsumidorFinal) {
		this.operacaoConsumidorFinal = operacaoConsumidorFinal;
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
