package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class ProdutoServicoNFe {
	@XmlElement(name = "CFOP")
	private String cfop;

	@XmlElement(name = "cProd")
	private String codigo;

	@XmlElement(name = "xProd")
	private String descricao;

	@XmlElement(name = "cEAN")
	private String EAN;

	@XmlElement(name = "cEANTrib")
	private String EANTributavel;

	@XmlElement(name = "EXTIPI")
	private String EXTIPI;

	@XmlElement(name = "nItemPed")
	private Integer itemPedidoCompra;

	@XmlElement(name = "NCM")
	private String ncm;

	@XmlElement(name = "xPed")
	private String numeroPedidoCompra;

	@XmlElement(name = "vOutro")
	private Double outrasDespesasAcessorias;

	@XmlElement(name = "qCom")
	private Integer quantidadeComercial;

	@XmlElement(name = "qTrib")
	private Integer quantidadeTributavel;

	@XmlElement(name = "uCom")
	private String unidadeComercial;

	@XmlElement(name = "uTrib")
	private String unidadeTributavel;

	@XmlElement(name = "vDesc")
	private Double valorDesconto;

	@XmlElement(name = "vProd")
	private Double valorTotalBruto;

	@XmlElement(name = "vFrete")
	private Double valorTotalFrete;

	@XmlElement(name = "vSeg")
	private Double valorTotalSeguro;

	@XmlElement(name = "vUnCom")
	private Double valorUnitarioComercializacao;

	@XmlElement(name = "vUnTrib")
	private Double valorUnitarioTributacao;

	@XmlTransient
	public Double getOutrasDespesasAcessorias() {
		return outrasDespesasAcessorias == null ? 0 : outrasDespesasAcessorias;
	}

	@XmlTransient
	public double getValorDesconto() {
		return valorDesconto == null ? 0 : valorDesconto;
	}

	@XmlTransient
	public Double getValorTotalBruto() {
		return valorTotalBruto == null ? 0 : valorTotalBruto;
	}

	@XmlTransient
	public Double getValorTotalFrete() {
		return valorTotalFrete == null ? 0 : valorTotalFrete;
	}

	@XmlTransient
	public Double getValorTotalSeguro() {
		return valorTotalSeguro == null ? 0 : valorTotalSeguro;
	}

	public void setCfop(String cfop) {
		this.cfop = cfop;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setEAN(String eAN) {
		EAN = eAN;
	}

	public void setEANTributavel(String eANTributavel) {
		EANTributavel = eANTributavel;
	}

	public void setEXTIPI(String eXTIPI) {
		EXTIPI = eXTIPI;
	}

	public void setItemPedidoCompra(Integer itemPedidoCompra) {
		this.itemPedidoCompra = itemPedidoCompra;
	}

	public void setNcm(String ncm) {
		this.ncm = ncm;
	}

	public void setNumeroPedidoCompra(String numeroPedidoCompra) {
		this.numeroPedidoCompra = numeroPedidoCompra;
	}

	public void setOutrasDespesasAcessorias(Double outrasDespesasAcessorias) {
		this.outrasDespesasAcessorias = outrasDespesasAcessorias;
	}

	public void setQuantidadeComercial(Integer quantidadeComercial) {
		this.quantidadeComercial = quantidadeComercial;
	}

	public void setQuantidadeTributavel(Integer quantidadeTributavel) {
		this.quantidadeTributavel = quantidadeTributavel;
	}

	public void setUnidadeComercial(String unidadeComercial) {
		this.unidadeComercial = unidadeComercial;
	}

	public void setUnidadeTributavel(String unidadeTributavel) {
		this.unidadeTributavel = unidadeTributavel;
	}

	public void setValorDesconto(Double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public void setValorTotalBruto(Double valorTotalBruto) {
		this.valorTotalBruto = valorTotalBruto;
	}

	public void setValorTotalFrete(Double valorTotalFrete) {
		this.valorTotalFrete = valorTotalFrete;
	}

	public void setValorTotalSeguro(Double valorTotalSeguro) {
		this.valorTotalSeguro = valorTotalSeguro;
	}

	public void setValorUnitarioComercializacao(
			Double valorUnitarioComercializacao) {
		this.valorUnitarioComercializacao = valorUnitarioComercializacao;
	}

	public void setValorUnitarioTributacao(Double valorUnitarioTributacao) {
		this.valorUnitarioTributacao = valorUnitarioTributacao;
	}
}