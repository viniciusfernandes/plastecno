package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ImpostoImportacao {
	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor do imposto de importação")
	@XmlElement(name = "vII")
	private Double valor;

	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor da BC do imposto de importação")
	@XmlElement(name = "vBC")
	private Double valorBC;

	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor de despesa aduaneira do imposto de importação")
	@XmlElement(name = "vDespAdu")
	private Double valorDespesaAduaneira;

	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor da IOF do imposto de importação")
	@XmlElement(name = "vIOF")
	private Double valorIOF;

	@XmlTransient
	public Double getValor() {
		return valor == null ? 0 : valor;
	}

	@XmlTransient
	public Double getValorBC() {
		return valorBC;
	}

	@XmlTransient
	public Double getValorDespesaAduaneira() {
		return valorDespesaAduaneira;
	}

	@XmlTransient
	public Double getValorIOF() {
		return valorIOF;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorDespesaAduaneira(Double valorDespesaAduaneira) {
		this.valorDespesaAduaneira = valorDespesaAduaneira;
	}

	public void setValorIOF(Double valorIOF) {
		this.valorIOF = valorIOF;
	}

}
