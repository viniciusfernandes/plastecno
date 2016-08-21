package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ProdutoServicoNFe {
	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{4}", nomeExibicao = "CFOP do produtos/serviços")
	@XmlElement(name = "CFOP")
	private String cfop;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 60 }, nomeExibicao = "Código do produtos/serviços")
	@XmlElement(name = "cProd")
	private String codigo;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 120 }, nomeExibicao = "Descrição do produtos/serviços")
	@XmlElement(name = "xProd")
	private String descricao;

	@InformacaoValidavel(obrigatorio = true, tamanhos = { 0, 8, 12, 13, 14 }, nomeExibicao = "Código EAN ou de barras do produto/serviço")
	@XmlElement(name = "cEAN")
	private String EAN;

	@InformacaoValidavel(obrigatorio = true, tamanhos = { 1, 8, 12, 13, 14 }, nomeExibicao = "EAN tributável do produtos/serviços")
	@XmlElement(name = "cEANTrib")
	private String EANTributavel;

	@InformacaoValidavel(intervaloComprimento = { 2, 3 }, nomeExibicao = "EXTIPI do produtos/serviços")
	@XmlElement(name = "EXTIPI")
	private String EXTIPI;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 0, 1 }, nomeExibicao = "Indicador de composição do valor total produtos/serviços")
	@XmlElement(name = "indTot")
	private Integer indicadorValorTotal;

	@XmlElement(name = "nItemPed")
	private Integer itemPedidoCompra;

	@InformacaoValidavel(obrigatorio = true, tamanhos = { 2, 8 }, nomeExibicao = "NCM do produtos/serviços")
	@XmlElement(name = "NCM")
	private String ncm;

	@XmlElement(name = "xPed")
	private String numeroPedidoCompra;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Outras despesas acessórias do produtos/serviços")
	@XmlElement(name = "vOutro")
	private Double outrasDespesasAcessorias;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,15}\\.{1}\\d{0,4}", nomeExibicao = "Quantidade comercial do produtos/serviços")
	@XmlElement(name = "qCom")
	private String quantidadeComercial;

	@XmlElement(name = "qTrib")
	private Integer quantidadeTributavel;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 6 }, nomeExibicao = "Unidade comercial do produtos/serviços")
	@XmlElement(name = "uCom")
	private String unidadeComercial;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 6 }, nomeExibicao = "Unidade tributável do produtos/serviços")
	@XmlElement(name = "uTrib")
	private String unidadeTributavel;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total do desconto do produtos/serviços")
	@XmlElement(name = "vDesc")
	private Double valorDesconto;

	@XmlElement(name = "vProd")
	private Double valorTotalBruto;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total do frete do produtos/serviços")
	@XmlElement(name = "vFrete")
	private Double valorTotalFrete;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor total do seguro do produtos/serviços")
	@XmlElement(name = "vSeg")
	private Double valorTotalSeguro;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,21}\\.{1}\\d{1,10}", nomeExibicao = "Valor unitário comercial do produtos/serviços")
	@XmlElement(name = "vUnCom")
	private String valorUnitarioComercializacao;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,21}\\.{1}\\d{1,10}", nomeExibicao = "Valor unitário tributação do produtos/serviços")
	@XmlElement(name = "vUnTrib")
	private Double valorUnitarioTributacao;

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
	public Integer getItemPedidoCompra() {
		return itemPedidoCompra;
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
	public String getValorUnitarioComercializacao() {
		return valorUnitarioComercializacao;
	}

	public Double getValorUnitarioTributacao() {
		return valorUnitarioTributacao;
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

	public void setValorUnitarioComercializacao(
			String valorUnitarioComercializacao) {
		this.valorUnitarioComercializacao = valorUnitarioComercializacao;
	}

	public void setValorUnitarioTributacao(Double valorUnitarioTributacao) {
		this.valorUnitarioTributacao = valorUnitarioTributacao;
	}
}