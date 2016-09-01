package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class DeclaracaoImportacao {
	@InformacaoValidavel(padrao = "\\d{14}", nomeExibicao = "CNPJ encomendante de importação do produto/serviço")
	@XmlElement(name = "CNPJ")
	private String cnpjEncomendante;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Código do exportador de importação do produto/serviço")
	@XmlElement(name = "cExportador")
	private String codigoExportador;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{4}-\\d{2}-\\d{2}", nomeExibicao = "Data de desembaraço de importação do produto/serviço")
	@XmlElement(name = "dDesemb")
	private String dataDesembaraco;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{4}-\\d{2}-\\d{2}", nomeExibicao = "Data da declaração de importação do produto/serviço")
	@XmlElement(name = "nDI")
	private String dataImportacao;

	@InformacaoValidavel(obrigatorio = true, iteravel = true, nomeExibicao = "Lista de adição de importação")
	@XmlElement(name = "adi")
	private List<AdicaoImportacao> listaAdicao;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Local de desembaraço de importação do produto/serviço")
	@XmlElement(name = "xLocDesemb")
	private String localDesembaraco;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 12 }, nomeExibicao = "Número da declaração de importação do produto/serviço")
	@XmlElement(name = "nDI")
	private String numero;

	@InformacaoValidavel(obrigatorio = true, tamanho = 1, nomeExibicao = "Intermédio de importação do produto/serviço")
	@XmlElement(name = "tpIntermedio")
	private String tipoIntermediacao;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "Tipo transporte de importação do produto/serviço")
	@XmlElement(name = "tpViaTransp")
	private String tipoTransporteInternacional;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "UF de desembaraço de importação do produto/serviço")
	@XmlElement(name = "UFDesemb")
	private String ufDesembaraco;

	@InformacaoValidavel(tamanho = 2, nomeExibicao = "UF encomendante de importação do produto/serviço")
	@XmlElement(name = "UFTerceiro")
	private String ufEncomendante;

	@InformacaoValidavel(decimal = { 13, 2 }, nomeExibicao = "Valor AFRMM de importação do produto/serviço")
	@XmlElement(name = "vAFRMM")
	private Double valorAFRMM;

	@XmlTransient
	public String getCnpjEncomendante() {
		return cnpjEncomendante;
	}

	@XmlTransient
	public String getCodigoExportador() {
		return codigoExportador;
	}

	@XmlTransient
	public String getDataDesembaraco() {
		return dataDesembaraco;
	}

	@XmlTransient
	public String getDataImportacao() {
		return dataImportacao;
	}

	@XmlTransient
	public List<AdicaoImportacao> getListaAdicao() {
		return listaAdicao;
	}

	@XmlTransient
	public String getLocalDesembaraco() {
		return localDesembaraco;
	}

	@XmlTransient
	public String getNumero() {
		return numero;
	}

	@XmlTransient
	public String getTipoIntermediacao() {
		return tipoIntermediacao;
	}

	@XmlTransient
	public String getTipoTransporteInternacional() {
		return tipoTransporteInternacional;
	}

	@XmlTransient
	public String getUfDesembaraco() {
		return ufDesembaraco;
	}

	@XmlTransient
	public String getUfEncomendante() {
		return ufEncomendante;
	}

	@XmlTransient
	public Double getValorAFRMM() {
		return valorAFRMM;
	}

	public void setCnpjEncomendante(String cnpjEncomendante) {
		this.cnpjEncomendante = cnpjEncomendante;
	}

	public void setCodigoExportador(String codigoExportador) {
		this.codigoExportador = codigoExportador;
	}

	public void setDataDesembaraco(String dataDesembaraco) {
		this.dataDesembaraco = dataDesembaraco;
	}

	public void setDataImportacao(String dataImportacao) {
		this.dataImportacao = dataImportacao;
	}

	public void setListaAdicao(List<AdicaoImportacao> listaAdicao) {
		this.listaAdicao = listaAdicao;
	}

	public void setLocalDesembaraco(String localDesembaraco) {
		this.localDesembaraco = localDesembaraco;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setTipoIntermediacao(String tipoIntermediacao) {
		this.tipoIntermediacao = tipoIntermediacao;
	}

	public void setTipoTransporteInternacional(String tipoTransporteInternacional) {
		this.tipoTransporteInternacional = tipoTransporteInternacional;
	}

	public void setUfDesembaraco(String ufDesembaraco) {
		this.ufDesembaraco = ufDesembaraco;
	}

	public void setUfEncomendante(String ufEncomendante) {
		this.ufEncomendante = ufEncomendante;
	}

	public void setValorAFRMM(Double valorAFRMM) {
		this.valorAFRMM = valorAFRMM;
	}
}
