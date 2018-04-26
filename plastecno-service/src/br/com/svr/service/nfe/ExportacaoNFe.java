package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "ufSaida", "localExportacao", "localDespacho" })
public class ExportacaoNFe {
	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Local de despacho")
	@XmlElement(name = "xLocDespacho")
	private String localDespacho;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Local")
	@XmlElement(name = "xLocExporta")
	private String localExportacao;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "UF de saída da exportação")
	@XmlElement(name = "UFSaidaPais")
	private String ufSaida;

	@XmlTransient
	public String getLocalDespacho() {
		return localDespacho;
	}

	@XmlTransient
	public String getLocalExportacao() {
		return localExportacao;
	}

	@XmlTransient
	public String getUfSaida() {
		return ufSaida;
	}

	public void setLocalDespacho(String localDespacho) {
		this.localDespacho = localDespacho;
	}

	public void setLocalExportacao(String localExportacao) {
		this.localExportacao = localExportacao;
	}

	public void setUfSaida(String ufSaida) {
		this.ufSaida = ufSaida;
	}

}
