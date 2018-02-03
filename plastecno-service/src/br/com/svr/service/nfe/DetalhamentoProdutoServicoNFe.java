package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(campoIdentificacao = "numeroItem")
@XmlType(propOrder = { "numeroItem", "produtoServicoNFe", "tributosProdutoServico", "informacoesAdicionais" })
public class DetalhamentoProdutoServicoNFe {

	// Campos criado para utilizar a indexacao dos itens da lista no arquivo
	// .jsp
	@XmlTransient
	private Integer indiceItem;

	@InformacaoValidavel(intervaloComprimento = { 1, 500 }, nomeExibicao = "Informações adicionais de produtos/serviços")
	@XmlElement(name = "infAdProd")
	private String informacoesAdicionais;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 990 }, nomeExibicao = "Número de produtos/serviços")
	@XmlAttribute(name = "nItem")
	private Integer numeroItem;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Produto/serviço")
	@XmlElement(name = "prod")
	private ProdutoServicoNFe produtoServicoNFe;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tributos do produtos/serviços")
	@XmlElement(name = "imposto")
	private TributosProdutoServico tributosProdutoServico;

	public boolean contemICMS() {
		return tributosProdutoServico != null && tributosProdutoServico.contemICMS();
	}

	public boolean contemIPI() {
		return tributosProdutoServico != null && tributosProdutoServico.contemIPI();
	}

	@XmlTransient
	public double getAliquotaICMS() {
		if (tributosProdutoServico == null || !tributosProdutoServico.contemICMS()) {
			return 0;
		}
		ICMS icms = tributosProdutoServico.getIcms();
		if (icms == null || icms.getTipoIcms() == null || icms.getTipoIcms().getAliquota() == null) {
			return 0d;
		}
		return icms.getTipoIcms().getAliquota() / 100d;
	}

	@XmlTransient
	public double getAliquotaICMSInterestadual() {
		if (tributosProdutoServico == null || !tributosProdutoServico.contemICMSInterestadual()
				|| tributosProdutoServico.getIcmsInterestadual().getAliquotaInterestadual() == null) {
			return 0;
		}
		return tributosProdutoServico.getIcmsInterestadual().getAliquotaInterestadual() / 100d;
	}

	@XmlTransient
	public double getAliquotaICMSST() {
		if (tributosProdutoServico == null || !tributosProdutoServico.contemICMS()) {
			return 0;
		}
		ICMS icms = tributosProdutoServico.getIcms();
		if (icms == null || icms.getTipoIcms() == null || icms.getTipoIcms().getAliquotaST() == null) {
			return 0d;
		}
		return icms.getTipoIcms().getAliquotaST() / 100d;
	}

	// Devemos fazer esse tratamento do indice do item pois ele esta sendo
	// recuperado na pesquisa pelo numero do pedido e ele nao foi populado no
	// banco de dados pois nao esta no xml
	@XmlTransient
	public Integer getIndiceItem() {
		if (indiceItem == null && numeroItem != null) {
			indiceItem = numeroItem - 1;
		}
		return indiceItem;
	}

	@XmlTransient
	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}

	@XmlTransient
	public Integer getNumeroItem() {
		return numeroItem;
	}

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	@XmlTransient
	public ProdutoServicoNFe getProduto() {
		return produtoServicoNFe;
	}

	@XmlTransient
	public ProdutoServicoNFe getProdutoServicoNFe() {
		return produtoServicoNFe;
	}

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	@XmlTransient
	public TributosProdutoServico getTributos() {
		return tributosProdutoServico;
	}

	@XmlTransient
	public TributosProdutoServico getTributosProdutoServico() {
		return tributosProdutoServico;
	}

	public double getValorBruto() {
		return produtoServicoNFe != null && produtoServicoNFe.getValorTotalBruto() != null ? produtoServicoNFe
				.getValorTotalBruto() : 0d;
	}

	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
		if (numeroItem != null) {
			indiceItem = numeroItem - 1;
		}
	}

	public void setProdutoServicoNFe(ProdutoServicoNFe produtoServicoNFe) {
		this.produtoServicoNFe = produtoServicoNFe;
	}

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	public void setTributos(TributosProdutoServico tributos) {
		setTributosProdutoServico(tributos);
	}

	public void setTributosProdutoServico(TributosProdutoServico tributosProdutoServico) {
		this.tributosProdutoServico = tributosProdutoServico;
	}
}
