package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "numero", "dataVencimento", "valor" })
public class DuplicataNFe {
	@InformacaoValidavel(padrao = "\\d{4}-\\d{2}-\\d{2}", padraoExemplo = "aaaa-mm-dd", nomeExibicao = "Data de vencimento da duplicata")
	@XmlElement(name = "dVenc")
	private String dataVencimento;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Número da duplicata")
	@XmlElement(name = "nDup")
	private String numero;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor da duplicata")
	@XmlElement(name = "vDup")
	private Double valor;

	@XmlTransient
	public String getDataVencimento() {
		return dataVencimento;
	}

	@XmlTransient
	public String getNumero() {
		return numero;
	}

	@XmlTransient
	public Double getValor() {
		return valor;
	}

	public void setDataVencimento(String dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
