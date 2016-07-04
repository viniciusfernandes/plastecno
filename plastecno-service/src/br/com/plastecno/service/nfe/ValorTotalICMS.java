package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ValorTotalICMS {
	@XmlElement(name = "vBC")
	private Double valorBaseCalculo;
	@XmlElement(name = "vBCST")
	private Double valorBaseCalculoST;
	@XmlElement(name = "vOutro")
	private Double valorOutrasDespesasAcessorias;
	@XmlElement(name = "vICMS")
	private Double valorTotal;
	@XmlElement(name = "vCOFINS")
	private Double valorTotalCOFINS;
	@XmlElement(name = "vDesc")
	private Double valorTotalDesconto;
	@XmlElement(name = "vFrete")
	private Double valorTotalFrete;
	@XmlElement(name = "vII")
	private Double valorTotalII;
	@XmlElement(name = "vIPI")
	private Double valorTotalIPI;
	@XmlElement(name = "vNF")
	private Double valorTotalNF;
	@XmlElement(name = "vPIS")
	private Double valorTotalPIS;
	@XmlElement(name = "vProd")
	private Double valorTotalProdutosServicos;
	@XmlElement(name = "vSeg")
	private Double valorTotalSeguro;
	@XmlElement(name = "vST")
	private Double valorTotalST;

	public void setValorBaseCalculo(Double valorBaseCalculo) {
		this.valorBaseCalculo = valorBaseCalculo;
	}

	public void setValorBaseCalculoST(Double valorBaseCalculoST) {
		this.valorBaseCalculoST = valorBaseCalculoST;
	}

	public void setValorOutrasDespesasAcessorias(Double valorOutrasDespesasAcessorias) {
		this.valorOutrasDespesasAcessorias = valorOutrasDespesasAcessorias;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public void setValorTotalCOFINS(Double valorTotalCOFINS) {
		this.valorTotalCOFINS = valorTotalCOFINS;
	}

	public void setValorTotalDesconto(Double valorTotalDesconto) {
		this.valorTotalDesconto = valorTotalDesconto;
	}

	public void setValorTotalFrete(Double valorTotalFrete) {
		this.valorTotalFrete = valorTotalFrete;
	}

	public void setValorTotalII(Double valorTotalII) {
		this.valorTotalII = valorTotalII;
	}

	public void setValorTotalIPI(Double valorTotalIPI) {
		this.valorTotalIPI = valorTotalIPI;
	}

	public void setValorTotalNF(Double valorTotalNF) {
		this.valorTotalNF = valorTotalNF;
	}

	public void setValorTotalPIS(Double valorTotalPIS) {
		this.valorTotalPIS = valorTotalPIS;
	}

	public void setValorTotalProdutosServicos(Double valorTotalProdutosServicos) {
		this.valorTotalProdutosServicos = valorTotalProdutosServicos;
	}

	public void setValorTotalSeguro(Double valorTotalSeguro) {
		this.valorTotalSeguro = valorTotalSeguro;
	}

	public void setValorTotalST(Double valorTotalST) {
		this.valorTotalST = valorTotalST;
	}

}
