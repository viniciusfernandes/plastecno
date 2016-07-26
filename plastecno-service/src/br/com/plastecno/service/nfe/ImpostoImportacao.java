package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ImpostoImportacao {
	@XmlElement(name = "vBC")
	private Double valorBaseCalculo;

	@XmlElement(name = "vDespAdu")
	private Double valorDespesaAduaneira;
	
	@XmlElement(name = "vII")
	private Double valorImpostoImportacao;
	
	@XmlElement(name = "vIOF")
	private Double valorIOF;

	public void setValorBaseCalculo(Double valorBaseCalculo) {
		this.valorBaseCalculo = valorBaseCalculo;
	}

	public void setValorDespesaAduaneira(Double valorDespesaAduaneira) {
		this.valorDespesaAduaneira = valorDespesaAduaneira;
	}

	public void setValorImpostoImportacao(Double valorImpostoImportacao) {
		this.valorImpostoImportacao = valorImpostoImportacao;
	}

	public void setValorIOF(Double valorIOF) {
		this.valorIOF = valorIOF;
	}

}
