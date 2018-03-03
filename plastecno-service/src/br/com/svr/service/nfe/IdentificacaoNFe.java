package br.com.svr.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "codigoUFEmitente", "chaveAcesso", "naturezaOperacao", "indicadorFormaPagamento", "modelo",
		"serie", "numero", "dataHoraEmissao", "dataHoraEntradaSaidaProduto", "tipoOperacao", "destinoOperacao",
		"municipioOcorrenciaFatorGerador", "tipoImpressao", "tipoEmissao", "digitoVerificador", "tipoAmbiente",
		"finalidadeEmissao", "operacaoConsumidorFinal", "tipoPresencaComprador", "processoEmissao",
		"versaoProcessoEmissao", "dataHoraEntradaContigencia", "justificativaContigencia", "listaNFeReferenciada" })
public class IdentificacaoNFe {
	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{8}", padraoExemplo = "8 digitos", nomeExibicao = "Código da chave de acesso")
	@XmlElement(name = "cNF")
	private String chaveAcesso;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "UF da identificação do emitente da NFe")
	@XmlElement(name = "cUF")
	private String codigoUFEmitente;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{4}-\\d{2}-\\d{2}T\\d{1,2}:\\d{1,2}:\\d{1,2}[-^+]\\d{2}:\\d{2}", padraoExemplo = "yyyy-MM-ddTHH:mm:ss+HH:mm", nomeExibicao = "Data emissão da NFe")
	@XmlElement(name = "dhEmi")
	private String dataHoraEmissao;

	@InformacaoValidavel(padrao = "\\d{4}-\\d{2}-\\d{5}:\\d{2}:\\d{2}", padraoExemplo = "dd/mm/aaaa", nomeExibicao = "Dada/hora entrada em contigência")
	@XmlElement(name = "dhCont")
	private String dataHoraEntradaContigencia;

	@InformacaoValidavel(padrao = "\\d{4}-\\d{2}-\\d{2}T\\d{1,2}:\\d{1,2}:\\d{1,2}[-^+]\\d{2}:\\d{2}", padraoExemplo = "yyyy-MM-ddTHH:mm:ss+HH:mm", nomeExibicao = "Data e hora entrada/saída produto")
	@XmlElement(name = "dhSaiEnt")
	private String dataHoraEntradaSaidaProduto;

	@XmlTransient
	private String dataSaida;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", padraoExemplo = "1 digito", nomeExibicao = "Destino da operação da NFe")
	@XmlElement(name = "idDest")
	private String destinoOperacao;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", padraoExemplo = "1 digito", nomeExibicao = "Dígito verificador da NFe")
	@XmlElement(name = "cDV")
	private String digitoVerificador;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", padraoExemplo = "1 digito", nomeExibicao = "Finalidade da emissão  da NFe")
	@XmlElement(name = "finNFe")
	private Integer finalidadeEmissao;

	@XmlTransient
	private String horaSaida;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Forma de pagamento da NFe")
	@XmlElement(name = "indPag")
	private Integer indicadorFormaPagamento;

	@InformacaoValidavel(intervaloComprimento = { 0, 256 }, nomeExibicao = "Justificativa de entrada em contigência")
	@XmlElement(name = "xJust")
	private String justificativaContigencia;

	@XmlElement(name = "NFref")
	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de NFe referenciada")
	private List<NFeReferenciada> listaNFeReferenciada;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Modelo da NFe")
	@XmlElement(name = "mod")
	private String modelo;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{7}", padraoExemplo = "7 digitos", nomeExibicao = "Código município do fator gerador")
	@XmlElement(name = "cMunFG")
	private String municipioOcorrenciaFatorGerador;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Natureza da operação da NFe")
	@XmlElement(name = "natOp")
	private String naturezaOperacao;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,9}", padraoExemplo = "1 a 9 digitos", nomeExibicao = "Número da NFe")
	@XmlElement(name = "nNF")
	private String numero;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", padraoExemplo = "1 digito", nomeExibicao = "Operação com consumidor final da NFe")
	@XmlElement(name = "indFinal")
	private String operacaoConsumidorFinal;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 9 }, nomeExibicao = "Processo de emissão da NFe")
	@XmlElement(name = "procEmi")
	private String processoEmissao;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,3}", padraoExemplo = "1 a 3 digitos", nomeExibicao = "Série da NFe")
	@XmlElement(name = "serie")
	private String serie;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d", padraoExemplo = "1 digito", nomeExibicao = "Identificação do ambiente de emissão")
	@XmlElement(name = "tpAmb")
	private String tipoAmbiente;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 9 }, nomeExibicao = "Tipo emissão da NFe")
	@XmlElement(name = "tpEmis")
	private String tipoEmissao;

	@InformacaoValidavel(obrigatorio = true, opcoes = { "0", "1", "2", "3", "4", "5" }, nomeExibicao = "Formato da impressão do DANFE")
	@XmlElement(name = "tpImp")
	private String tipoImpressao;

	@InformacaoValidavel(obrigatorio = true, opcoes = { "0", "1" }, nomeExibicao = "Tipo operação da NFe")
	@XmlElement(name = "tpNF")
	private String tipoOperacao;

	@InformacaoValidavel(obrigatorio = true, opcoes = { "0", "1", "2", "3", "4", "9" }, nomeExibicao = "Tipo de presença do comprador")
	@XmlElement(name = "indPres")
	private String tipoPresencaComprador;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 20 }, nomeExibicao = "Versão do processo de emissão da NFe")
	@XmlElement(name = "verProc")
	private String versaoProcessoEmissao;

	public boolean contemNumeroSerieModelo() {
		return numero != null && serie != null && modelo != null;
	}

	@XmlTransient
	public String getChaveAcesso() {
		return chaveAcesso;
	}

	@XmlTransient
	public String getCodigoUFEmitente() {
		return codigoUFEmitente;
	}

	@XmlTransient
	public String getDataHoraEmissao() {
		return dataHoraEmissao;
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
	public String getDataSaida() {
		return dataSaida;
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
	public String getHoraSaida() {
		return horaSaida;
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
	public String getTipoPresencaComprador() {
		return tipoPresencaComprador;
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

	public void setDataHoraEmissao(String dataEmissao) {
		this.dataHoraEmissao = dataEmissao;
	}

	public void setDataHoraEntradaContigencia(String dataHoraEntradaContigencia) {
		this.dataHoraEntradaContigencia = dataHoraEntradaContigencia;
	}

	public void setDataHoraEntradaSaidaProduto(String dataHoraEntradaSaidaProduto) {
		this.dataHoraEntradaSaidaProduto = dataHoraEntradaSaidaProduto;
	}

	public void setDataSaida(String dataSaida) {
		this.dataSaida = dataSaida;
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

	public void setHoraSaida(String horaSaida) {
		this.horaSaida = horaSaida;
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

	public void setTipoPresencaComprador(String tipoPresencaComprador) {
		this.tipoPresencaComprador = tipoPresencaComprador;
	}

	public void setVersaoProcessoEmissao(String versaoProcessoEmissao) {
		this.versaoProcessoEmissao = versaoProcessoEmissao;
	}
}
