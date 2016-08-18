package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ValoresTotaisICMS {
	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de base de calculo do total de ICMS")
	@XmlElement(name = "vBC")
	private Double valorBaseCalculo;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de base de ST de calculo do total de ICMS")
	@XmlElement(name = "vBCST")
	private Double valorBaseCalculoST;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de COFINS")
	@XmlElement(name = "vCOFINS")
	private Double valorTotalCOFINS;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de desconto")
	@XmlElement(name = "vDesc")
	private Double valorTotalDesconto;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de despesas acessórias")
	@XmlElement(name = "vOutro")
	private Double valorTotalDespAcessorias;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de frete")
	@XmlElement(name = "vFrete")
	private Double valorTotalFrete;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de ICMS")
	@XmlElement(name = "vICMS")
	private Double valorTotalICMS;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de Imposto de Importação")
	@XmlElement(name = "vII")
	private Double valorTotalII;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de IPI")
	@XmlElement(name = "vIPI")
	private Double valorTotalIPI;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total da nota fiscal")
	@XmlElement(name = "vNF")
	private Double valorTotalNF;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de PIS")
	@XmlElement(name = "vPIS")
	private Double valorTotalPIS;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de produtos/serviços")
	@XmlElement(name = "vProd")
	private Double valorTotalProdutosServicos;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de seguro")
	@XmlElement(name = "vSeg")
	private Double valorTotalSeguro;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor total de substituição tributária")
	@XmlElement(name = "vST")
	private Double valorTotalST;

	public void setValorBaseCalculo(Double valorBaseCalculo) {
		this.valorBaseCalculo = valorBaseCalculo;
	}

	public void setValorBaseCalculoST(Double valorBaseCalculoST) {
		this.valorBaseCalculoST = valorBaseCalculoST;
	}

	public void setValorTotalCOFINS(Double valorTotalCOFINS) {
		this.valorTotalCOFINS = valorTotalCOFINS;
	}

	public void setValorTotalDesconto(Double valorTotalDesconto) {
		this.valorTotalDesconto = valorTotalDesconto;
	}

	public void setValorTotalDespAcessorias(Double valorTotalDespAcessorias) {
		this.valorTotalDespAcessorias = valorTotalDespAcessorias;
	}

	public void setValorTotalFrete(Double valorTotalFrete) {
		this.valorTotalFrete = valorTotalFrete;
	}

	public void setValorTotalICMS(Double valorTotalICMS) {
		this.valorTotalICMS = valorTotalICMS;
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
