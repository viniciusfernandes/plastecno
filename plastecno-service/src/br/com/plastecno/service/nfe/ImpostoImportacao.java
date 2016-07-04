package br.com.plastecno.service.nfe;

public class ImpostoImportacao {
	private Double valorBaseCalculo;
	private Double valorDespesaAduaneira;
	private Double valorImpostoImportacao;
	private Double valorIOF;

	public Double getValorBaseCalculo() {
		return valorBaseCalculo;
	}

	public Double getValorDespesaAduaneira() {
		return valorDespesaAduaneira;
	}

	public Double getValorImpostoImportacao() {
		return valorImpostoImportacao;
	}

	public Double getValorIOF() {
		return valorIOF;
	}

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
