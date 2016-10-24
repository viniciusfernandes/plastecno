package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(campoIdentificacao = "codigo")
public class ProdutoServicoNFe {
	@XmlElement(name = "CEST")
	@InformacaoValidavel(padrao = "\\d{7}", padraoExemplo = "7 d�gitos", nomeExibicao = "CEST do produtos/servi�os")
	private String cest;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{4}", padraoExemplo = "4 digitos", nomeExibicao = "CFOP do produtos/servi�os")
	@XmlElement(name = "CFOP")
	private String cfop;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 60 }, nomeExibicao = "C�digo do produtos/servi�os")
	@XmlElement(name = "cProd")
	private String codigo;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 120 }, nomeExibicao = "Descri��o do produtos/servi�os")
	@XmlElement(name = "xProd")
	private String descricao;

	// @InformacaoValidavel(obrigatorio = true, tamanhos = { 0, 8, 12, 13, 14 },
	// nomeExibicao = "C�digo EAN ou de barras do produto/servi�o")
	@XmlElement(name = "cEAN")
	private String EAN;

	// @InformacaoValidavel(obrigatorio = true, tamanhos = { 1, 8, 12, 13, 14 },
	// nomeExibicao = "EAN tribut�vel do produtos/servi�os")
	@XmlElement(name = "cEANTrib")
	private String EANTributavel;

	@InformacaoValidavel(intervaloComprimento = { 2, 3 }, nomeExibicao = "EXTIPI do produtos/servi�os")
	@XmlElement(name = "EXTIPI")
	private String EXTIPI;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 0, 1 }, nomeExibicao = "Indicador de composi��o do valor total produtos/servi�os")
	@XmlElement(name = "indTot")
	private Integer indicadorValorTotal;

	@InformacaoValidavel(obrigatorio = true, tamanhos = { 2, 8 }, substituicao = { "\\D", "" }, nomeExibicao = "NCM do produtos/servi�os")
	@XmlElement(name = "NCM")
	private String ncm;

	@XmlElement(name = "xPed")
	@InformacaoValidavel(intervaloComprimento = { 1, 15 }, nomeExibicao = "N�mero de pedido de compra")
	private String numeroPedidoCompra;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Outras despesas acess�rias do produtos/servi�os")
	@XmlElement(name = "vOutro")
	private Double outrasDespesasAcessorias;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,15}\\.{1}\\d{0,4}", padraoExemplo = "0 a 4 decimais", nomeExibicao = "Quantidade comercial do produtos/servi�os")
	@XmlElement(name = "qCom")
	private String quantidadeComercial;

	@XmlElement(name = "qTrib")
	private Integer quantidadeTributavel;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 6 }, nomeExibicao = "Unidade comercial do produtos/servi�os")
	@XmlElement(name = "uCom")
	private String unidadeComercial;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 6 }, nomeExibicao = "Unidade tribut�vel do produtos/servi�os")
	@XmlElement(name = "uTrib")
	private String unidadeTributavel;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total do desconto do produtos/servi�os")
	@XmlElement(name = "vDesc")
	private Double valorDesconto;

	@XmlElement(name = "vProd")
	private Double valorTotalBruto;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total do frete do produtos/servi�os")
	@XmlElement(name = "vFrete")
	private Double valorTotalFrete;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total do seguro do produtos/servi�os")
	@XmlElement(name = "vSeg")
	private Double valorTotalSeguro;

	@InformacaoValidavel(obrigatorio = true, decimal = { 21, 10 }, nomeExibicao = "Valor unit�rio comercial do produtos/servi�os")
	@XmlElement(name = "vUnCom")
	private Double valorUnitarioComercializacao;

	@InformacaoValidavel(obrigatorio = true, decimal = { 21, 10 }, nomeExibicao = "Valor unit�rio tributa��o do produtos/servi�os")
	@XmlElement(name = "vUnTrib")
	private Double valorUnitarioTributacao;

	@XmlTransient
	public String getCest() {
		return cest;
	}

	@XmlTransient
	public String getCfop() {
		return cfop;
	}

	@XmlTransient
	public String getCodigo() {
		return codigo;
	}

	@XmlTransient
	public String getDescricao() {
		return descricao;
	}

	@XmlTransient
	public String getEAN() {
		return EAN;
	}

	@XmlTransient
	public String getEANTributavel() {
		return EANTributavel;
	}

	@XmlTransient
	public String getEXTIPI() {
		return EXTIPI;
	}

	@XmlTransient
	public Integer getIndicadorValorTotal() {
		return indicadorValorTotal;
	}

	@XmlTransient
	public String getNcm() {
		return ncm;
	}

	@XmlTransient
	public String getNumeroPedidoCompra() {
		return numeroPedidoCompra;
	}

	@XmlTransient
	public Double getOutrasDespesasAcessorias() {
		return outrasDespesasAcessorias == null ? 0 : outrasDespesasAcessorias;
	}

	@XmlTransient
	public String getQuantidadeComercial() {
		return quantidadeComercial;
	}

	@XmlTransient
	public Integer getQuantidadeTributavel() {
		return quantidadeTributavel;
	}

	@XmlTransient
	public String getUnidadeComercial() {
		return unidadeComercial;
	}

	@XmlTransient
	public String getUnidadeTributavel() {
		return unidadeTributavel;
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

	@XmlTransient
	public Double getValorUnitarioComercializacao() {
		return valorUnitarioComercializacao;
	}

	@XmlTransient
	public Double getValorUnitarioTributacao() {
		return valorUnitarioTributacao;
	}

	public void setCest(String cest) {
		this.cest = cest;
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

	public void setIndicadorValorTotal(Integer indicadorValorTotal) {
		this.indicadorValorTotal = indicadorValorTotal;
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

	public void setQuantidadeComercial(String quantidadeComercial) {
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

	public void setValorUnitarioComercializacao(Double valorUnitarioComercializacao) {
		this.valorUnitarioComercializacao = valorUnitarioComercializacao;
	}

	public void setValorUnitarioTributacao(Double valorUnitarioTributacao) {
		this.valorUnitarioTributacao = valorUnitarioTributacao;
	}
}