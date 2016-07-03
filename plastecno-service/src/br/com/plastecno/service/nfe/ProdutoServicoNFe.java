package br.com.plastecno.service.nfe;

public class ProdutoServicoNFe {
	private String CFOP;
	private String codigo;
	private String descricao;
	private String EAN;
	private String EANTributavel;
	private String EXTIPI;
	private String NCM;
	private Integer outrasDespesasAcessorias;
	private Integer quantidadeComercial;
	private Integer quantidadeTributavel;
	private String unidadeComercial;
	private Double valorDesconto;
	private Double valorTotalBruto;
	private Double valorTotalFrete;
	private Double valorTotalSeguro;
	private Double valorUnitarioComercializacao;
	private Double valorUnitarioTributacao;

	public String getCFOP() {
		return CFOP;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getEAN() {
		return EAN;
	}

	public String getEANTributavel() {
		return EANTributavel;
	}

	public String getEXTIPI() {
		return EXTIPI;
	}

	public String getNCM() {
		return NCM;
	}

	public Integer getOutrasDespesasAcessorias() {
		return outrasDespesasAcessorias;
	}

	public Integer getQuantidadeComercial() {
		return quantidadeComercial;
	}

	public Integer getQuantidadeTributavel() {
		return quantidadeTributavel;
	}

	public String getUnidadeComercial() {
		return unidadeComercial;
	}

	public Double getValorDesconto() {
		return valorDesconto;
	}

	public Double getValorTotalBruto() {
		return valorTotalBruto;
	}

	public Double getValorTotalFrete() {
		return valorTotalFrete;
	}

	public Double getValorTotalSeguro() {
		return valorTotalSeguro;
	}

	public Double getValorUnitarioComercializacao() {
		return valorUnitarioComercializacao;
	}

	public Double getValorUnitarioTributacao() {
		return valorUnitarioTributacao;
	}

	public void setCFOP(String cFOP) {
		CFOP = cFOP;
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

	public void setNCM(String nCM) {
		NCM = nCM;
	}

	public void setOutrasDespesasAcessorias(Integer outrasDespesasAcessorias) {
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